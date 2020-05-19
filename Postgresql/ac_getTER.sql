DROP FUNCTION mm.ac_getTER(date, integer);

CREATE OR REPLACE FUNCTION mm.ac_getTER(docDate date, docId integer)
  RETURNS real AS
$BODY$
/*
   Функция рассчитывает расход электроэнергии или дизельного топлива на маршрут машиниста

   Входящие переменные:
   1) docDate (date - dd.mm.yyyy) - Отчетная дата ввода и обработки документа (отчетные сутки)
   2) docId (integer) - Идентификатор документа

   Исходящие переменные:
   1) rez - расход электроэнергии или дизельного топлива на маршрут машиниста
 */
DECLARE
    -- массивы
    dt_arr_arr timestamp array; -- массив с датами прибытия
    dt_dep_arr timestamp array; -- массив с датами отправления
    comm_km_arr real array; -- массив пройденный путь в км
    loc_ser_arr smallint array; -- массив коды серий ТПС
    qt_full_arr smallint array; -- массив количество груженных вагонов
    qt_empty_arr smallint array; -- массив количество порожних вагонов
    type_vag_arr smallint array; -- массив код учетного рода вагонов
    w_brutto_arr smallint array; -- массив масса поезда бруто в тоннах по остановочным пунктам
    num_train_arr smallint array; -- массив номер поезда по остановочным пунктам
    rangea_arr integer array; -- массив начала номеров диапазона категорий поездов
    rangebfull_arr integer array; -- массив конца диапазона категорий поездов
    groupname_arr varchar array; -- массив наименований диапазона категорий поездов

    -- переменные
    rezult real; -- расход электроэнергии или дизельного топлива на маршрут машиниста
    countSections integer; -- количество секций электровоза
    countAxis integer; -- количество осей в составе
    timeDriving integer; -- общее время движения в минутах
    parkingTimeMin integer; -- общее время остановок в минутах
    parkingCount integer; -- число остановок на промежуточных остановочных пунктах
    lengthRouteKm real; -- длина участка всей поездки в километрах
    typeLocomotive smallint; -- тип локомотива (1 - электровоз, 2 - тепловоз, 3 - электропоезд, 4 - дизель-поезд и автомотрис (АМВПС))
    weightLocomotive real; -- вес локомотива в тоннах
    kpdLocomotive real; -- КПД локомотива
    powerLocomotiveMotion real; -- мощность локомотива в движении в кВт
    powerLocomotiveParking real; -- мощность локомотива на стоянке в кВт
    lengthTrain real; -- длина поезда (состава) в метрах

BEGIN
    -- Получаем массивы с данными из тяжелых таблиц, чтобы больше данные не запрашивать
    begin
        select d.dt_arr_arr, d.dt_dep_arr, d.comm_km_arr, d.loc_ser_arr, d.qt_full_arr, d.qt_empty_arr, d.type_vag_arr,
               d.w_brutto_arr, d.num_train_arr, d.rangea_arr, d.rangebfull_arr, d.groupname_arr
        from mm.ac_getData(docDate, docId) d
        into dt_arr_arr, dt_dep_arr, comm_km_arr, loc_ser_arr, qt_full_arr, qt_empty_arr, type_vag_arr,
             w_brutto_arr, num_train_arr, rangea_arr, rangebfull_arr, groupname_arr;
    exception
        when others then
            raise exception 'Ошибка: %', SQLERRM;
    end;

    -- Получаем начальные общие данные для расчета по маршруту машиниста и нитки графика,
    -- необходимые для расчета расхода электроэнергии или дизельного топлива
    begin
        select d.timeDriving, d.parkingTimeMin, d.parkingCount, d.lengthRouteKm, d.countAxis, d.countSections,
               d.typeLocomotive, d.weightLocomotive, d.kpdLocomotive, d.powerLocomotiveMotion, d.powerLocomotiveParking,
               d.lengthTrain
        from mm.ac_getCommonParameters(docDate, docId, dt_arr_arr, dt_dep_arr,
            comm_km_arr, loc_ser_arr,qt_full_arr,qt_empty_arr,
            type_vag_arr) d
        into timeDriving, parkingTimeMin, parkingCount, lengthRouteKm, countAxis, countSections,
             typeLocomotive, weightLocomotive, kpdLocomotive, powerLocomotiveMotion, powerLocomotiveParking,
             lengthTrain;
    exception
        when others then
            raise exception 'Ошибка: %', SQLERRM;
    end;

    -- Рассчитываем расход электроэнергии или дизельного топлива на маршрут машиниста
    begin
      case typeLocomotive
          when 1 then
              -- Производим расчет расхода топлива для электровоза
              select mm.ac_getELVOZter(docDate, docId, w_brutto_arr, dt_arr_arr,
                  dt_dep_arr, comm_km_arr, rangea_arr, rangebfull_arr,
                  groupname_arr, num_train_arr) into rezult;
          when 2 then rezult := 2; -- Производим расчет расхода топлива для тепловоза
          when 3 then rezult := 3; -- Производим расчет расхода топлива для электропоезда
          when 4 then rezult := 4; -- Производим расчет расхода топлива для дизель-поезда и автомотриса (АМВПС)
          else rezult := 0;
      end case;
    exception
        when others then
            raise exception 'Ошибка: %', SQLERRM;
    end;
/*
    raise info 'timeDriving=%', timeDriving;
    raise info 'parkingTimeMin=%', parkingTimeMin;
    raise info 'parkingCount=%', parkingCount;
    raise info 'lengthRouteKm=%', lengthRouteKm;
    raise info 'countAxis=%', countAxis;
    raise info 'countSections=%', countSections;
    raise info 'typeLocomotive=%', typeLocomotive;
    raise info 'weightLocomotive=%', weightLocomotive;
    raise info 'kpdLocomotive=%', kpdLocomotive;
    raise info 'powerLocomotiveMotion=%', powerLocomotiveMotion;
    raise info 'powerLocomotiveParking=%', powerLocomotiveParking;
    raise info 'lengthTrain=%', lengthTrain;
*/


    RETURN rezult;
END
$BODY$
LANGUAGE 'plpgsql';
