--CREATE SCHEMA svltr;


--создание таблицы svltr.nsi_railways
DROP TABLE IF EXISTS svltr.nsi_stations;
CREATE TABLE svltr.nsi_stations(station_esr INTEGER, station_name TEXT, railway_id SMALLINT);

COMMENT ON TABLE svltr.nsi_stations IS 'Станции';
comment on column svltr.nsi_stations.station_esr is 'ЕСР станции';
comment on column svltr.nsi_stations.station_name is 'Имя станции';
comment on column svltr.nsi_stations.railway_id is 'Код дороги, к которой относится станция';

drop table if exists temp_svltr;
create unlogged table temp_svltr(doc json);

--заливка данных истории nsi_stations* в таблицу svltr.nsi_stations
--список (уникальных частей) имен файлов для заливки в строке v_lstFiles разделенных символом ";"
DO LANGUAGE plpgsql $$
DECLARE
  v_lstFiles text = '20200831-1340_20200831-1355';

  arr_files text[];
  v_path text = '/opt/postgres/data_src/svltr/';
  v_file text; v_s text;
BEGIN
  arr_files = string_to_array(v_lstFiles,';');
  FOREACH v_s in array arr_files LOOP
    truncate table temp_svltr;
    --copy temp_svltr from '/opt/postgres/data_src/svltr/coord_speed_history_20200831-1343_20200831-1344.json';
    v_file = v_path || 'nsi_stations_' || v_s || '.json';
    raise notice '%', v_file;

    --копирую json во временное поле таблицы temp_svltr
    execute 'copy temp_svltr from ''' || v_file || '''';

    --копирую данные в рабочую таблицу
    insert into svltr.nsi_stations(station_esr, station_name, railway_id)
      select p.station_esr, p.station_name, p.railway_id
      from temp_svltr l
      cross join lateral json_populate_recordset(null::svltr.nsi_stations, doc) as p;
  END LOOP;
END;
$$;

drop table if exists temp_svltr;