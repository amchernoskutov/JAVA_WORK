--CREATE SCHEMA svltr;


--создание таблицы svltr.cs_history
DROP TABLE IF EXISTS svltr.cs_history;
CREATE TABLE svltr.cs_history(loc_type SMALLINT, loc_num SMALLINT, sec_num SMALLINT, speed SMALLINT, lat float, lon float,
  dt TIMESTAMP, record_dt TIMESTAMP);

COMMENT ON TABLE svltr.cs_history IS 'Скорость и географические координаты';
comment on column svltr.cs_history.loc_type is 'Код серии ТПС';
comment on column svltr.cs_history.loc_num is 'Номер электровоза / электропоезда / тепловоза';
comment on column svltr.cs_history.sec_num is 'Номер секции / вагона Секции локомотивов: 0 - А, 1 - Б, 2 - В, 3 - Г Номер вагона для электропоездов: 0 – 1-ый вагон, 1 – последний вагон';
comment on column svltr.cs_history.speed is 'Широта, градусы';
comment on column svltr.cs_history.lat is 'Долгота, градусы';
comment on column svltr.cs_history.lon is 'Скорость фактическая, км/ч';
comment on column svltr.cs_history.dt is 'Дата и время значения';
comment on column svltr.cs_history.record_dt is 'Дата и время записи';

drop table if exists temp_svltr;
create unlogged table temp_svltr(doc json);

--заливка данных истории coord_speed_history* в таблицу svltr.cs_history
--список (уникальных частей) имен файлов для заливки в строке v_lstFiles разделенных символом ";"
DO LANGUAGE plpgsql $$
DECLARE
  v_lstFiles text = '20200831-1343_20200831-1344;20200831-1344_20200831-1345;20200831-1345_20200831-1346;20200831-1346_20200831-1347;20200831-1347_20200831-1350;20200831-1350_20200831-1351;20200831-1351_20200831-1352;20200831-1352_20200831-1353;20200831-1353_20200831-1354;20200831-1354_20200831-1355;20200831-1355_20200831-1356;20200831-1356_20200831-1357;20200831-1357_20200831-1358;20200831-1358_20200831-1359;20200831-1359_20200831-1400;20200831-1400_20200831-1401;20200831-1401_20200831-1402;20200831-1402_20200831-1403;20200831-1403_20200831-1404;20200831-1404_20200831-1405;20200831-1405_20200831-1406;20200831-1406_20200831-1407;20200831-1407_20200831-1408;20200831-1408_20200831-1409;20200831-1409_20200831-1410;20200831-1410_20200831-1411;20200831-1411_20200831-1412;20200831-1412_20200831-1413;20200831-1413_20200831-1414;20200831-1414_20200831-1415;20200831-1415_20200831-1416;20200831-1416_20200831-1417;20200831-1417_20200831-1418;20200831-1418_20200831-1419;20200831-1419_20200831-1420;20200831-1420_20200831-1421;20200831-1421_20200831-1422;20200831-1422_20200831-1423;20200831-1423_20200831-1424;20200831-1424_20200831-1425;20200831-1425_20200831-1426;20200831-1426_20200831-1427;20200831-1427_20200831-1428;20200831-1428_20200831-1429;20200831-1429_20200831-1430;20200831-1430_20200831-1431;20200831-1431_20200831-1432';
  arr_files text[];
  v_path text = '/opt/postgres/data_src/svltr/';
  v_file text; v_s text;
BEGIN
  arr_files = string_to_array(v_lstFiles,';');
  FOREACH v_s in array arr_files LOOP
    truncate table temp_svltr;
    --copy temp_svltr from '/opt/postgres/data_src/svltr/coord_speed_history_20200831-1343_20200831-1344.json';
    v_file = v_path || 'coord_speed_history_' || v_s || '.json';
    raise notice '%', v_file;

    --копирую json во временное поле таблицы temp_svltr
    execute 'copy temp_svltr from ''' || v_file || '''';

    --копирую данные в рабочую таблицу
    insert into svltr.cs_history(loc_type, loc_num, sec_num, speed, lat, lon, dt, record_dt)
      select p.loc_type, p.loc_num, p.sec_num, p.speed, p.lat, p.lon, p.dt, p.record_dt
      from temp_svltr l
      cross join lateral json_populate_recordset(null::svltr.cs_history, doc) as p;
  END LOOP;
END;
$$;

drop table if exists temp_svltr;