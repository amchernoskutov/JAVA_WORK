--CREATE SCHEMA svltr;


--создание таблицы svltr.nsi_depots
DROP TABLE IF EXISTS svltr.nsi_depots;
CREATE TABLE svltr.nsi_depots(depot_id SMALLINT, depot_name TEXT, depot_name_short TEXT, railway_id SMALLINT);

COMMENT ON TABLE svltr.nsi_depots IS 'Депо';
comment on column svltr.nsi_depots.depot_id is 'Код депо';
comment on column svltr.nsi_depots.depot_name is 'Имя депо';
comment on column svltr.nsi_depots.depot_name_short is 'Короткое имя депо';
comment on column svltr.nsi_depots.railway_id is 'Код дороги, к которой относится депо';

drop table if exists temp_svltr;
create unlogged table temp_svltr(doc json);

--заливка данных истории nsi_depots* в таблицу svltr.nsi_depots
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
    v_file = v_path || 'nsi_depots_' || v_s || '.json';
    raise notice '%', v_file;

    --копирую json во временное поле таблицы temp_svltr
    execute 'copy temp_svltr from ''' || v_file || '''';

    --копирую данные в рабочую таблицу
    insert into svltr.nsi_depots(depot_id, depot_name, depot_name_short, railway_id)
      select p.depot_id, p.depot_name, p.depot_name_short, p.railway_id
      from temp_svltr l
      cross join lateral json_populate_recordset(null::svltr.nsi_depots, doc) as p;
  END LOOP;
END;
$$;

drop table if exists temp_svltr;