drop table if exists nsi.loc_power_consumption;

create table nsi.loc_power_consumption
(
	id serial not null,
	kol_vagon integer,
	r1 real,
	r2 real,
	r3 real,
	num_train_start integer,
	num_train_finish integer,
	w_train_1 integer,
	w_train_2 integer,
	w_train_3 integer,
	w_train_4 integer
);

comment on table nsi.loc_power_consumption is 'Удельный расход электроэнергии электропоездами на собственные нужды и вентиляцию';
comment on column nsi.loc_power_consumption.kol_vagon is 'Количество вагонов';
comment on column nsi.loc_power_consumption.r1 is 'Расход э.э. на работу вспомогательных машин и оборудования МВПС, кВт.ч./мин.';
comment on column nsi.loc_power_consumption.r2 is 'Расход э.э. на работу вспомогательных машин и оборудования МВПС, кВт.ч./ч.';
comment on column nsi.loc_power_consumption.r3 is 'Расход э.э. на вентиляцию, кВт.ч./ч.';
comment on column nsi.loc_power_consumption.num_train_start is 'Начальный номер поездов';
comment on column nsi.loc_power_consumption.num_train_finish is 'Конечный номер поездов';
comment on column nsi.loc_power_consumption.w_train_1 is 'Масса одного вагона г.мот';
comment on column nsi.loc_power_consumption.w_train_2 is 'Масса одного вагона г.приц';
comment on column nsi.loc_power_consumption.w_train_3 is 'Масса одного вагона мот';
comment on column nsi.loc_power_consumption.w_train_4 is 'Масса одного вагона приц';

create index vag_power_consumption_id_index
	on nsi.loc_power_consumption (id);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (1, 10, 0.58, 34.8, 31.2, 7631, 7998, 45, 45, 45, 45);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (1, 10, 0.58, 34.8, 31.2, 6000, 6998, 54, 54, 54, 54);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (1, 10, 0.58, 34.8, 31.2, 7001, 7098, 50, 50, 50, 50);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (2, 10, 1.2, 72, 31.2, 7631, 7998, 45, 45, 45, 45);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (2, 10, 1.2, 72, 31.2, 6000, 6998, 54, 54, 54, 54);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (2, 10, 1.2, 72, 31.2, 7001, 7098, 50, 50, 50, 50);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (3, 2, null, 30, 7.2, 7631, 7998, 45, 45, 45, 45);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (3, 2, null, 30, 7.2, 6000, 6998, 54, 54, 54, 54);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (3, 2, null, 30, 7.2, 7001, 7098, 50, 50, 50, 50);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (4, 10, 1.45, 87, 31.2, 7631, 7998, 45, 45, 45, 45);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (4, 10, 1.45, 87, 31.2, 6000, 6998, 54, 54, 54, 54);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (4, 10, 1.45, 87, 31.2, 7001, 7098, 50, 50, 50, 50);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (5, 10, 0.93, 55.8, 31.2, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (5, 10, 0.93, 55.8, 31.2, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (5, 10, 0.93, 55.8, 31.2, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (6, 10, 1.08, 64.8, 31.2, 7631, 7998, 45, 45, 45, 45);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (6, 10, 1.08, 64.8, 31.2, 6000, 6998, 54, 54, 54, 54);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (6, 10, 1.08, 64.8, 31.2, 7001, 7098, 50, 50, 50, 50);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (7, 10, 1.09, 65.4, 31.2, 7631, 7998, 45, 45, 45, 45);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (7, 10, 1.09, 65.4, 31.2, 6000, 6998, 54, 54, 54, 54);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (7, 10, 1.09, 65.4, 31.2, 7001, 7098, 50, 50, 50, 50);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (8, 9, null, 70, null, 7631, 7998, 45, 45, 45, 45);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (8, 9, null, 70, null, 6000, 6998, 54, 54, 54, 54);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (8, 9, null, 70, null, 7001, 7098, 50, 50, 50, 50);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (9, 10, null, 30, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (9, 10, null, 30, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (9, 10, null, 30, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (10, 9, null, 70, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (10, 9, null, 70, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (10, 9, null, 70, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (11, 5, null, 54, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (11, 5, null, 54, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (11, 5, null, 54, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (12, 6, null, 34, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (12, 6, null, 34, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (12, 6, null, 34, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (13, 10, null, 250, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (13, 10, null, 250, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (13, 10, null, 250, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (14, 10, null, 250, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (14, 10, null, 250, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (14, 10, null, 250, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (15, 1, null, 36, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (15, 1, null, 36, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (15, 1, null, 36, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (16, 1, null, 36, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (16, 1, null, 36, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (16, 1, null, 36, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (17, 1, null, 27.2, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (17, 1, null, 27.2, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (17, 1, null, 27.2, null, 7001, 7098, null, null, null, null);

insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (18, 1, null, 32, null, 7631, 7998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (18, 1, null, 32, null, 6000, 6998, null, null, null, null);
insert into nsi.loc_power_consumption(id, kol_vagon, r1, r2, r3, num_train_start, num_train_finish, w_train_1, w_train_2, w_train_3, w_train_4) values (18, 1, null, 32, null, 7001, 7098, null, null, null, null);

alter table nsi.loc_ser
	add id_loc_power_consumption integer;
comment on column nsi.loc_ser.id_loc_power_consumption is 'Ссылка на id таблицы nsi.loc_power_consumption';

update nsi.loc_ser set id_loc_power_consumption=1 where num_ser in (306, 366, 313, 371, 372);
update nsi.loc_ser set id_loc_power_consumption=2 where num_ser in (316, 318, 321, 330, 327, 322, 324);
update nsi.loc_ser set id_loc_power_consumption=3 where num_ser in (326, 328);
update nsi.loc_ser set id_loc_power_consumption=4 where num_ser in (310);
update nsi.loc_ser set id_loc_power_consumption=5 where num_ser in (303);
update nsi.loc_ser set id_loc_power_consumption=6 where num_ser in (401, 402, 405, 406, 410, 409, 403, 404);
update nsi.loc_ser set id_loc_power_consumption=7 where num_ser in (412, 413);
update nsi.loc_ser set id_loc_power_consumption=8 where num_ser in (416);
update nsi.loc_ser set id_loc_power_consumption=9 where num_ser in (374);
update nsi.loc_ser set id_loc_power_consumption=10 where num_ser in (425);
update nsi.loc_ser set id_loc_power_consumption=11 where num_ser in (375);
update nsi.loc_ser set id_loc_power_consumption=12 where num_ser in (376);
update nsi.loc_ser set id_loc_power_consumption=13 where num_ser in (309);
update nsi.loc_ser set id_loc_power_consumption=14 where num_ser in (482);
update nsi.loc_ser set id_loc_power_consumption=15 where num_ser in (486);
update nsi.loc_ser set id_loc_power_consumption=16 where num_ser in (488);
update nsi.loc_ser set id_loc_power_consumption=17 where num_ser in (317);
update nsi.loc_ser set id_loc_power_consumption=18 where num_ser in (377);


