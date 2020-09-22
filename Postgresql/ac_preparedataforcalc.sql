DROP FUNCTION IF EXISTS mm.ac_preparedataforcalc(smallint, date, integer);

CREATE OR REPLACE FUNCTION mm.ac_preparedataforcalc(typeData smallint, docDate date, docId integer)
RETURNS TABLE (ord bigint, rowComment text, val text)
AS
$BODY$
/*
   Функция запрашивает данные, необходимые для алгоритмического расчета расхода электроэнергии или дизельного топлива.

   Входящие переменные:
   1)  v_typeData - тип данных -> config.data_name.id_data;
   2) docDate (date - dd.mm.yyyy) - Отчетная дата ввода и обработки документа (отчетные сутки)
   3) docId (integer) - Идентификатор документа

   Исходящие данные:
   Таблица состаящая из трех колонок:
   1) ord - порядковый номер
   2) rowComment - название таблицы из базы данных из которой запрашиваются данные
   3) val - значения данных, полученные из базы

   Исходящие переменные (расшифровка колонки val):

   Данные из таблицы m2_sched - таблица расписаний для поездов из маршрута машиниста
   [ nord ; dt_arr ; dt_dep ; comm_km ; w_brutto ; num_train ; num_sostav ; st_id ]
   1) nord - номер по порядку
   2) dt_arr - дата прибытия
   3) dt_dep - дата отправления
   4) comm_km - пройденный путь в км
   5) w_brutto - масса бруто в тоннах по остановочным пунктам
   6) w_netto -  масса нетто в тоннах по остановочным пунктам
   7) num_train - номер поезда по остановочным пунктам
   8) num_sostav - номер состава по остановочным пунктам
   9) st_id - id станции

   Данные из таблицы mm.m2_sostav_groupvag - таблица сведений о группах вагонов в составах поездов по маршруту
   [ qt_full ; qt_empty ; type_vag ; num_sostav ]
   1) qt_full - количество груженных вагонов
   2) qt_empty - количество порожних вагонов
   3) type_vag - код учетного рода вагонов
   4) num_sostav - номер состава по перегону по таблице m2_sostav_groupvag

   Данные из таблицы nsi.categorytrainsdetail - Нумерация поездов, утверждён распоряжением
   [ rangea ; rangebfull ; groupname ]
   1) rangea - начало номеров диапазона категорий поездов
   2) rangebfull - конец диапазона категорий поездов
   3) groupname - наименование диапазона категорий поездов
   4) acceleration - среднее разгонное ускорение
   5) slowdown - расчётное время замедления поезда
   6) typeTrain - Тип поезда 0 - грузовые поезда, 1 - пассажирские поезда, 2 - локомотивы

   Данные из таблицы mm.m2_locsec - таблица сведений о секциях локомотивов по маршруту
   [ loc_ser ; rec_sched1 ; rec_sched2 ]
   1) loc_ser - код серий ТПС
   2) rec_sched1 - порядковый номер начальной строки расписания по маршруту
   3) rec_sched2 - порядковый номер конечной строки расписания по маршруту
   4) loc_num - номер локомотива
   5) dt_cp_out - дата и время выхода секции из депо с бригадой
   6) dt_receive_end - дата и время окончания приемки локомотива
   7) dt_handover_begin - дата и время начала сдачи локомотива
   8) dt_cp_in - дата и время захода секции в депо после поездки
   9) pr_handover - признак сдачи локомотива в депо или на станции
   10) pr_receive - признак приемки в депо или на станции
   11) pr_mmvps_espul - номер секции

   Данные из таблицы mm.ci_type_vagon - Справочник "Код учетного рода вагона" для маршрутов машинистов от ЦОММ
   [ code_tv ; id_vag_property ]
   1) code_tv - код учетного рода вагонов
   2) id_vag_property - id строки свойств вагона -> nsi.vag_type.id

   Данные из таблицы nsi.vag_type - Справочник "Код учетного рода вагона" для маршрутов машинистов от АС Энергосправка
   [ id ; code_resist_alg ; num_axel ; weight ; length ]
   1) id - код учетного рода вагонов
   2) code_resist_alg - ссылка на коэффиценты сопротивления
   3) num_axel - количество осей у вагона
   4) weight - масса вагона, тонна
   5) length - длинна вагона, метры

   Данные из таблицы nsi.vag_resist_alg - справочник коэф. основного удельного сопротивления вагонов для алг. расчета
   [ code_resist_alg ; rail_type ; a0 ; a1 ; a2 ; a3 ]
   1) code_resist_alg - код общий для типа вагона с учетом вариантов типа пути и загрузки
   2) rail_type - тип пути (0-звеньевой, 1-бесстыковой)
   3) a0 - постоянный коэф., Н/т
   4) a1 - постоянный коэф. зависящий от нагрузки на ось, Н/т
   5) a2 - коэф. зависящий от скорости, Н/т
   6) a3 - коэф. зависящий от квадрата скорости, Н/т

   Данные из таблицы nsi.ways - таблица путей по ж/д участкам
   [ nord ; rail_type ; id ]
   1) nord - номер по порядку из таблицы m2_sched
   2) rail_type - тип пути (0-звеньевой, 1-бесстыковой)
   3) id - участка

   Данные из таблицы nsi.loc_ser - таблица свойств серий ТПС (значения приведены на секцию)
   [ num_ser ; weight ; length ; id_koefresists ; id_koef_poly_tract; kpdloc ; kpdrazgon ; koefstartv1 ; koefstartv2 ; loc_type ]
   1) num_ser - код серии ТПС
   2) weight - вес одной секции
   3) length - длина одной секции
   4) id_koefresists - nsi.loc_resist.id : id строки с коэф.основного уд. сопротивления движению локомотива
   5) kpdloc - КПД локомотива
   6) kpdrazgon - коэффициент при КПД локомотива в режиме разгона
   7) koefstartv1 - коэффициент пуска для тех.скорости меньше 30 км/ч
   8) koefstartv2 - коэффициент пуска для тех.скорости больше 30 км/ч
   9 koefaadditionalloss - коэффициент, определяющий дополнительные потери электроэнергии, связанные с движением на
      маневренной позиции контроллера машиниста
   10) loc_type - тип локомотива (1 - электровоз, 2 - тепловоз, 3 - электропоезд (МВПС), 4 - дизель-поезд и автомотрис (АМВПС))
   11) type_tok - тип тока (1 - постоянный, 2 - переменный)
   12) p_support - Номинальная мощность на вспомогательные нужды на секцию
   13) p_supportstopped - Номинальная мощность на вспомогательные нужды на секцию при стоянке
   14) is_ep20 - Имя локомотива ЭП20 (1-Да, 2-Нет)
   15) is_2ec4k_3ec4k_vl80 - Имя локомотива ВЛ80, 2ЭС4К, 3ЭС4К (1-Да, 2-Нет)
   16) id_loc_power_consumption - ссылка на id таблицы nsi.loc_power_consumption
   17) id_loc_fuel_consumption - ссылка на id таблицы nsi.loc_fuel_consumption
   18) id_koef_mvps_resist - ссылка на id таблицы nsi.mvps_resist
   19) minsectioncount - мин.возможное кол-во секций

    Данные профиля берем из e2_preparedataforcalc (nsi.way_profile - Справочник коэф. основного удельного сопротивления вагонов для алг. расчета)
   [ x ; len ; grad ]
   1) x - Начало элемента
   2) len - длина
   3) grad - градусы

   Данные из таблицы nsi.way_profile - Справочник коэф. основного удельного сопротивления вагонов для алг. расчета
   [ way_id ; len ; grad ]
   1) way_id - id участка
   2) len - длина
   3) grad - градусы
   4) x - Начало элемента

   Данные из таблицы nsi.loc_resist - справочник коэф. основного уд. сопротивления движению локомотивов
   [ id ; ldefault ; a0 ; a1 ; a2 ; a1_z ; a2_z ]
   1) id - id
   2) ldefault - Данные по умолчанию, используются для типов локомотивов у которых не указана строка данных
   3) a0 - постоянный коэф. основного уд. сопротивления движению локомотива
   4) a1 - безстыковой путь - коэф.влияния скорости
   5) a2 - безстыковой путь - коэф.влияния квадрата скорости
   6) a1_z - звеньевой путь - коэф.влияния скорости
   7) a2_z - звеньевой путь - коэф.влияния квадрата скорости

    Данные из таблицы nsi.loc_ser_koef_polynom - коэффициенты при степенях 4,3,2,1 и 0 полиномиальной аппроксимации КПД тяги и
    рекуперации от касательной мощности P, МВт (5 чисел) и интервал этой аппроксимации
   [ id ; a4 ; a3 ; a2 ; a1 ; a0 ; p_min ; p_max ]
   1) num_ser - id серии локомотива
   2) qt_sec - количество секций
   3) a4 - коэффициенты при степени 4
   4) a3 - коэффициенты при степени 3
   5) a2 - коэффициенты при степени 2
   6) a1 - коэффициенты при степени 1
   7) a0 - коэффициенты при степени 0
   8) p_min - мин.мощность
   9) p_max - макс.мощность

    Данные из таблицы nsi.loc_koef_kpd_diff_speed - КПД для электропоездов постоянного, переменного тока
    при различных скоростях движения, КПД АМВПС при различных скоростях движения
   [id ; v ; kpd]
   1) id - ID
   2) v - скорость, км./ч.
   3) kpd - КПД, %

    Данные из таблицы mm.e2_preparedataforcalc - значение максимально возможной скорости на участках профиля 'lim'
   [xStart ; xStop ; maxSpeed]
   1) xStart - в метрах
   2) xStop -  в метрах
   3) vMax - значение максимально возможной скорости км./ч.

    Данные из таблицы mm.e2_preparedataforcalc - профиль 'prof'
   [x ; len ; grad]
   1) x - Начало элемента
   2) len - длина
   3) grad - градусы

    Данные из таблицы m2_trains - Сведения о поездках в маршруте машиниста.
   [ nord ; rec_begin ; rec_end ; dt_begin ; dt_end ; time_all ; time_stop ; run ; qt_stop ; time_nagon ; num_train]
   1) nord - номер по порядку
   2) rec_begin - строка начала поездки в расписании маршрута -> mm.m2_sched.nord
   3) rec_end - строка окончания поездки в расписании маршрута -> mm.m2_sched.nord
   4) dt_begin - дата прибытия
   5) dt_end - дата отправления
   6) time_all - продолжительность всей поездки, сек.
   7) time_stop - продолжительность остановок по поездке, сек.
   8) run - общий пробег по поездке, м
   9) qt_stop - кол-во остановок по поездке
   10) time_nagon - общее время нагона, сек.
   11) num_train - номер поезда
   12) time_manevr - продолжительность поездных маневров, сек.
   13) w_brutto - вес поезда брутто, т
   14) w_netto - вес поезда нетто, т
   15) st_id_begin - ствнция начала поездки
   16) st_id_end - станция конца поездки

    Данные из таблицы nsi.loc_power_consumption - Удельный расход электроэнергии электропоездами на собственные нужды и вентиляцию
   [ id ; kol_vagon ; r1 ; r2 ; r3 ; w_train ; w_brutto ]
   1) id - ID
   2) kol_vagon - количество вагонов
   3) r1 - расход э.э. на работу вспомогательных машин и оборудования МВПС, кВт.ч./мин.
   4) r2 - расход э.э. на работу вспомогательных машин и оборудования МВПС, кВт.ч./ч.
   5) r3 - расход э.э. на вентиляцию, кВт.ч./ч.
   6) num_traun_start начало номеров поездов
   7) num_train_finish - конец номеров поездов
   8) w_train_1 - Масса одного вагона г.мот
   9) w_train_2 - Масса одного вагона г.приц
   10) w_train_3 - Масса одного вагона мот
   11) w_train_4 - Масса одного вагона приц

    Данные из таблицы nsi.loc_fuel_consumption - Удельный расход топлива АМВПС на холостом ходу
   [ id ; r1 ; r2 ; r3 ; r4 ; n ; w_netto ; w_brutto ]
   1) id - ID
   2) r1 - Удельный расход топлива одной секции АМВПС кг/мин
   3) r2 - Удельный расход топлива одной секции АМВПС кг/ч
   4) r3 - Удельный расход топлива в целом АМВПС кг/мин
   5) r3 - Удельный расход топлива в целом АМВПС кг/ч
   6) n - номинальная касательная мощность, кВт
   7) w_netto - масса поезда нетто
   8) w_brutto - масса поезда брутто

    Данные из таблицы mm.m2_sched_manevr - Таблица маневровой работы по маршрутам машиниста
   [ nord_shed ; nord ; dt_begin ; dt_end ]
   1) nord_shed - строка поездки к которой относятся данные -> mm.m2_sched.nord
   2) nord - порядковый номер строки данных в маршруте
   3) dt_begin - время начала работы
   4) dt_end - время окончания работы

    Данные из таблицы mm.m2_sched_stoppg - Таблица сведений об остановках и задержках на перегонах по маршруту
   [ nord_shed ; dur_manevr ; dur_stop ]
   1) nord_shed - строка поездки к которой относятся данные -> mm.m2_sched.nord
   2) dur_manevr - продолжительность поездных маневров, мин.
   3) dur_stop - Продолжительность задержки на перегоне, мин.

    Данные из таблицы mm.m2_loctg - Таблица сведений о тяговых единицах по маршруту
   [ dt_begin ; dt_end ; pr_tg_double ]
   1) dt_begin - дата и время начала работы (режима)
   2) dt_end - дата и время окончания работы (режима)
   3) pr_tg_double - признак наличия двойной тяги

    Данные из таблицы mm.gr_sched - таблица заголовков ниток графиков движения (норм., прогнозный)
   [ nord ; num_train ; dt_arr ; dt_dep ; st_id ; xline ]
   1) nord - порядковый номер точки в нитке
   2) num_train - номер нитки в точке
   3) dt_arr - дата и время прибытия
   4) dt_dep - дата и время отправления
   5) st_id - ID станции ->  nsi.stations.id
   6) xline - расстояние точки от начальной точки нитки, м

    Данные из таблицы nsi.mvps_resist - Значения коэффициентов, входящих в выражение для расчета основного удельного сопротивления движению
   [ id ; a ; b ; c ; a_z ; b_z ; c_z ]
   1) id - id
   2) kol_vagon - количество вагонов
   3) a - коэффициент бесстыковой a
   4) b - коэффициент бесстыковой b
   5) c - коэффициент бесстыковой c
   6) a_z - коэффициент звеньевой a_z
   7) b_z - коэффициент звеньевой b_z
   8) c_z - коэффициент звеньевой c_z

 */
DECLARE
BEGIN
  RETURN QUERY
    with
      t0 as (
          select p.f_ord, p.f_rowcomment, p.f_val
          from mm.e2_preparedataforcalc(typeData, docDate, docId) p
          where (p.f_rowcomment='lim_2') or (p.f_rowcomment='prof')
      ),
      t1 as (
        select 'mm.m2_sched' as rowComment, nord, dt_arr, dt_dep, comm_km, w_brutto, w_netto, num_train,
               num_sostav, st_id
        from mm.m2_sched
        where (doc_date = docDate) and (doc_id = docId)),
      t2 as (
        select 'mm.m2_sostav_groupvag' as rowComment, qt_full, qt_empty, type_vag, num_sostav
        from mm.m2_sostav_groupvag where (doc_date = docDate) and (doc_id = docId)),
      t3 as (
        select 'mm.m2_locsec' as rowComment, loc_ser, rec_sched1, rec_sched2, loc_num,
               dt_cp_out, dt_receive_end, dt_handover_begin, dt_cp_in, pr_handover, pr_receive, pr_mvps_espul
        from mm.m2_locsec
        where (doc_date = docDate) and (doc_id = docId)),
      t4 as (
        select 'mm.ci_type_vagon' as rowComment, code, id_vag_property
        from mm.ci_type_vagon
        where code in (select t2.type_vag from t2)),
      t5 as (
        select 'nsi.vag_type' as rowComment, id, code_resist_alg, num_axel, weight, length
        from nsi.vag_type
        where id in (select t4.id_vag_property from t4)),
      t6 as (
        select 'nsi.vag_resist_alg' as rowComment, code_resist_alg, rail_type, a0, a1, a2, a3
        from nsi.vag_resist_alg
        where code_resist_alg in (select t5.code_resist_alg from t5)),
      t7 as (
        select distinct 'nsi.categorytrainsdetail' as rowComment, ctd.rangea, ctd.rangebfull, ctd.groupname,
          p.acceleration, p.slowdown, p.typetrain
        from nsi.categorytrainsdetail ctd, t1, mm.ac_preparedataforcalconeparams(ctd.groupname, t1.w_brutto) p
        where (ctd.rangea <= t1.num_train) and (ctd.rangeb >= t1.num_train)),
      -- TODO: Проверить что есть бесстыковочные пути, 03.07.2020 не смог найти таких маршрутов
      t8 as (
        select distinct 'nsi.ways' as rowComment, tOne.nord, w.rail_type, w.id
        from t1 tOne
        left join nsi.path_abinfo pai on (pai.stida = tOne.st_id) and (pai.stidb in (select tTwo.st_id from t1 tTwo where tTwo.nord = tOne.nord +1))
        left join nsi.path_ab pa on (pa.idfind = pai.id) and (pa.stidb = pai.stidb)
        left join nsi.ways w on (pa.id_way = w.id)),
      -- TODO: С типом локомотивов, нужно более точно в базе определиться, так как один состав в зависимости от типа секций
      -- на одном маршруте может быть например как электровозом, так и тепловозом?
      t9 as (
        select 'nsi.loc_ser' as rowComment, ls.num_ser, ls.weight, ls.length, ls.id_koefresists,
          ls.kpdloc, ls.kpdrazgon, ls.koefstartv1, ls.koefstartv2, ls.id_koef_kpd_diff_speed,
          ls.koefaadditionalloss,
          case
            when strpos(lower(lt.type_name), 'электровоз') > 0 then 1
            when strpos(lower(lt.type_name),'тепловоз') > 0 then 2
            when strpos(lower(lt.type_name),'электропоезд') > 0 then 3
            else 4
          end as loc_type,
          case
            when strpos(lower(lt.type_name), 'постоянн') > 0 then 1
            when strpos(lower(lt.type_name),'переменн') > 0 then 2
          end as type_tok,
          ls.p_support, ls.p_supportstopped,
          case ls.name_ser='ЭП20' when true then 1 else 0 end as is_ep20,
          case ls.name_ser in ('ВЛ80', '3ЭС4К', '3ЭС4К') when true then 1 else 0 end as is_2ec4k_3ec4k_vl80,
          id_loc_power_consumption, id_loc_fuel_consumption, id_koef_mvps_resist,
          minsectioncount
        from nsi.loc_ser ls, nsi.loc_type lt
        where (ls.num_ser in (select t3.loc_ser::integer from t3)) and (ls.code_typeloc = lt.code)),
      t10 as (
        select 'nsi.loc_koef_kpd_diff_speed' as rowComment, lk.id, lk.v, lk.kpd
        from nsi.loc_koef_kpd_diff_speed lk
        where lk.id in (select t9.id_koef_kpd_diff_speed from t9)),
      t11 as (
        select 'nsi.loc_resist' as rowComment, id, ldefault, a0, a1, a2, a1_z, a2_z
        from nsi.loc_resist k),
      t12 as (
        select distinct 'nsi.loc_ser_koef_polynom' as rowComment, k.num_ser, k.qt_sec, k.a4, k.a3, k.a2, k.a1, k.a0, k.p_min, k.p_max
        from nsi.loc_ser_koef_polynom k, t9
        where (k.num_ser in (select t9.num_ser from t9)) and (k.type_koef = 1::smallint)),
      t13 as (
        select 'e2_preparedataforcalc.lim' as rowComment, t0.f_val as f_val
        from t0
        where (t0.f_rowcomment='lim_2')
      ),
      t14 as (
        select 'mm.m2_trains' as rowComment, nord, rec_begin, rec_end, dt_begin, dt_end, time_all, time_stop,
               run, qt_stop, time_nagon, num_train, time_manevr, w_brutto, w_netto, st_id_begin, st_id_end
        from mm.m2_trains
        where (doc_date = docDate) and (doc_id = docId)
        order by doc_date, doc_id, nord
      ),
      t15 as (
        select 'nsi.loc_power_consumption' as rowComment, id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish,
               w_train_1, w_train_2, w_train_3, w_train_4
        from nsi.loc_power_consumption
        where id in (select id_loc_power_consumption from t9)
      ),
      t16 as (
        select 'nsi.loc_fuel_consumption' as rowComment, id, r1, r2, r3, r4, n, w_netto, w_brutto
        from nsi.loc_fuel_consumption
        where id in (select id_loc_fuel_consumption from t9)
      ),
      t17 as (
        select 'mm.m2_sched_manevr' as rowComment, nord_shed, nord, dt_begin, dt_end
        from mm.m2_sched_manevr
        where (doc_date = docDate) and (doc_id = docId)
      ),
      t18 as (
        select 'mm.m2_sched_stoppg' as rowComment, nord_shed, dur_manevr, dur_stop
        from mm.m2_sched_stoppg
        where (doc_date = docDate) and (doc_id = docId)
      ),
      t19 as (
        select 'mm.m2_loctg' as rowComment, dt_begin, dt_end, pr_tg_double
        from mm.m2_loctg
        where (doc_date = docDate) and (doc_id = docId) and (pr_tg_double=true)
      ),
      t20 as (
        select 'e2_preparedataforcalc.prof' as rowComment, t0.f_val as f_val
        from t0
        where (t0.f_rowComment = 'prof') and (substring(t0.f_val,1,1) != '-')
      ),
      t21 as (
        select 'mm.gr_sched' as rowComment, gs.nord, gs.num_train, gs.dt_arr, gs.dt_dep, gs.st_id, gs.xline
        from mm.gr_sched gs, mm.m2_trains_graphics tg
        where (gs.th_id=tg.th_id) and (tg.doc_date = docDate) and (tg.doc_id = docId) and
              (tg.type_gr = 102)
      ),
      t22 as (
        select 'nsi.mvps_resist' as rowComment, mr.id, mr.kol_vagon, mr.a, mr.b, mr.c, mr.a_z, mr.b_z, mr.c_z
        from nsi.mvps_resist mr
        where mr.id in (select t9.id_koef_mvps_resist from t9)
      ),
      tAll as (
        select t1.rowComment, coalesce(t1.nord::text,'') || ';' || coalesce(to_char(t1.dt_arr, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t1.dt_dep, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' || coalesce(t1.comm_km::text, '') || ';' ||
               coalesce(t1.w_brutto::text, '') || ';' || coalesce(t1.w_netto::text, '') || ';' ||
               coalesce(t1.num_train::text, '') || ';' || coalesce(t1.num_sostav::text, '') || ';' ||
               coalesce(t1.st_id::text, '') as val
        from t1
        union all
        select t2.rowComment, coalesce(t2.qt_full::text, '') || ';' || coalesce(t2.qt_empty::text, '') || ';' ||
               coalesce(t2.type_vag::text, '') || ';' || coalesce(t2.num_sostav::text, '')
        from t2
        union all
        select t3.rowComment, coalesce(t3.loc_ser::text, '') || ';' || coalesce(t3.rec_sched1::text, '') || ';' ||
               coalesce(t3.rec_sched2::text, '') || ';' || coalesce(t3.loc_num::text, '') || ';' ||
               coalesce(to_char(t3.dt_cp_out, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t3.dt_receive_end, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t3.dt_handover_begin, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t3.dt_cp_in, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(t3.pr_handover::text, '') || ';' || coalesce(t3.pr_receive::text, '') || ';' ||
               coalesce(t3.pr_mvps_espul::text, '')
        from t3
        union all
        select t4.rowComment, coalesce(t4.code::text, '') || ';' || coalesce(t4.id_vag_property::text, '')
        from t4
        union all
        select t5.rowComment, coalesce(t5.id::text, '') || ';' || coalesce(t5.code_resist_alg::text, '') || ';' ||
             coalesce(t5.num_axel::text, '') || ';' || coalesce(t5.weight::text, '') || ';' || coalesce(t5.length::text, '')
        from t5
        union all
        select t6.rowComment, coalesce(t6.code_resist_alg::text, '') || ';' || coalesce(t6.rail_type::text, '') || ';' ||
               coalesce(trim(to_char(t6.a0, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t6.a1, 'FM99999999990D0000')), '') || ';' ||
               coalesce(trim(to_char(t6.a2, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t6.a3, 'FM99999999990D0000')), '')
         from t6
         union all
         select t7.rowComment, coalesce(t7.rangea::text, '') || ';' || coalesce(t7.rangebfull::text, '') || ';' ||
             coalesce(t7.groupname::text, '') || ';' || coalesce(trim(to_char(t7.acceleration, 'FM99999999990D00')), '') || ';' ||
             coalesce(trim(to_char(t7.slowdown, 'FM99999999990D00')), '') || ';' || coalesce(t7.typetrain::text, '')
         from t7
         union all
         select t8.rowComment, coalesce(t8.nord::text, '') || ';' || coalesce(t8.rail_type::text, '') || ';' ||
             coalesce(t8.id::text, '')
         from t8
         union all
         select t9.rowComment, coalesce(t9.num_ser::text, '') || ';' || coalesce(t9.weight::text, '') || ';' ||
             coalesce(t9.length::text, '') || ';' || coalesce(t9.id_koefresists::text, '') || ';' ||
             coalesce(trim(to_char(t9.kpdloc, 'FM99999999990D00')), '') || ';' ||
             coalesce(trim(to_char(t9.kpdrazgon, 'FM99999999990D00')), '') || ';' ||
             coalesce(trim(to_char(t9.koefstartv1, 'FM99999999990D0000')), '') || ';' ||
             coalesce(trim(to_char(t9.koefstartv2, 'FM99999999990D0000')), '') || ';' ||
             coalesce(trim(to_char(t9.koefaadditionalloss, 'FM99999999990D0000')), '') || ';' ||
             coalesce(t9.loc_type::text, '') || ';' || coalesce(t9.type_tok::text, '') || ';' ||
             coalesce(trim(to_char(t9.p_support, 'FM99999999990D0000')), '') || ';' ||
             coalesce(trim(to_char(t9.p_supportstopped, 'FM99999999990D0000')), '') || ';' ||
             coalesce(t9.is_ep20::text, '') || ';' || coalesce(t9.is_2ec4k_3ec4k_vl80::text, '') || ';' ||
             coalesce(t9.id_loc_power_consumption::text, '0') || ';' || coalesce(t9.id_loc_fuel_consumption::text, '0') || ';' ||
             coalesce(t9.id_koef_mvps_resist::text, '0') || ';' || coalesce(t9.minsectioncount::text, '0')
             as val
         from t9
         union all
         select t10.rowComment, coalesce(t10.id::text, '') || ';' ||
             coalesce(trim(to_char(t10.v, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t10.kpd, 'FM99999999990D0000')), '')
         from t10
         union all
         select t11.rowComment, coalesce(t11.id::text, '') || ';' || coalesce(t11.ldefault::text, '') || ';' ||
             coalesce(trim(to_char(t11.a0, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t11.a1, 'FM99999999990D0000')), '') || ';' ||
             coalesce(trim(to_char(t11.a2, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t11.a1_z, 'FM99999999990D0000')), '') || ';' ||
             coalesce(trim(to_char(t11.a2_z, 'FM99999999990D0000')), '')
         from t11
         union all
         select t12.rowComment, coalesce(t12.num_ser::text, '') || ';' || coalesce(t12.qt_sec::text, '') || ';' ||
             coalesce(trim(to_char(t12.a4, 'FM99999999990D000000')), '') || ';' || coalesce(trim(to_char(t12.a3, 'FM99999999990D000000')), '') || ';' ||
             coalesce(trim(to_char(t12.a2, 'FM99999999990D000000')), '') || ';' || coalesce(trim(to_char(t12.a1, 'FM99999999990D000000')), '') || ';' ||
             coalesce(trim(to_char(t12.a0, 'FM99999999990D000000')), '') || ';' || coalesce(trim(to_char(t12.p_min, 'FM99999999990D000000')), '') || ';' ||
             coalesce(trim(to_char(t12.p_max, 'FM99999999990D000000')), '')
         from t12
         union all
         select t13.rowComment,  f_val
         from t13
         union all
         select t14.rowComment, coalesce(t14.nord::text,'') || ';' ||
               coalesce(t14.rec_begin::text, '') || ';' || coalesce(t14.rec_end::text, '') || ';' ||
               coalesce(to_char(t14.dt_begin, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t14.dt_end, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(t14.time_all::text, '') || ';' ||
               coalesce(t14.time_stop::text, '') || ';' || coalesce(t14.run::text, '') || ';' ||
               coalesce(t14.qt_stop::text, '') || ';' || coalesce(t14.time_nagon::text, '')  || ';' ||
               coalesce(t14.num_train::text, '') || ';' || coalesce(t14.time_manevr::text, '') || ';' ||
               coalesce(t14.w_brutto::text, '') || ';' || coalesce(t14.w_netto::text, '') || ';' ||
               coalesce(t14.st_id_begin::text, '') || ';' || coalesce(t14.st_id_end::text, '') as val
         from t14
         union all
         select t15.rowComment, coalesce(t15.id::text,'') || ';' ||
               coalesce(t15.kol_vagon::text, '') || ';' || coalesce(trim(to_char(t15.r1, 'FM99999999990D0000')), '') || ';' ||
               coalesce(trim(to_char(t15.r2, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t15.r3, 'FM99999999990D0000')), '') || ';' ||
               coalesce(t15.num_train_start::text, '') || ';' || coalesce(t15.num_train_finish::text, '') || ';' ||
               coalesce(t15.w_train_1::text, '') || ';' || coalesce(t15.w_train_2::text, '') || ';' ||
               coalesce(t15.w_train_3::text, '') || ';' || coalesce(t15.w_train_4::text, '') as val
         from t15
         union all
         select t16.rowComment, coalesce(t16.id::text,'') || ';' ||
               coalesce(trim(to_char(t16.r1, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t16.r2, 'FM99999999990D0000')), '') || ';' ||
               coalesce(trim(to_char(t16.r3, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t16.r4, 'FM99999999990D0000')), '') || ';' ||
               coalesce(trim(to_char(t16.n, 'FM99999999990D0000')), '') || ';' || coalesce(trim(to_char(t16.w_netto, 'FM99999999990D0000')), '') || ';' ||
               coalesce(trim(to_char(t16.w_brutto, 'FM99999999990D0000')), '') as val
         from t16
         union all
         select t17.rowComment, coalesce(t17.nord_shed::text,'') || ';' || coalesce(t17.nord::text,'') || ';' ||
               coalesce(to_char(t17.dt_begin, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t17.dt_end, 'YYYY-MM-DD HH24.MI.SS'), '') as val
         from t17
         union all
         select t18.rowComment, coalesce(t18.nord_shed::text,'') || ';' || coalesce(t18.dur_manevr::text,'') || ';' ||
               coalesce(t18.dur_stop::text,'')
         from t18
         union all
         select t19.rowComment, coalesce(to_char(t19.dt_begin, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t19.dt_end, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' || coalesce(t19.pr_tg_double::text, '') as val
         from t19
         union all
         select t20.rowComment,  f_val
         from t20
         union all
         select t21.rowComment, coalesce(t21.nord::text,'') || ';' || coalesce(t21.num_train::text,'') || ';' ||
               coalesce(to_char(t21.dt_arr, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(to_char(t21.dt_dep, 'YYYY-MM-DD HH24.MI.SS'), '') || ';' ||
               coalesce(t21.st_id::text, '') || ';' || coalesce(t21.xline::text, '') as val
         from t21
         union all
         select t22.rowComment, coalesce(t22.id::text,'') || ';' || coalesce(t22.kol_vagon::text,'') || ';' ||
               coalesce(t22.a::text,'') || ';' || coalesce(t22.b::text,'') || ';' || coalesce(t22.c::text,'') || ';' ||
               coalesce(t22.a_z::text,'') || ';' || coalesce(t22.b_z::text,'') || ';' || coalesce(t22.c_z::text,'')
         from t22
          )
    select row_number() OVER() as ord, tAll.rowComment, tAll.val
    from tAll;
END
$BODY$
LANGUAGE 'plpgsql';



