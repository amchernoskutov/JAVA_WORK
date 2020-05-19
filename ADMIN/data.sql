CREATE OR REPLACE FUNCTION config.get_settings ()
  RETURNS TABLE (code_module text, id_data numeric, data_name text, property text, 
				 val text, mnem_system text) AS
$BODY$ 
DECLARE
  rec RECORD;
BEGIN  
  FOR rec IN EXECUTE '
    select settings.code_module, 0 as id_data, null as data_name, 
	       settings.property, settings.val, null as mnem_system
    from config.settings settings
    where (settings.id_data is null) and (settings.id_system is null)
    union
    select settings.code_module, 0 as id_data, null as data_name, 
	       settings.property, settings.val, system_name.mnem_system
    from config.settings settings, config.system_name system_name
    where (settings.id_data is null) and
	      (settings.id_system = system_name.id_system)
    union
    select settings.code_module, data_name.id_data, data_name.data_name, 
	       settings.property, settings.val, null as mnem_system
    from config.settings settings, config.data_name data_name
    where (settings.id_data = data_name.id_data) and (data_name.use_get_data = true) and
	      (settings.id_system is null)
    union
    select settings.code_module, data_name.id_data, data_name.data_name, 
	       settings.property, settings.val, system_name.mnem_system
    from config.settings settings, config.data_name data_name,
	     config.system_name system_name
    where (settings.id_data = data_name.id_data) and (data_name.use_get_data = true) and
	      (settings.id_system = system_name.id_system)
		  '
  LOOP
	code_module = rec.code_module;
	id_data = rec.id_data;
	data_name = rec.data_name;
	property = rec.property;
	val = rec.val;
	mnem_system = rec.mnem_system;
    RETURN next;
  END LOOP;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE COST 100 ROWS 1000;   
   
   
CREATE OR REPLACE FUNCTION config.get_system_name ()
  RETURNS TABLE (id_system numeric, mnem_system text, description text) AS
$BODY$ 
DECLARE
  rec RECORD;
BEGIN  
  FOR rec IN EXECUTE '
    select id_system, mnem_system, description
    from config.system_name
	order by id_system
		  '
  LOOP
	id_system = rec.id_system;
	mnem_system = rec.mnem_system;
	description = rec.description;
    RETURN next;
  END LOOP;
END
$BODY$
LANGUAGE 'plpgsql' VOLATILE COST 100 ROWS 1000;   

CREATE OR REPLACE PROCEDURE config.add_system_name (mnem_system text, description text)
AS
$BODY$ 
BEGIN  
  INSERT INTO config.system_name (mnem_system, description)
  VALUES (mnem_system, description);
END
$BODY$
LANGUAGE 'plpgsql';   

CREATE OR REPLACE PROCEDURE config.delete_system_name (id numeric)
AS
$BODY$ 
BEGIN  
  DELETE FROM config.system_name system_name WHERE (system_name.id_system = id);
END
$BODY$
LANGUAGE 'plpgsql';   

CREATE OR REPLACE PROCEDURE config.update_system_name (id numeric, mnem text, descr text)
AS
$BODY$ 
BEGIN  
  UPDATE config.system_name 
  SET mnem_system = mnem, description = descr
  WHERE (system_name.id_system = id);
END
$BODY$
LANGUAGE 'plpgsql';   


CREATE OR REPLACE FUNCTION config.get_one_system_name (id numeric)
RETURNS RECORD AS
$BODY$ 
DECLARE
rec RECORD;
BEGIN  
  SELECT system_name.id_system, system_name.mnem_system, system_name.description
  INTO rec 
  FROM config.system_name system_name 
  WHERE (system_name.id_system = id);
  
  RETURN rec;
END
$BODY$
LANGUAGE 'plpgsql';



select id_system, mnem_system, description from config.get_one_system_name(1) as (id_system smallint, mnem_system varchar, description varchar);

