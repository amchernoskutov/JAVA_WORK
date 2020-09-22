drop table if exists nsi.mvps_resist;

create table nsi.mvps_resist
(
	id serial not null,
	kol_vagon integer,
	a real,
	b real,
	c real,
	a_z real,
	b_z real,
	c_z real
);

comment on table nsi.mvps_resist is 'Значения коэффициентов, входящих в выражение для расчета основного удельного сопротивления движению';
comment on column nsi.mvps_resist.id is 'id';
comment on column nsi.mvps_resist.kol_vagon is 'Количество вагонов';
comment on column nsi.mvps_resist.a is 'Коэффициент бесстыковой a';
comment on column nsi.mvps_resist.b is 'Коэффициент бесстыковой b';
comment on column nsi.mvps_resist.c is 'Коэффициент бесстыковой c';
comment on column nsi.mvps_resist.a_z is 'Коэффициент звеньевой a_z';
comment on column nsi.mvps_resist.b_z is 'Коэффициент звеньевой b_z';
comment on column nsi.mvps_resist.c_z is 'Коэффициент звеньевой c_z';

insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (1, 10, 10.8, 0.1, 0.0022, 10.8, 0.12, 0.0026);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (2, 10, 10.9, 0.078, 0.0019, 10.9, 0.188, 0.0026);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (3, null, 7.2, 0.115, 0.0041, 6.79, 0.329, 0.0002);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (4, null, 5.12, 0.21, 0.005, 16.0, 0.176, 0.00079);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (5, 10, 5.763, 0.243, 0.00454, null, null, null);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (6, 10, null, null, null, null, null, null);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (7, null, 5.42, 0.0045, 0.00226, null, null, null);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (8, 10, 7.2, 0.0539, 0.00126, null, null, null);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (9, null, 15.6, 0.0032, 0.0029, 15.6, 0.0026, 0.0034);
insert into nsi.mvps_resist(id, kol_vagon, a, b, c, a_z, b_z, c_z) values (10, 2, null, null, null, null, null, null);

alter table nsi.loc_ser
	add column if not exists id_koef_mvps_resist integer;
comment on column nsi.loc_ser.id_koef_mvps_resist is 'ID коэфф. для расчета основного удельного сопротивления движению для МВПС-> nsi.mvps_resist';

update nsi.loc_ser set id_koef_mvps_resist = 1 where num_ser in (306,366,313,372,371,401,402,405,406,404,403);
update nsi.loc_ser set id_koef_mvps_resist = 2 where num_ser in (316,318,322,324,321,330,410,409);
update nsi.loc_ser set id_koef_mvps_resist = 3 where num_ser in (412,413,425,416);
update nsi.loc_ser set id_koef_mvps_resist = 4 where num_ser in (326,328,310,323,327,374);
update nsi.loc_ser set id_koef_mvps_resist = 5 where num_ser in (486,317,377,488);
update nsi.loc_ser set id_koef_mvps_resist = 6 where num_ser in (375);
update nsi.loc_ser set id_koef_mvps_resist = 7 where num_ser in (376);
update nsi.loc_ser set id_koef_mvps_resist = 8 where num_ser in (802,803,852,851);

