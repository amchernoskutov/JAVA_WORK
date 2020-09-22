DROP FUNCTION mm.ac_preparedataforcalconeparams(varchar, integer,
out integer, out real, out real, out smallint);

CREATE OR REPLACE FUNCTION mm.ac_preparedataforcalconeparams(groupname varchar, w_brutto integer,
out maxSpeed integer, out acceleration real, out slowdown real, out typeTrain smallint)
  AS
$BODY$
/*
   Функция возвращает максимально допустимую скорость поезда на перегоне в зависимости от категории поезда
   Данные по справочнику nsi.categorytransdetal
   80 км/ч - грузовые поезда, 140 км/ч - пассажирские поезда, 100 км/ч - локомотивы
   А так же возвращает коэффициенты: среднее разгонное ускорение, расчётное время замедления поезда

   Входящие переменные:
   1) groupname - наименование диапазона категорий поездов
   2) w_brutto - масса поезда бруто в тоннах по остановочным пунктам

   Исходящие переменные:
   1) acceleration - среднее разгонное ускорение
   2) slowdown - расчётное время замедления поезда
   3) typeTrain - Тип поезда 0 - грузовые поезда, 1 - пассажирские поезда, 2 - локомотивы
 */
DECLARE
BEGIN
    case
        when strpos(lower(groupname), 'грузовые поезда') > 0 then
            typeTrain := 0; slowdown := 0.25;
            case
                when (w_brutto <= 2500) then acceleration := 0.14;
                when (w_brutto >= 2501) and (w_brutto < 3600) then acceleration := 0.1;
                when (w_brutto >= 3601) then acceleration := 0.07;
            end case;
        when strpos(lower(groupname), 'специализированные (ускоренные) грузовые поезда, в том числе на удлиненных плечах обслуживания') > 0 then
            typeTrain := 0; slowdown := 0.25;
            case
                when (w_brutto <= 2500) then acceleration := 0.14;
                when (w_brutto >= 2501) and (w_brutto < 3600) then acceleration := 0.1;
                when (w_brutto >= 3601) then acceleration := 0.07;
            end case;
        when strpos(lower(groupname), 'путевые') > 0 then
            typeTrain := 0; slowdown := 0.25;
            case
                when (w_brutto <= 2500) then acceleration := 0.14;
                when (w_brutto >= 2501) and (w_brutto < 3600) then acceleration := 0.1;
                when (w_brutto >= 3601) then acceleration := 0.07;
            end case;
        when strpos(lower(groupname), 'локомотивы') > 0 then
            typeTrain := 2; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname), 'пассажирские поезда') > 0 then
            typeTrain := 1; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname), 'почтово-багажные, грузо-пассажирские и людские поезда') > 0 then
            typeTrain := 1; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname), 'пригородные') > 0 then
            typeTrain := 1; slowdown := 1.15; acceleration := 0.24;
        when strpos(lower(groupname), 'технические поезда') > 0 then
            typeTrain := 1; slowdown := 1.15; acceleration := 0.24;
        else maxSpeed := 0;
    end case;
END
$BODY$
LANGUAGE 'plpgsql';



