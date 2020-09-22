drop table if exists nsi.loc_fuel_consumption;

create table nsi.loc_fuel_consumption
(
	id serial not null,
	r1 real,
	r2 real,
	r3 real,
	r4 real,
	n real,
	w_netto real,
	w_brutto real
);

comment on table nsi.loc_fuel_consumption is 'Удельный расход топлива АМВПС на холостом ходу';
comment on column nsi.loc_fuel_consumption.r1 is 'Удельный расход топлива одной секции АМВПС кг/мин';
comment on column nsi.loc_fuel_consumption.r2 is 'Удельный расход топлива одной секции АМВПС кг/ч';
comment on column nsi.loc_fuel_consumption.r3 is 'Удельный расход топлива в целом АМВПС кг/мин';
comment on column nsi.loc_fuel_consumption.r4 is 'Удельный расход топлива в целом АМВПС кг/ч';
comment on column nsi.loc_fuel_consumption.n is 'Номинальная касательная мощность, кВт';
comment on column nsi.loc_fuel_consumption.w_netto is 'Масса поезда нетто';
comment on column nsi.loc_fuel_consumption.w_brutto is 'Масса поезда брутто';


create index loc_full_consumption_id_index
	on nsi.loc_fuel_consumption (id);

insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (1, 0.16, 9.6, 0.16, 9.6, 552, null, null);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (2, null, null, 0.32, 19.2, 1104, null, null);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (3, null, null, 0.48, 28.8, 1656, null, null);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (4, 0.16, 9.6, 0.32, 19.2, 1104, 272, 352);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (5, 0.15, 9, 0.30, 18, 807, 210, 274);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (6, 0.24, 2.4, 0.04, 2.4, 236, 42.5, 56.7);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (7, null, null, 0.08, 4.8, 472, 89.6, 123.1);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (8, null, null, 0.08, 4.8, 472, 126.1, 173.3);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (9, null, null, 0.12, 7.2, 708, 126.1, 173.3);
insert into nsi.loc_fuel_consumption(id, r1, r2, r3, r4, n, w_netto, w_brutto) values (10, null, null, 0.063, 3.8, 540, 101.2, 119.7);

alter table nsi.loc_ser
	add id_loc_fuel_consumption integer;
comment on column nsi.loc_ser.id_loc_fuel_consumption is 'Ссылка на id таблицы nsi.loc_fuel_consumption';

update nsi.loc_ser set id_loc_fuel_consumption=1 where num_ser=705;
update nsi.loc_ser set id_loc_fuel_consumption=4 where num_ser=804;
update nsi.loc_ser set id_loc_fuel_consumption=5 where num_ser=802;
update nsi.loc_ser set id_loc_fuel_consumption=6 where num_ser=852;

