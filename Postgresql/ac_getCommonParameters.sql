DROP FUNCTION mm.ac_getCommonParameters(date, integer, timestamp array, timestamp array, real array, smallint array,
smallint array, smallint array, smallint array,
out integer, out integer, out real, out integer, out integer);

CREATE OR REPLACE FUNCTION mm.ac_getCommonParameters(docDate date, docId integer,
    dt_arr_arr timestamp array, dt_dep_arr timestamp array,
    comm_km_arr real array, loc_ser_arr smallint array,
    qt_full_arr smallint array, qt_empty_arr smallint array, type_vag_arr smallint array,
    out timeDriving integer, out parkingTimeMin integer, out parkingCount integer, out lengthRouteKm real,
    out countAxis integer, out countSections integer, out typeLocomotive smallint, out weightLocomotive real,
    out kpdLocomotive real, out powerLocomotiveMotion real, out powerLocomotiveParking real,
    out lengthTrain real)
  AS
$BODY$
/*
   Функция возвращает начальные общие для расчета данные по маршруту машиниста и нитки графика,
   необходимые для расчета расхода электроэнергии или дизельного топлива

   Входящие переменные:
   1) docDate (date - dd.mm.yyyy) - Отчетная дата ввода и обработки документа (отчетные сутки)
   2) docId (integer) - Идентификатор документа
   3) dt_arr_arr - массив с датами прибытия
   4) dt_dep_arr - массив с датами отправления
   5) comm_km_arr - пройденный путь в км
   6) loc_ser_arr - массив коды серий ТПС
   7) qt_full_arr - массив количество груженных вагонов
   8) qt_empty_arr - массив количество порожних вагонов
   9) type_vag_arr - массив код учетного рода вагонов

   Исходящие переменные:
   1) timeDriving - общее время движения в минутах по поездке в маршруте машиниста
   2) parkingTimeMin - общее время стоянок в минутах по поездке в маршруте машиниста
   3) parkingCount - число остановок на промежуточных остановочных пунктах
      (в подсчет идут только остановки на станциях по маршруту следования, а так же на разъездах и блок-постах)
   4) lengthRouteKm - длина участка всей поездки в километрах
   5) countAxis - количество осей в составе
   6) countSections - количество секций электровоза
   7) typeLocomotive - тип локомотива (1 - электровоз, 2 - тепловоз, 3 - электропоезд, 4 - дизель-поезд и автомотрис (АМВПС))
   8) weightLocomotive - вес локомотива в тоннах
   9) kpdLocomotive - КПД локомотива
   10) powerLocomotiveMotion - мощность локомотива в движении в кВт
   11) powerLocomotiveParking - мощность локомотива на стоянке в кВт
   12) lengthTrain - длина поезда (состава) в метрах

 */
DECLARE
    timeStart timestamp; -- время начала движения
    timeStop timestamp; -- время завершения движения
    lengthLocomotive real; -- длина локомотива в метрах
    lengthVagons real; -- длина состава вагонов в метрах
    axisLocomotive smallint; -- количество осей в локомотиве
    countAxelVagons smallint; -- количество осей во всех вагонах
BEGIN
    /*
     Получаем length_route_km - длина участка всей поездки в километрах
     */
    lengthRouteKm := max(x) from unnest(comm_km_arr) x;
    if (lengthRouteKm is null) or (lengthRouteKm = 0) then
        raise exception 'Длина участка всей поездки не определена по маршрут: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

    /*
     Получаем time_driving - время движения машиниста по маршруту
     */
    timeStart := min(x) from unnest(dt_dep_arr) x;
    timeStop := max(x) from unnest(dt_arr_arr) x;
    timeDriving := extract(epoch from (timeStop - timeStart))/60;
    if (timeDriving is null) or (timeDriving = 0) then
        raise exception 'Не определено время движения машиниста по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

    /*
     Получаем parking_time_min - общее время стоянок в минутах по поездке в маршруте машиниста
     */
    with t as (select unnest(dt_dep_arr) as dt_dep, unnest(dt_arr_arr) as dt_arr)
    select sum(extract(epoch from (dt_dep - dt_arr))/60) into parkingTimeMin
    from t where (dt_dep != timeStart) and (dt_arr != timeStop);
    if (parkingTimeMin is null) then parkingTimeMin := 0; end if;

    /*
     Получаем parking_count - число остановок на промежуточных остановочных пунктах
     */
    with t as (select unnest(dt_dep_arr) as dt_dep, unnest(dt_arr_arr) as dt_arr)
    select count(*) into parkingCount
    from t where (dt_dep != timeStart) and (dt_arr != timeStop) and (dt_dep !=dt_arr);
    if (parkingCount is null) then parkingCount := 0; end if;

    /*
     Получаем countSections - количество секций электровоза
     */
    with t as (select unnest(loc_ser_arr) as loc_ser)
    select count(*) into countSections from t;
    if (countSections is null) then
        raise exception 'Не определено количество секций электровоза по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

    /*
     Получаем typeLocomotive - тип локомотива (1 - электровоз, 2 - тепловоз, 3 - электропоезд, 4 - дизель-поезд и автомотрис (АМВПС))
     Получаем weightLocomotive - вес локомотива в тоннах
     Получаем kpdLocomotive - КПД локомотива
     Получаем powerLocomotiveMotion - мощность локомотива в движении в кВт
     Получаем powerLocomotiveParking - мощность локомотива на стоянке в кВт
     Получаем lengthLocomotive - длина локомотива в метрах
     */
    select loc_ser.weight * countSections, loc_ser.length * countSections, loc_ser.kpdloc,
           loc_ser.p_support * countSections, loc_ser.p_supportstopped * countSections,
           loc_ser.naxis * countSections,
           case
               when strpos(lower(loc_type.type_name), 'электровоз') > 0 then 1
               when strpos(lower(loc_type.type_name),'тепловоз') > 0 then 2
               when strpos(lower(loc_type.type_name),'электропоезд') > 0 then 3
               else 4
           end
    from nsi.loc_ser loc_ser, nsi.loc_type loc_type
    where (loc_ser.num_ser = loc_ser_arr[1]) and (loc_ser.code_typeloc = loc_type.code) -- and (loc_ser.flag_correct = 2) 2 - данные для арифметического расчета
    into weightLocomotive, lengthLocomotive, kpdLocomotive, powerLocomotiveMotion, powerLocomotiveParking, axisLocomotive, typeLocomotive;

    if (weightLocomotive is null) or (weightLocomotive = 0) then
        raise exception 'Не определен вес локомотива в тоннах по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
    if (lengthLocomotive is null) or (lengthLocomotive = 0) then
        raise exception 'Не определена длина локомотива в метрах по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
    if (kpdLocomotive is null) or (kpdLocomotive = 0) then
        raise exception 'Не определено КПД локомотива по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
    if (powerLocomotiveMotion is null) or (powerLocomotiveMotion = 0) then
        raise exception 'Не определена мощность локомотива в движении в кВт по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
    if (powerLocomotiveParking is null) or (powerLocomotiveParking = 0) then
        raise exception 'Не определеан мощность локомотива на стоянке в кВт по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
    if (typeLocomotive is null) or (typeLocomotive = 0) then
        raise exception 'Не определен тип локомотива по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
    if (axisLocomotive is null) or (axisLocomotive = 0) then
        raise exception 'Не определено количество осей локомотива по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

    -- Получаем lengthVagons - длина состава вагонов в метрах
    -- Получаем countAxelVagons - количество осей во всех вагонах
    with t as (select unnest(qt_full_arr) as qt_full, unnest(qt_empty_arr) as qt_empty, unnest(type_vag_arr) as type_vag)
    select sum(vt.num_axel * (sg.qt_full + sg.qt_empty)),
           sum(vt.length * (sg.qt_full + sg.qt_empty))
    from t sg, mm.ci_type_vagon ctv
    left join nsi.vag_type vt on (ctv.id_vag_property = vt.id)
    where (sg.type_vag = ctv.code)
    into countAxelVagons, lengthVagons;

    if (countAxelVagons is null) or (countAxelVagons = 0) then
        raise exception 'Не определено количество осей во всех вагонах состава по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;
    if (lengthVagons is null) or (lengthVagons = 0) then
        raise exception 'Не определена длина состава вагонов в метрах по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

    /*
     Получаем count_axis - количество осей в составе
     */
    countAxis := axisLocomotive + countAxelVagons;
    if (countAxis is null) or (countAxis = 0) then
        raise exception 'Не определено количество осей в составе по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

    /*
     Получаем lengthTrain -- длина поезда (состава) в метрах
     */
    lengthTrain := lengthLocomotive + lengthVagons;
    if (lengthTrain is null) or (lengthTrain = 0) then
        raise exception 'Не определена длина поезда по маршруту: Отчетная дата: %, Идентификатор документа: %', docDate, docId;
    end if;

END
$BODY$
LANGUAGE 'plpgsql';



