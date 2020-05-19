DROP FUNCTION mm.ac_getStageParameters(date, integer, smallint, integer array, integer array, varchar array, smallint,
out integer, out real, out real);

CREATE OR REPLACE FUNCTION mm.ac_getStageParameters(docDate date, docId integer,
  numberTrain smallint, rangea_arr integer array, rangebfull_arr integer array, groupname_arr varchar array,
  w_brutto smallint, out maxSpeed integer, out acceleration real, out slowdown real)
  AS
$BODY$
/*
   Функция возвращает максимально допустимую скорость поезда на перегоне в зависимости от категории поезда
   Данные по справочнику nsi.categorytransdetal
   80 км/ч - грузовые поезда, 140 км/ч - пассажирские поезда, 100 км/ч - локомотивы
   А так же возвращает коэффициенты: среднее разгонное ускорение, расчётное время замедления поезда

   Входящие переменные:
   1) docDate (date - dd.mm.yyyy) - Отчетная дата ввода и обработки документа (отчетные сутки)
   2) docId (integer) - Идентификатор документа
   3) numberTrain - номер поезда
   4) rangea_arr - массив начала номеров диапазона категорий поездов
   5) rangebfull_arr - массив конца диапазона категорий поездов
   6) groupname_arr - массив наименований диапазона категорий поездов
   7) w_brutto - масса поезда бруто в тоннах по остановочным пунктам

   Исходящие переменные:
   1) maxSpeed - максимально допустимая скорость плюс дополнительные 5 км/ч
   2) acceleration - среднее разгонное ускорение
   3) slowdown - расчётное время замедления поезда
 */
DECLARE
    groupname_str varchar; -- служебная переменная
    DOP_SPEEN CONSTANT NUMERIC := 5; -- дополнительные 5 км/ч
BEGIN
    with t as (select unnest(rangea_arr) as rangea, unnest(rangebfull_arr) as rangebfull, unnest(groupname_arr) as groupname)
    select groupname into groupname_str from t where (t.rangea <= numberTrain) and (numberTrain <= t.rangebfull) order by t.rangea limit 1;

    if (groupname_str is null) then
        raise exception 'Не определена категория поезда N% по маршруту: Отчетная дата: %, Идентификатор документа: %', numberTrain, docDate, docId;
    end if;

    -- 80 км/ч - грузовые поезда, 140 км/ч - пассажирские поезда, 100 км/ч - локомотивы
    maxSpeed := 0;
    case
        when strpos(lower(groupname_str), 'грузовые поезда') > 0 then
            maxSpeed := 80;
            slowdown := 0.25;
            case
                when (w_brutto <= 2500) then acceleration := 0.14;
                when (w_brutto >= 2501) and (w_brutto < 3600) then acceleration := 0.1;
                when (w_brutto >= 3601) then acceleration := 0.07;
            end case;
        when strpos(lower(groupname_str), 'локомотивы') > 0 then
            maxSpeed := 100; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname_str), 'пассажирские поезда') > 0 then
            maxSpeed := 140; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname_str), 'почтово-багажные, грузо-пассажирские и людские поезда') > 0 then
            maxSpeed := 140; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname_str), 'пригородные') > 0 then
            maxSpeed := 140; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname_str), 'специализированные (ускоренные) грузовые поезда, в том числе на удлиненных плечах обслуживания') > 0 then
            maxSpeed := 80;
            slowdown := 0.25;
            case
                when (w_brutto <= 2500) then acceleration := 0.14;
                when (w_brutto >= 2501) and (w_brutto < 3600) then acceleration := 0.1;
                when (w_brutto >= 3601) then acceleration := 0.07;
            end case;
        when strpos(lower(groupname_str), 'технические поезда') > 0 then
            maxSpeed := 140; slowdown := 1.15; acceleration := 0.24;
        else maxSpeed := 0;
    end case;

    if (maxSpeed = 0) then
        raise exception 'Не определена категория поезда N% по маршруту: Отчетная дата: %, Идентификатор документа: %', numberTrain, docDate, docId;
    end if;

    maxSpeed = maxSpeed + DOP_SPEEN;
END
$BODY$
LANGUAGE 'plpgsql';



