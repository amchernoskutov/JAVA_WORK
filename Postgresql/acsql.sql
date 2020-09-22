select count(*) from mm.m2_marshrut;
select mm.ac_getTER(doc_date, doc_id) from mm.m2_marshrut where not doc_id in (15106235, 244692371, 15106478, 244692531, 244692564);
------------------------------------------------------------------------------------------------------------------------

select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15105509);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105509);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105509);

select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15105423);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105423);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105423);

select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15105430);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105430);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105430);

select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15105484);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105484);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105484);

select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15105603); -- поезд пассажирский
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105603);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105603);

select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15106170);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15106170);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15106170);

-- производим расчет расхода электроэнергии на участке всей поездки в целом и выходим из цикла
select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15106235);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15106235);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15106235);

-- производим расчет расхода электроэнергии на участке (%-%) безостановочного движения
select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 244692371);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=244692371);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=244692371);

------------------------------------------------------------------------------------------------------------------------


select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105862);
select * from mm.e_init where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105862);

select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105543);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105548);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105548);




select loc_ser, * from mm.m2_locsec where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105509);
select id_koeftanp, * from nsi.loc_ser where num_ser=222;


select sg.type_vag, sg.qt_full, sg.qt_empty, v.id_vag_property, vt.id, vt.code_resist_alg, vr.a0, vr.a1, vr.a2, vr.a3
from mm.m2_sostav_groupvag sg
left join mm.ci_type_vagon v on (v.code = sg.type_vag)
left join nsi.vag_type vt on (vt.id = v.id_vag_property)
left join nsi.vag_resist_alg vr on (vr.code_resist_alg = vt.code_resist_alg)
where (sg.num_sostav = 1) and (vr.rail_type = 0) and
      (sg.doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (sg.doc_id=15105509);

select sum(vr.a0 * (sg.qt_full + sg.qt_empty))/sum(sg.qt_full + sg.qt_empty) as a0,
       sum(vr.a1 * (sg.qt_full + sg.qt_empty))/sum(sg.qt_full + sg.qt_empty) as a1,
       sum(vr.a2 * (sg.qt_full + sg.qt_empty))/sum(sg.qt_full + sg.qt_empty) as a2,
       sum(vr.a3 * (sg.qt_full + sg.qt_empty))/sum(sg.qt_full + sg.qt_empty) as a3,
       sum((sg.qt_full + sg.qt_empty) * vt.num_axel)
from mm.m2_sostav_groupvag sg
left join mm.ci_type_vagon v on (v.code = sg.type_vag)
left join nsi.vag_type vt on (vt.id = v.id_vag_property)
left join nsi.vag_resist_alg vr on (vr.code_resist_alg = vt.code_resist_alg)
where (sg.num_sostav = 1) and (vr.rail_type = 0) and
      (sg.doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (sg.doc_id=15105509);
-------------------------------------------------------------------------------------------------------------------
select sum(vr.a0 * sg.qt_full)/sum(sg.qt_full) as a0,
       sum(vr.a1 * sg.qt_full)/sum(sg.qt_full) as a1,
       sum(vr.a2 * sg.qt_full)/sum(sg.qt_full) as a2,
       sum(vr.a3 * sg.qt_full)/sum(sg.qt_full) as a3,
       sum(sg.qt_full * vt.num_axel),
       sum(vr.a0 * sg.qt_empty)/sum(sg.qt_empty) as b0,
       sum(vr.a1 * sg.qt_empty)/sum(sg.qt_empty) as b1,
       sum(vr.a2 * sg.qt_empty)/sum(sg.qt_empty) as b2,
       sum(vr.a3 * sg.qt_empty)/sum(sg.qt_empty) as b3,
       sum(sg.qt_empty * vt.num_axel),
       sum(sg.qt_empty * vt.weight)
from mm.m2_sostav_groupvag sg
left join mm.ci_type_vagon v on (v.code = sg.type_vag)
left join nsi.vag_type vt on (vt.id = v.id_vag_property)
left join nsi.vag_resist_alg vr on (vr.code_resist_alg = vt.code_resist_alg)
where (sg.num_sostav = 1) and (vr.rail_type = 0) and
      (sg.doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (sg.doc_id=15105423);

select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105423);

-------------------------------------------------------------------------------------------------------------------
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=280447956);


select groupname from nsi.categorytrainsdetail where (rangea <= 2392) and (2392 <= rangebfull);
select groupname from nsi.categorytrainsdetail where (rangea <= 8501) and (8501 <= rangebfull);
select n.groupname, s.*
from mm.m2_sched s, nsi.categorytrainsdetail n
where (n.rangea <= s.num_train) and (s.num_train <= n.rangebfull);

select st_id, * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15106235);

select id_way, * from nsi.path_ab where (stida = 12300000) and (stidb=12100000);
select id_way, * from nsi.path_ab where (stida = 12100000) and (stidb=12100000);
select rail_type, * from nsi.ways where (id = 3100102) and (dtbegin);
select * from nsi.path_ab;


select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105509);
select * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105509);
select * from mm.ci_type_vagon;
select * from nsi.vag_type;
select * from nsi.vag_resist_alg;


select count(w.*) from (select count(*) as id from nsi.vag_type v group by v.weight) as w;

select w.rail_type into rail_type
from nsi.path_ab p, nsi.ways w
where (p.stida = st_id_arr[rec.index - 1]) and (p.stidb=st_id_arr[rec.index]) and (p.id_way = w.id) and
(w.dtbegin <= docDate) and (w.dtend >= docDate);

select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105862);

select w.*, p.*
from nsi.path_ab p, nsi.ways w
where (p.stida = 33800000) and (p.stidb=34000000) and (p.id_way = w.id)



select grad, grad::real from nsi.way_profile where way_id=3400101;

select * from mm.e_tmp_calcresultdata;
select * from mm.e_resultdata;
select * from testspeed.test;


select * from mm.m2_marshrut;
select * from mm.e2_preparedataforcalc(1::integer, to_date('01.01.2020','dd.mm.yyyy'), 839823369::Integer);