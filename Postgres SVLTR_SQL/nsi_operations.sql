--CREATE SCHEMA svltr;


--создание таблицы svltr.nsi_operations
DROP TABLE IF EXISTS svltr.nsi_operations;
CREATE TABLE svltr.nsi_operations(oper_id SMALLINT, oper_name TEXT, oper_name_short TEXT);

COMMENT ON TABLE svltr.nsi_operations IS 'Операции АСОУП';
comment on column svltr.nsi_operations.oper_id is 'Код операции';
comment on column svltr.nsi_operations.oper_name is 'Имя операции';
comment on column svltr.nsi_operations.oper_name_short is 'Короткое имя операции';

drop table if exists temp_svltr;
create unlogged table temp_svltr(doc json);

--заливка данных истории nsi_operations* в таблицу svltr.nsi_operations
--список (уникальных частей) имен файлов для заливки в строке v_lstFiles разделенных символом ";"
DO LANGUAGE plpgsql $$
DECLARE
  v_lstFiles text = '20200831-1340_20200831-1400';

  arr_files text[];
  v_path text = '/opt/postgres/data_src/svltr/';
  v_file text; v_s text;
BEGIN
  arr_files = string_to_array(v_lstFiles,';');
  FOREACH v_s in array arr_files LOOP
    truncate table temp_svltr;
    --copy temp_svltr from '/opt/postgres/data_src/svltr/coord_speed_history_20200831-1343_20200831-1344.json';
    v_file = v_path || 'nsi_operations_' || v_s || '.json';
    raise notice '%', v_file;

    --копирую json во временное поле таблицы temp_svltr
    execute 'copy temp_svltr from ''' || v_file || '''';

    --копирую данные в рабочую таблицу
    insert into svltr.nsi_operations(oper_id, oper_name, oper_name_short)
      select p.oper_id, p.oper_name, p.oper_name_short
      from temp_svltr l
      cross join lateral json_populate_recordset(null::svltr.nsi_operations, doc) as p;
  END LOOP;
END;
$$;

drop table if exists temp_svltr;