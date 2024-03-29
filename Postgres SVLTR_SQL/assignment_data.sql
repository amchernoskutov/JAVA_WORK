--CREATE SCHEMA svltr;


--создание таблицы svltr.assigment_data
DROP TABLE IF EXISTS svltr.assigment_data;
CREATE TABLE svltr.assigment_data(loc_type SMALLINT, loc_num INTEGER, sec_num SMALLINT,
assignment_road_id SMALLINT, assignment_depot_id SMALLINT, record_dt TIMESTAMP);

COMMENT ON TABLE svltr.assigment_data IS 'Приписка локомотива';
comment on column svltr.assigment_data.loc_type is 'Код серии локомотива';
comment on column svltr.assigment_data.loc_num is 'Номер локомотива';
comment on column svltr.assigment_data.sec_num is 'Номер секции';
comment on column svltr.assigment_data.assignment_road_id is 'Код дороги приписки локомотива';
comment on column svltr.assigment_data.assignment_depot_id is 'Код депо приписки локомотива';
comment on column svltr.assigment_data.record_dt is 'Дата и время записи в БД';

drop table if exists temp_svltr;
create unlogged table temp_svltr(doc json);

--заливка данных истории assigment_data* в таблицу svltr.assigment_data
--список (уникальных частей) имен файлов для заливки в строке v_lstFiles разделенных символом ";"
DO LANGUAGE plpgsql $$
DECLARE
  v_lstFiles text = '20200831-1400_20200831-1415';
  arr_files text[];
  v_path text = '/opt/postgres/data_src/svltr/';
  v_file text; v_s text;
BEGIN
  arr_files = string_to_array(v_lstFiles,';');
  FOREACH v_s in array arr_files LOOP
    truncate table temp_svltr;
    --copy temp_svltr from '/opt/postgres/data_src/svltr/coord_speed_history_20200831-1343_20200831-1344.json';
    v_file = v_path || 'assignment_data_' || v_s || '.json';
    raise notice '%', v_file;

    --копирую json во временное поле таблицы temp_svltr
    execute 'copy temp_svltr from ''' || v_file || '''';

    --копирую данные в рабочую таблицу
    insert into svltr.assigment_data(loc_type, loc_num, sec_num, assignment_road_id, assignment_depot_id,record_dt)
      select p.loc_type, p.loc_num, p.sec_num, p.assignment_road_id, p.assignment_depot_id, p.record_dt
      from temp_svltr l
      cross join lateral json_populate_recordset(null::svltr.assigment_data, doc) as p;
  END LOOP;
END;
$$;

drop table if exists temp_svltr;