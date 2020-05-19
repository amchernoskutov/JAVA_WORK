DROP FUNCTION mm.ac_getELVOZter(date, integer, smallint array, timestamp array, timestamp array, real array,
integer array, integer array, varchar array, smallint array);

CREATE OR REPLACE FUNCTION mm.ac_getELVOZter(docDate date, docId integer, w_brutto_arr smallint array,
    dt_arr_arr timestamp array, dt_dep_arr timestamp array, comm_km_arr real array,
    rangea_arr integer array, rangebfull_arr integer array, groupname_arr varchar array, num_train_arr smallint array
   ) RETURNS real AS
$BODY$
/*
   Функция возвращает расхода электроэнергии для электровоза

   Входящие переменные:
   1) docDate (date - dd.mm.yyyy) - Отчетная дата ввода и обработки документа (отчетные сутки)
   2) docId - Идентификатор документа
   3) w_brutto_arr - массив масса поезда бруто в тоннах по остановочным пунктам
   4) dt_arr_arr - массив с датами прибытия
   5) dt_dep_arr - массив с датами отправления
   6) comm_km_arr - массив пройденный путь в км
   7) rangea_arr - массив начала номеров диапазона категорий поездов
   8) rangebfull_arr - массив конца диапазона категорий поездов
   9) groupname_arr - массив наименований диапазона категорий поездов
   10) num_train_arr - массив номер поезда по остановочным пунктам

   Исходящие переменные:

 */
DECLARE
    CHANGE_BRUTTO CONSTANT NUMERIC := 22; -- Константа изменение массы (брутто) поезда больше которого считается начало новой поездки
    N3 integer; -- Общее количество поездок в маршруте машиниста
    N5 integer; -- Общее количество перегонов/участков безостановочного движения в маршруте машиниста
    N6 integer; -- Общее количество остановок

    -- Технические переменные
    rec record;
    startLengthStage integer; -- Начало перегона в км
    departureTime timestamp; -- Начало времени отправления на перегоне
    averageTechnicalSpeed real; -- Средняя техническая скорость на перегоне
    speedingStage boolean; -- Превышел лимит скорости на перегоне (true)
    numberTrain smallint; -- Номер поезда на перегоне

    notStopStartLengthStage integer; -- Начало безостановочного перегона в км
    notStopDepartureTime timestamp; -- Начало времени отправления на безостановочном перегоне
    notStopAverageTechnicalSpeed real; -- Средняя техническая скорость на безостановочном перегоне
    notStopNumberTrain smallint; -- Номер поезда на безостановочном перегоне
    notStopAverageSpeedTrain real; -- Средняя скорость без учёта разгона и торможения
    notStopSpeedingStage boolean; -- Превышел лимит скорости на безостановочном перегоне (true)

    acceleration real; -- среднее разгонное ускорение
    slowdown real; -- расчётное время замедления поезда
    w_brutto smallint; -- вес поезда на перегоне
    maxSpeed integer; -- максимально допустимая скорость
BEGIN
    /*
     Получаем N3 - общее количество поездок в маршруте машиниста
     Получаем N5 - общее количество перегонов/участков безостановочного движения в маршруте машиниста
     Получаем N6 - общее количество остановок
     */
    raise info 'Расчет для маршрута docDate = %, docId = %', docDate, docId;

    N3 := 0;
    N5 := 0;
    N6 := 0;

    -- перегон
    startLengthStage := 0;
    departureTime := dt_dep_arr[1];
    numberTrain := num_train_arr[1];
    speedingStage := false;
    notStopSpeedingStage := false;

    -- безостановочный перегон
    notStopStartLengthStage := 0;
    notStopDepartureTime := dt_dep_arr[1];
    notStopNumberTrain := num_train_arr[1];

    -- Вес поезда на перегоне
    w_brutto := w_brutto_arr[1];

    for rec in select row_number() OVER() as index, w.w_brutto from (select unnest(w_brutto_arr) as w_brutto) w
    loop
/*
        -- перегон
        if not (dt_arr_arr[rec.index] is null) and not speedingStage then
            averageTechnicalSpeed := (comm_km_arr[rec.index] - startLengthStage) * 60 / (extract(epoch from (dt_arr_arr[rec.index] - departureTime))/60);
            -- Если на перегоне превышена максимально допустимая скорость а так же до перегона и после нет остановки,
            -- то расчет расхода топлива нужно считать на безостановочном перегоне
            select t.maxSpeed into maxSpeed
            from mm.ac_getStageParameters(docDate, docId, numberTrain, rangea_arr,rangebfull_arr,
                groupname_arr, w_brutto) t;

            if (averageTechnicalSpeed > maxSpeed) and
               (N6 <> 1) and not (dt_dep_arr[rec.index] is null) and
               ((dt_dep_arr[rec.index] <> dt_arr_arr[rec.index]) or (dt_dep_arr[rec.index-1] <> dt_arr_arr[rec.index-1])) then
                speedingStage := true;
            end if;

            raise info 'Перегон номер = %', N6 + 1;
            raise info 'Техническая скорость прохождения перегона = %', averageTechnicalSpeed;
            raise info 'Максимальная скорость для перегона = %', maxSpeed;
            raise info 'Расстояние перегона в км = %', comm_km_arr[rec.index] - startLengthStage;
            raise info 'Время прохождения перегона в минутах = %', extract(epoch from (dt_arr_arr[rec.index] - departureTime))/60;
            raise info 'Превышена максимальная скорость = %', speedingStage;

            numberTrain := num_train_arr[rec.index];
            departureTime := dt_dep_arr[rec.index];
            startLengthStage := comm_km_arr[rec.index];
            w_brutto := w_brutto_arr[rec.index];
            N6 := N6 + 1;
        end if;
*/
        -- безостановочный перегон
        if not (dt_arr_arr[rec.index] is null) and
           ((dt_dep_arr[rec.index] <> dt_arr_arr[rec.index]) or (dt_dep_arr[rec.index] is null)) then
--               if speedingStage then
               if not speedingStage then
                   notStopAverageTechnicalSpeed := (comm_km_arr[rec.index] - notStopStartLengthStage) * 60 / (extract(epoch from (dt_arr_arr[rec.index] - notStopDepartureTime))/60);

                   select t.maxSpeed, t.acceleration, t.slowdown
                   from mm.ac_getStageParameters(docDate, docId, numberTrain, rangea_arr,
                       rangebfull_arr, groupname_arr,w_brutto) t
                   into maxSpeed, acceleration, slowdown;

                   -- Средняя скорость без учёта разгона и торможения
                   notStopAverageSpeedTrain := ((comm_km_arr[rec.index] - notStopStartLengthStage) -
                                                (notStopAverageTechnicalSpeed / (acceleration * 216) + notStopAverageTechnicalSpeed / (slowdown * 216)) * 40 / 60);
                   notStopAverageSpeedTrain := notStopAverageSpeedTrain /
                                               ((extract(epoch from (dt_arr_arr[rec.index] - notStopDepartureTime)) / 60) -
                                                (notStopAverageTechnicalSpeed / (acceleration * 216) + notStopAverageTechnicalSpeed / (slowdown * 216)));
                   notStopAverageSpeedTrain := notStopAverageSpeedTrain * 60;

                   if (notStopAverageSpeedTrain >= maxSpeed) then
                       notStopSpeedingStage := true;
                   end if;

                   raise info 'Безостановочный перегон номер = %', N5 + 1;
                   raise info 'Максимальная скорость = %, среднее разгонное ускорение = %, расчётное время замедления поезда = %', maxSpeed, acceleration, slowdown;
                   raise info 'Средняя скорость без учёта разгона и торможения = %', notStopAverageSpeedTrain;
                   raise info 'Техническая скорость прохождения безостановочного перегона = %', notStopSpeedingStage;
                   raise info 'Расстояние перегона в км = %', comm_km_arr[rec.index] - notStopStartLengthStage;
                   raise info 'Время прохождения перегона в минутах = %', extract(epoch from (dt_arr_arr[rec.index] - notStopDepartureTime))/60;
                   N5 := N5 + 1;
               end if;

            notStopNumberTrain := num_train_arr[rec.index];
            notStopDepartureTime := dt_dep_arr[rec.index];
            notStopStartLengthStage := comm_km_arr[rec.index];
            w_brutto := w_brutto_arr[rec.index];
            speedingStage := false;
        end if;

        if ((array_length(w_brutto_arr, 1) - rec.index) > 1) then
            if (abs(rec.w_brutto - w_brutto_arr[rec.index + 1]) > CHANGE_BRUTTO) and
               (abs(rec.w_brutto - w_brutto_arr[rec.index + 2]) > CHANGE_BRUTTO) and
               (w_brutto_arr[rec.index + 1] = w_brutto_arr[rec.index + 2]) then
                N3 := N3 + 1;
            end if;
        end if;
    end loop;


    raise info 'Общее количество поездок (изменение массы поезда более чем на 22 тонны) = %', N3;
    return 999;
END
$BODY$
LANGUAGE 'plpgsql';



