drop table if exists nsi.vag_resist_alg;

create table nsi.vag_resist_alg
(
	id serial not null
		constraint vag_resist_alg_pk
			primary key,
	code_resist_alg integer not null,
	rail_type smallint not null,
	name varchar(50) not null,
	a0 real not null,
	a1 real not null,
	a2 real not null,
	a3 real not null,
	dt_modify timestamp default CURRENT_TIMESTAMP not null
);

comment on table nsi.vag_resist_alg is 'справочник коэф. основного удельного сопротивления вагонов для алг. расчета';
comment on column nsi.vag_resist_alg.code_resist_alg is 'код общий для типа вагона с учетом вариантов типа пути и загрузки';
comment on column nsi.vag_resist_alg.rail_type is 'тип пути (0-звеньевой, 1-бесстыковой)';
comment on column nsi.vag_resist_alg.name is 'наименование типа вагона';
comment on column nsi.vag_resist_alg.a0 is 'постоянный коэф., Н/т';
comment on column nsi.vag_resist_alg.a1 is 'постоянный коэф. зависящий от нагрузки на ось, Н/т';
comment on column nsi.vag_resist_alg.a2 is 'коэф. зависящий от скорости, Н/т';
comment on column nsi.vag_resist_alg.a3 is 'коэф. зависящий от квадрата скорости, Н/т';

insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (1, 'Полувагон (ПВ)', 0, 5.2, 35.4, 0.785, 0.027);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (1, 'Полувагон (ПВ)', 1, 5.2, 34.2, 0.732, 0.022);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (2, 'Цистерна 4-х осная', 0, 6.3, 28.7, 0.464, 0.027);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (2, 'Цистерна 4-х осная', 1, 6.3, 27.9, 0.436, 0.022);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (3, 'Цистерна 8-и осная', 0, 6.9, 58.9, 0.37,  0.021);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (3, 'Цистерна 8-и осная', 1, 6.9, 58.9, 0.26,  0.017);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (4, 'Рефрижератор', 0, 6.7, 29.4, 0.98, 0.025);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (4, 'Рефрижератор', 1, 6.9, 29.5, 0.88, 0.02);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (5, 'Пассажирские', 0, 6.9, 78.5, 1.76, 0.03);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (5, 'Пассажирские', 1, 6.9, 78.5, 1.57, 0.022);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (6, 'Пассажирские 2этаж.', 0, 6.9, 78.5, 1.76, 0.036);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (6, 'Пассажирские 2этаж.', 1, 6.9, 78.5, 1.57, 0.0264);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (7, 'Инновационные ПВ', 0, 5.18, 28.6, 0.521, 0.02);
insert into nsi.vag_resist_alg(code_resist_alg, name, rail_type, a0, a1, a2, a3) values (7, 'Инновационные ПВ', 1, 5.18, 28.6, 0.521, 0.02);

drop trigger if exists trg_vag_resist_alg on nsi.vag_resist_alg;

create trigger trg_vag_resist_alg
  after insert or update or delete
  on nsi.vag_resist_alg
for each row
  execute procedure nsi.logged_changes();

alter table nsi.vag_type
	add column if not exists code_resist_alg integer;
comment on column nsi.vag_type.code_resist_alg is 'код коэфф.сопротивления -> nsi.vag_resist_alg.code_resist_alg (для тягового расчета)';

update nsi.vag_type set code_resist_alg = 1;
update nsi.vag_type set code_resist_alg = 1 where strpos(lower(type_name), 'прочие') > 0;
update nsi.vag_type set code_resist_alg = 2 where strpos(lower(type_name), 'цистерны 4') > 0;
update nsi.vag_type set code_resist_alg = 4 where strpos(lower(type_name), 'рефрижерат') > 0;
update nsi.vag_type set code_resist_alg = 5 where strpos(lower(type_name), 'пассажирск') > 0;

