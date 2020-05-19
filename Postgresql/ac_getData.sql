DROP FUNCTION mm.ac_getData(date, integer, out timestamp array, out timestamp array, out real array, out smallint array,
out smallint array, out smallint array, out smallint array, out smallint array,
out integer array, out integer array, out varchar array);

CREATE OR REPLACE FUNCTION mm.ac_getData(docDate date, docId integer,
  out dt_arr_arr timestamp array, out dt_dep_arr timestamp array, out comm_km_arr real array,
  out loc_ser_arr smallint array, out qt_full_arr smallint array, out qt_empty_arr smallint array,
  out type_vag_arr smallint array, out w_brutto_arr smallint array, out num_train_arr smallint array,
  out rangea_arr integer array, out rangebfull_arr integer array, out groupname_arr varchar array)
  AS
$BODY$
/*
   Функция считывает в массивы данные (необходимые для расчета расхода электроэнергии или дизельного топлива)
   из больших (тяжелых) таблиц, чтобы не запрашивать данные повторно.

   Входящие переменные:
   1) docDate (date - dd.mm.yyyy) - Отчетная дата ввода и обработки документа (отчетные сутки)
   2) docId (integer) - Идентификатор документа

   Исходящие переменные:
   -- Данные из таблицы m2_sched - таблица расписаний для поездов из маршрута машиниста
   1) dt_arr_arr - массив с датами прибытия
   2) dt_dep_arr - массив с датами отправления
   3) comm_km_arr - массив пройденный путь в км
   4) qt_full_arr - массив количество груженных вагонов
   5) qt_empty_arr - массив количество порожних вагонов
   6) type_vag_arr - массив код учетного рода вагонов
   7) w_brutto_arr - массив масса бруто в тоннах по остановочным пунктам
   8) num_train_arr - массив номер поезда по остановочным пунктам
   9) rangea_arr - массив начала номеров диапазона категорий поездов
   10) rangebfull_arr - массив конца диапазона категорий поездов
   11) groupname_arr - массив наименований диапазона категорий поездов

   -- Данные из таблицы m2_locsec - таблица сведений о секциях локомотивов по маршруту
   4) loc_ser_arr - массив коды серий ТПС

 */
DECLARE
    -- Курсор по таблице расписаний по маршрутам машиниста
    rec_m2_sched record;
    cur_m2_sched cursor(docDate date, docId integer)
		 for select nord, dt_arr, dt_dep, comm_km, w_brutto, num_train
		 from mm.m2_sched where (doc_date = docDate) and (doc_id = docId)
         order by nord;

    -- Курсор по таблице сведений о секциях локомотивов по маршруту
    rec_m2_locsec record;
    cur_m2_locsec cursor(docDate date, docId integer)
		 for select loc_ser
         from mm.m2_locsec where (doc_date = docDate) and (doc_id = docId);

    -- Курсор по таблице сведений о группах вагонов в составах поездов по маршруту
    rec_m2_sostav_groupvag record;
    cur_m2_sostav_groupvag cursor(docDate date, docId integer)
		 for select qt_full, qt_empty, type_vag
         from mm.m2_sostav_groupvag where (doc_date = docDate) and (doc_id = docId);

    -- Курсор по таблице сведений о категорий поездов
    rec_categorytrainsdetail record;
    cur_categorytrainsdetail cursor
		 for select rangea, rangebfull, groupname
         from nsi.categorytrainsdetail order by rangea;

    index integer; -- служебная переменная
BEGIN
    /*
      Инициализируем массивы на основе таблицы расписаний по маршруту
     */
    open cur_m2_sched(docDate, docId);
    loop
        fetch cur_m2_sched into rec_m2_sched;
        exit when not found;
        dt_arr_arr := array_append(dt_arr_arr, rec_m2_sched.dt_arr);
        dt_dep_arr := array_append(dt_dep_arr, rec_m2_sched.dt_dep);
        comm_km_arr := array_append(comm_km_arr, rec_m2_sched.comm_km);
        w_brutto_arr := array_append(w_brutto_arr, rec_m2_sched.w_brutto);
        num_train_arr := array_append(num_train_arr, rec_m2_sched.num_train);
    end loop;
    close cur_m2_sched;

    -- Проверяем что таблица расписаний по маршруту не пуста
    if (array_length(comm_km_arr, 1) = 0) then
        raise exception 'Не найдено записей в таблице расписаний по маршрут: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
/*
    -- Проверяем что номер поезда не менялся
    index := count(DISTINCT n.num_train) from (select unnest(num_train_arr) as num_train) n;
    raise info 'index=%', index;

    if (index > 1) or (num_train_arr[1] is null) or (num_train_arr[1] = 0) then
        raise exception 'Номер поезда изменился (невозможна классификация поезда грузовой, пассажирский и прочее) по маршрут: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
*/


    /*
      Инициализируем массивы на основе таблицы сведений о группах вагонов в составах поездов по маршруту
     */
    open cur_m2_locsec(docDate, docId);
    loop
        fetch cur_m2_locsec into rec_m2_locsec;
        exit when not found;
        loc_ser_arr := array_append(loc_ser_arr, rec_m2_locsec.loc_ser);
    end loop;
    close cur_m2_locsec;

    -- Проверяем что таблица сведений о группах вагонов в составах поездов по маршруту не пуста
    if (array_length(loc_ser_arr, 1) = 0) then
        raise exception 'Не найдено записей в таблице сведений о группах вагонов в составах поездов по маршруту по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

    /*
      Инициализируем массив на основе таблицы сведений о секциях локомотивов по маршруту
     */
    open cur_m2_sostav_groupvag(docDate, docId);
    loop
        fetch cur_m2_sostav_groupvag into rec_m2_sostav_groupvag;
        exit when not found;
        qt_full_arr := array_append(qt_full_arr, rec_m2_sostav_groupvag.qt_full);
        qt_empty_arr := array_append(qt_empty_arr, rec_m2_sostav_groupvag.qt_empty);
        type_vag_arr := array_append(type_vag_arr, rec_m2_sostav_groupvag.type_vag);
    end loop;
    close cur_m2_sostav_groupvag;

    -- Проверяем что таблица сведений о секциях локомотивов по маршруту не пуста
    if (array_length(type_vag_arr, 1) = 0) then
        raise exception 'Не найдено записей в таблице сведений о секциях локомотивов по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

     /*
      Инициализируем массив таблице сведений о категорий поездов
     */
    open cur_categorytrainsdetail;
    loop
        fetch cur_categorytrainsdetail into rec_categorytrainsdetail;
        exit when not found;
        rangea_arr := array_append(rangea_arr, rec_categorytrainsdetail.rangea);
        rangebfull_arr := array_append(rangebfull_arr, rec_categorytrainsdetail.rangebfull);
        groupname_arr := array_append(groupname_arr, rec_categorytrainsdetail.groupname);
    end loop;
    close cur_categorytrainsdetail;

    -- Проверяем что таблица сведений о секциях локомотивов по маршруту не пуста
    if (array_length(groupname_arr, 1) = 0) then
        raise exception 'Не найдено записей в таблице сведений о категорий поездов';
    end if;

END
$BODY$
LANGUAGE 'plpgsql';



