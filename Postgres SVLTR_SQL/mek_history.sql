--CREATE SCHEMA svltr;


--создание таблицы svltr.m_history
DROP TABLE IF EXISTS svltr.m_history;
CREATE TABLE svltr.m_history(loc_type SMALLINT, loc_num INTEGER, sec_num SMALLINT,
  mek_fio TEXT, dt TIMESTAMP, record_dt TIMESTAMP);

COMMENT ON TABLE svltr.m_history IS 'Данные МЭК';
comment on column svltr.m_history.loc_type is 'Код серии ТПС';
comment on column svltr.m_history.loc_num is 'Номер электровоза / электропоезда / тепловоза';
comment on column svltr.m_history.sec_num is 'Номер секции / вагона Секции локомотивов: 0 - А, 1 - Б, 2 - В, 3 - Г Номер вагона для электропоездов: 0 – 1-ый вагон, 1 – последний вагон';
comment on column svltr.m_history.mek_fio is 'ФИО авторизованного машиниста';
comment on column svltr.m_history.dt is 'Дата и время значения';
comment on column svltr.m_history.record_dt is 'Дата и время записи';

drop table if exists temp_svltr;
create unlogged table temp_svltr(doc json);

--заливка данных истории mek_history* в таблицу svltr.m_history
--список (уникальных частей) имен файлов для заливки в строке v_lstFiles разделенных символом ";"
DO LANGUAGE plpgsql $$
DECLARE
  v_lstFiles text = '20200831-1345_20200831-1350;20200831-1350_20200831-1355;20200831-1355_20200831-1400;'||
                    '20200831-1400_20200831-1405;20200831-1405_20200831-1410;20200831-1410_20200831-1415;'||
                    '20200831-1415_20200831-1420;20200831-1420_20200831-1425;20200831-1425_20200831-1430;'||
                    '20200831-1430_20200831-1435;20200831-1435_20200831-1440;20200831-1440_20200831-1445;'||
                    '20200831-1445_20200831-1450;20200831-1450_20200831-1455;20200831-1455_20200831-1500;'||
                    '20200831-1500_20200831-1505;20200831-1505_20200831-1510;20200831-1510_20200831-1515;'||
                    '20200831-1515_20200831-1520;20200831-1520_20200831-1525;20200831-1525_20200831-1530;'||
                    '20200831-1530_20200831-1535;20200831-1535_20200831-1540;20200831-1540_20200831-1545;'||
                    '20200831-1545_20200831-1550;20200831-1550_20200831-1555;20200831-1555_20200831-1600;'||
                    '20200831-1600_20200831-1605;20200831-1605_20200831-1610;20200831-1610_20200831-1615;'||
                    '20200831-1615_20200831-1620;20200831-1620_20200831-1625;20200831-1625_20200831-1630;'||
                    '20200831-1630_20200831-1635;20200831-1635_20200831-1640;20200831-1640_20200831-1645;'||
                    '20200831-1645_20200831-1650;20200831-1650_20200831-1655;20200831-1655_20200831-1700;'||
                    '20200831-1700_20200831-1705;20200831-1705_20200831-1710;20200831-1710_20200831-1715;'||
                    '20200831-1715_20200831-1720;20200831-1720_20200831-1725;20200831-1725_20200831-1730;'||
                    '20200831-1730_20200831-1735;20200831-1735_20200831-1740;20200831-1740_20200831-1745;'||
                    '20200831-1745_20200831-1750;20200831-1750_20200831-1755;20200831-1755_20200831-1800;'||
                    '20200831-1800_20200831-1805;20200831-1805_20200831-1810;20200831-1810_20200831-1815;'||
                    '20200831-1815_20200831-1820;20200831-1820_20200831-1825;20200831-1825_20200831-1830;'||
                    '20200831-1830_20200831-1835;20200831-1835_20200831-1840;20200831-1840_20200831-1845;'||
                    '20200831-1845_20200831-1850;20200831-1850_20200831-1855;20200831-1855_20200831-1900;'||
                    '20200831-1900_20200831-1905;20200831-1905_20200831-1910;20200831-1910_20200831-1915;'||
                    '20200831-1915_20200831-1920;20200831-1920_20200831-1925;20200831-1925_20200831-1930;'||
                    '20200831-1930_20200831-1935;20200831-1935_20200831-1940;20200831-1940_20200831-1945;'||
                    '20200831-1945_20200831-1950;20200831-1950_20200831-1955;20200831-1955_20200831-2000;'||
                    '20200831-2000_20200831-2005;20200831-2005_20200831-2010;20200831-2010_20200831-2015;'||
                    '20200831-2015_20200831-2020;20200831-2020_20200831-2025;20200831-2025_20200831-2030;'||
                    '20200831-2030_20200831-2035;20200831-2035_20200831-2040;20200831-2040_20200831-2045;'||
                    '20200831-2045_20200831-2050;20200831-2050_20200831-2055;20200831-2055_20200831-2100;'||
                    '20200831-2100_20200831-2105;20200831-2105_20200831-2110;20200831-2110_20200831-2115;'||
                    '20200831-2115_20200831-2120;20200831-2120_20200831-2125;20200831-2125_20200831-2130;'||
                    '20200831-2130_20200831-2135;20200831-2135_20200831-2140;20200831-2140_20200831-2145;'||
                    '20200831-2145_20200831-2150;20200831-2150_20200831-2155;20200831-2155_20200831-2200;'||
                    '20200831-2200_20200831-2205;20200831-2205_20200831-2210;20200831-2210_20200831-2215;'||
                    '20200831-2215_20200831-2220;20200831-2220_20200831-2225;20200831-2225_20200831-2230;'||
                    '20200831-2230_20200831-2235;20200831-2235_20200831-2240;20200831-2240_20200831-2245;'||
                    '20200831-2245_20200831-2250;20200831-2250_20200831-2255;20200831-2255_20200831-2300;'||
                    '20200831-2300_20200831-2305;20200831-2305_20200831-2310;20200831-2310_20200831-2315;'||
                    '20200831-2315_20200831-2320;20200831-2320_20200831-2325;20200831-2325_20200831-2330';

  arr_files text[];
  v_path text = '/opt/postgres/data_src/svltr/';
  v_file text; v_s text;
BEGIN
  arr_files = string_to_array(v_lstFiles,';');
  FOREACH v_s in array arr_files LOOP
    truncate table temp_svltr;
    --copy temp_svltr from '/opt/postgres/data_src/svltr/coord_speed_history_20200831-1343_20200831-1344.json';
    v_file = v_path || 'mek_history_' || v_s || '.json';
    raise notice '%', v_file;

    --копирую json во временное поле таблицы temp_svltr
    execute 'copy temp_svltr from ''' || v_file || '''';

    --копирую данные в рабочую таблицу
    insert into svltr.m_history(loc_type, loc_num, sec_num, mek_fio, dt, record_dt)
      select p.loc_type, p.loc_num, p.sec_num, p.mek_fio, p.dt, p.record_dt
      from temp_svltr l
      cross join lateral json_populate_recordset(null::svltr.m_history, doc) as p;
  END LOOP;
END;
$$;

drop table if exists temp_svltr;