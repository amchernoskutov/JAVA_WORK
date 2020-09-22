

select mm.ac_getTER(to_date('03.03.2020','dd.mm.yyyy'), 15105423);
select * from mm.m2_sched where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105423);
select  * from mm.m2_sostav_groupvag where (doc_date = to_date('03.03.2020','dd.mm.yyyy')) and (doc_id=15105423);

select * from mm.e2_preparedataforcalc(1::smallint, to_date('01.01.2020','dd.mm.yyyy'), 839823369::INTEGER) where f_rowcomment = 'lim';
select * from mm.e_tmp_calcresultdata;

select * from mm.e2_calcresult where doc_id=839824062;
select * from mm.e2_calcresult_alg where doc_id=839824062;
select * from  mm.e2_calcerror;
select * from  mm.e_tmp_calcresultdata;
select * from  mm.e_tmp_calcresult;

delete from mm.m2_calcresult;
delete from mm.m2_calcerror;
delete from mm.e_tmp_calcresultdata ;
delete from mm.e_tmp_calcresult;

SELECT * FROM pg_stat_activity;

SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE pid <> pg_backend_pid() AND datname = current_database();

delete from mm.m2_calcresult where doc_id = 839824334;
delete from  mm.m2_calcerror where doc_id = 839824334;
delete from  mm.e_tmp_calcresultdata;
delete from  mm.e_tmp_calcresult where doc_id = 839824334;

select distinct  doc_id from mm.m2_calcresult;
select count(*) from mm.m2_calcresult;
select distinct doc_id from mm.m2_calcresult;
select distinct m.doc_id from mm.m2_trains t join mm.m2_marshrut m on m.doc_date=t.doc_date and m.doc_id=t.doc_id where ((st_id_begin=873300000 and st_id_end=880100000) or (st_id_begin=880100000 and st_id_end=873300000)) and m.status=0;

select * from mm.m2_calcresult where doc_id = 839824334;
select * from mm.m2_calcerror where doc_id = 839824334;
select * from mm.e_tmp_calcresultdata;
select * from mm.e_tmp_calcresult where doc_id = 839824334;

select * from mm.e2_preparedataforcalc(1::smallint, to_date('01.01.2020','dd.mm.yyyy'), 839824334);
select doc_id, doc_date from mm.m2_marshrut where doc_id = 839846063;

select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_id in (839823369, 839823370);

select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut limit 6;
select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_id in (839823369, 839823370, 839823374);
select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_id in (839823375, 839823376, 839823378);

select count(*) from mm.m2_marshrut;

select 1  as typeData, extract(epoch from t.doc_date) * 1000 as docDate, t.doc_id as docID   from mm.m2_trains t   join (select doc_date, doc_id, min(loc_ser)loc_ser from mm.m2_locsec group by doc_date, doc_id)ls on ls.doc_date=t.doc_date and ls.doc_id=t.doc_id join nsi.loc_ser l on l.num_ser=ls.loc_ser where t.st_id_begin=880100000 and t.st_id_end=873300000 and ls.loc_ser=125 limit 10;

select * from mm.m2_sched where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823370));

insert into mm.e_tmp_calcresultdata (id_calcpkg, ord, type_res, val_info, val_list)
VALUES(48875, 1, 2, 'IIIIIIIIIIIIII', '0|462740|0|3202|462740|0|0|0|0|0|0|0|1797|1797');


select 1  as typeData, extract(epoch from t.doc_date) * 1000 as docDate, t.doc_id as docID   from mm.m2_trains t   join (select doc_date, doc_id, min(loc_ser)loc_ser from mm.m2_locsec group by doc_date, doc_id)ls on ls.doc_date=t.doc_date and ls.doc_id=t.doc_id join nsi.loc_ser l on l.num_ser=ls.loc_ser where t.st_id_begin=880100000 and t.st_id_end=873300000 and ls.loc_ser=125 limit 10;

select distinct 1 as typeData, m.doc_id as docID, extract(epoch from m.doc_date) * 1000 as docDate
from mm.m2_trains t
join mm.m2_marshrut m on m.doc_date=t.doc_date and m.doc_id=t.doc_id
where ((st_id_begin=873300000 and st_id_end=880100000) or (st_id_begin=880100000 and st_id_end=873300000)) and m.status=0;

select * from mm.m2_marshrut where doc_id=839887871;

select * from mm.e2_preparedataforcalc(1::smallint, to_date('22.01.2020','dd.mm.yyyy'), 839928927);

select * from mm.e2_preparedataforcalc(1::smallint, to_date('22.01.2020','dd.mm.yyyy'), 839887871);
select * from mm.m2_calcresult where doc_id = 839887871;


select * from mm.m2_trains t join mm.m2_marshrut m on m.doc_date=t.doc_date and m.doc_id=t.doc_id where ((st_id_begin=873300000 and st_id_end=880100000) or (st_id_begin=880100000 and st_id_end=873300000)) and m.status=0;

select extract(epoch from to_date('30.06.2020','dd.mm.yyyy')) * 1000 as docDate;

select * from mm.m2_del_marshruts('2020-03-03'::date ,null, 12000090000);
select * from mm.m2_marshrut where doc_id = 830702003;
select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_id in (839823370, 839928927);

select * from ximport.mm2_process_xml('2020-06-29 03:00:00.0'::timestamp,'2020-06-30 03:00:00.0'::timestamp,'2020-06-30 16:28:23.163'::timestamp,"c:/!/mm_dor83depo3_20200629-0000_20200630-0000.xml-001.xml", 1, 83);


select * from mm.m2_marshrut;
select * from mm.ac_preparedataforcalc(1::smallint, to_date('01.01.2020','dd.mm.yyyy'), 839823370);
select 1, doc_date, doc_id from mm.m2_marshrut where doc_id = 839823370;

select 'mm.m2_locsec' as rowComment, loc_ser, rec_sched1, rec_sched2
from mm.m2_locsec where (doc_id = 839823370);

select * from mm.m2_marshrut

select * from mm.m2_sched where (doc_date = to_date('01.02.2020','dd.mm.yyyy')) and (doc_id in (839986533));
select * from mm.m2_sched where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823370) and nord=6);



select loc_ser, * from mm.m2_locsec where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823370));
select * from nsi.loc_koef_pol;
select * from nsi.loc_ser where num_ser = 145 order by num_ser;

select id_vag_property, * from mm.ci_type_vagon;
select num_sostav, * from mm.m2_sched where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823369));
select * from mm.m2_sostav_groupvag where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823369));
select code_resist, code_resist_alg, * from nsi.vag_type;
select * from nsi.vag_resist_alg;
select * from nsi.loc_koef_pol;


select w.rail_type, w.id, m.*
from nsi.path_ab p, nsi.ways w, mm.m2_sched m
where (p.stida = m.st_id) and (p.id_way = w.id) and rail_type=0;

select * from nsi.ways where rail_type = 0;

select * from mm.e2_preparedataforcalc(1::smallint, to_date('22.01.2020','dd.mm.yyyy'), 839928927)
where f_rowcomment='lim';

839833867 839833959

select * from mm.ac_preparedataforcalc(1::smallint, to_date('03.01.2020','dd.mm.yyyy'), 839835077);
select * from mm.m2_sched where (doc_date = to_date('22.01.2020','dd.mm.yyyy')) and (doc_id in (839928927));
select * from mm.m2_sched where (doc_date = to_date('03.01.2020','dd.mm.yyyy')) and (doc_id in (839835077));
select * from mm.m2_trains where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823469));

select * from mm.m2_loctg where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823424));
select * from mm.m2_sched where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823424));
select * from mm.m2_locsec where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823424));
select * from nsi.loc_ser where num_ser = 138;
select * from mm.m2_loctgsec where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823424));

select * from mm.ac_preparedataforcalc(1::smallint, to_date('01.01.2020','dd.mm.yyyy'), 839823465);
select * from mm.m2_loctg where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823465));
select * from mm.m2_sched where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823465));
select * from mm.m2_locsec where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823469));

select * from nsi.categorytrainsdetail order by rangea

select * from nsi.loc_ser where num_ser = 138;
select * from mm.m2_loctgsec where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823465));

select distinct 'mm.m2_locsec' as rowComment, loc_ser, rec_sched1, rec_sched2, count(*) as count_sec,
dt_cp_out, dt_receive_end, dt_handover_begin, dt_cp_in, pr_handover, pr_receive
from mm.m2_locsec
where (doc_id = 839823393)
group by loc_ser, rec_sched1, rec_sched2, dt_cp_out, dt_receive_end, dt_handover_begin, dt_cp_in, pr_handover, pr_receive




select nord_shed, nord, dt_begin, dt_end from mm.m2_sched_manevr where doc_id=839824215;
select * from mm.m2_sched where doc_id=839824215;

select dt_cp_in, dt_cp_out, dt_receive_end, dt_handover_begin, pr_receive, pr_handover, * from mm.m2_locsec
where (pr_handover=2) and (pr_receive=2)
where doc_id in (839835077);
select p_support, * from nsi.loc_ser where name_ser like '2ТЭ116';
select * from nsi.loc_type where code=5;
select * from nsi.loc_resist where id=5;
select * from mm.m2_sostav_groupvag where (doc_date = to_date('22.01.2020','dd.mm.yyyy')) and (doc_id in (839928927));
select * from mm.ci_type_vagon;
select * from nsi.vag_type;
select * from nsi.loc_ser where name_ser ='3ЭС4К';
select * from nsi.loc_ser where name_ser like 'РА%';

select * from mm.m2_sched where doc_id in (
select doc_id from mm.m2_locsec where not (loc_ser in (326, 366)) and loc_ser in (
select num_ser from nsi.loc_ser where code_typeloc in (
select code from nsi.loc_type where type_name like '%электропое%'))) and doc_id = 839841642;

select * from mm.m2_locsec where loc_ser in (
select num_ser from nsi.loc_ser where code_typeloc in (
select * from nsi.loc_type where type_name like '%электропое%'));

select * from nsi.loc_ser where name_ser like 'ЭР2'

select * from mm.m2_sched where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823369));
select * from mm.m2_locsec where (doc_id =839823369);
select * from nsi.loc_ser where num_ser=138;

select * from mm.m2_sostav_groupvag where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823467));
select * from mm.ci_type_vagon
select * from nsi.vag_type
select * from mm.m2_trains where doc_id=839855588;
select * from nsi.categorytrainsdetail where (lower(categoryname) like '%мотор%')
select * from nsi.vag_resist_alg

select * from mm.m2_marshrut where doc_id = 839928927;

select * from mm.m2_sched where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823369));
select * from mm.m2_sostav_groupvag where (doc_date = to_date('01.01.2020','dd.mm.yyyy')) and (doc_id in (839823369));
select * from mm.ci_type_vagon
select * from nsi.vag_type



select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_date='2020-07-01' and doc_id=804449841

select * from mm.m2_sched where doc_id = 804449841;
select * from nsi.path_ab where stida = 873300000 and stidb = 875900000;
select * from nsi.ways where (id = 85060202)

select * from mm.e2_calcresult where doc_id = 268576379;
select * from mm.e2_calcerror where doc_id = 268576379;
select * from mm.e_tmp_calcresultdata where type_res=3;
select * from mm.e_tmp_calcresult where doc_id = 804451095;



1 v_idcalcpkg=1202936
2
1 v_idcalcpkg=1202937
2
1 v_idcalcpkg=1202938

select * from mm.e2_getid2savecalc(1, 1, to_date('01.07.2020','dd.mm.yyyy'), 804451095, 1::smallint, 2::smallint, 242, true, '', null);











select * from mm.e2_calcresult where not texterr is null;
select count(*) from mm.e_tmp_calcresultdata;

select t.* from mm.e_tmp_calcresultdata t where t.id_calcpkg in (select id_calcpkg from mm.e_tmp_calcresult where type_res=3);

select t.* from mm.e_tmp_calcresult t where t.id_calcpkg in (select id_calcpkg from mm.e_tmp_calcresultdata where type_res=3 and doc_id=804451095);
select p_supportstopped, * from nsi.loc_ser where not p_supportstopped is null
select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate, '2s' as flagForCalc from mm.m2_marshrut where doc_date='2020-07-01' and doc_id=804449447;
select type_gr as typeData, th_id as docID, extract(epoch from th_date) * 1000 as docDate, '2s' as flagForCalc from mm.gr_thread where th_date='2020-06-30' and type_gr=101 and th_id=268456757;



select m.doc_date, m.doc_id, e.n_rec1, e.n_rec2, st1.st_name, st2.st_name, ed.type_res, e.coderesultcalc
  from mm.m2_marshrut m
  join mm.m2_marshrut_ext mex on mex.doc_date=m.doc_date and mex.doc_id=m.doc_id
  join mm.e_tmp_calcresult e on e.doc_date=m.doc_date and e.doc_id=m.doc_id
  join mm.e_tmp_calcresultdata ed on ed.id_calcpkg=e.id_calcpkg
  join mm.m2_sched s1 on s1.doc_date=e.doc_date and s1.doc_id=e.doc_id and s1.nord=e.n_rec1
  join mm.m2_sched s2 on s2.doc_date=e.doc_date and s2.doc_id=e.doc_id and s2.nord=e.n_rec2
  join nsi.stations st1 on st1.id=s1.st_id
  join nsi.stations st2 on st2.id=s2.st_id
  where ((mex.st_id_begin=17722 and mex.st_id_end=18240) or (mex.st_id_begin=18240 and mex.st_id_end=17722))
  and e.coderesultcalc>0 and ed.type_res=2;



delete from  mm.e_tmp_calcresultdata;
delete from  mm.e_tmp_calcresult;

select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_date='2020-07-01' and doc_id=804449447



select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_id=804449458;
select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate, '2,22,3' as flagForCalc from mm.m2_marshrut where doc_date='2020-07-01' and doc_id=804449458;

select * from mm.e2_saveresultcalc(104346);
SELECT datname,usename,client_addr,client_port, * FROM pg_stat_activity where client_addr='172.22.12.29';


delete from mm.m2_calcresult where doc_id = 839870328;
delete from  mm.m2_calcerror where doc_id = 839870328;
delete from  mm.e_tmp_calcresultdata where val_info='';;
delete from  mm.e_tmp_calcresult where doc_id = 804449797;

select * from mm.m2_sched where doc_id = 804449555;
select * from mm.m2_sched_manevr where doc_id = 839824334;
select * from mm.m2_locsec where (doc_id = 804471156);
select kpdloc, * from nsi.loc_ser where num_ser=606;
select p_support, p_supportstopped, * from nsi.loc_ser where p_support is null;

select * from mm.m2_sostav_groupvag where doc_id = 839824334;
select * from mm.m2_trains_graphics where doc_id = 804449490;
select * from nsi.vag_type
select * from mm.ci_type_vagon


select distinct 'nsi.loc_koef_polynom' as rowComment, k.id, k.a4, k.a3, k.a2, k.a1, k.a0, k.p_min, k.p_max
from nsi.loc_koef_polynom k, t9
where (k.id in (select t9.id_koef_poly_tract from t9)) and (k.type_koef = 1::smallint)),

select id_koef_poly_tract, * from nsi.loc_ser where num_ser=123;
select loc_ser from mm.m2_locsec group by loc_ser;

select id_koef_poly_tract, * from nsi.loc_ser where num_ser in (239,562,627,321,240,153,328,413,318,230,341,516,322,412,139,134,226,220,160,409,221,852,509,327,234,506,508,530,135,851,640,123,557,326);



select * from mm.ac_preparedataforcalc(1::smallint, to_date('01.07.2020','dd.mm.yyyy'), 804451095);
select * from mm.e2_preparedataforcalc(1::smallint, to_date('01.07.2020','dd.mm.yyyy'), 804451095 ) where f_rowcomment like '%lim_2%';
select * from mm.m2_marshrut where doc_id=804471156;

/*
select distinct 'nsi.ways' as rowComment, tOne.nord, w.rail_type, w.id
from (select nord, st_id from mm.m2_sched where doc_id = 804449515) tOne
left join nsi.path_abinfo p on (p.stida = tOne.st_id) and (p.stidb in (select tTwo.st_id from (select nord, st_id from mm.m2_sched where doc_id = 804449515) tTwo where tTwo.nord = tOne.nord +1))
left join nsi.path_ab tTрree on (tTрree.idfind = p.id) and (tTрree.stidb = p.stidb)
left join nsi.ways w on (tTрree.id_way = w.id);

select nord, st_id from mm.m2_sched where doc_id = 804449515;
select * from nsi.path_abinfo where stida=18164 and stidb=17988;
select * from nsi.path_ab where idfind=81420810000198;
select * from nsi.ways where id in (1461, 1486);
*/


select 'mm.m2_sched' as rowComment, nord, dt_arr, dt_dep, comm_km, w_brutto, w_netto, num_train, num_sostav, st_id
from mm.m2_sched where doc_id = 839823501;

select nord, st_id from mm.m2_sched where doc_id = 804451095;
select * from nsi.path_ab where stida=872050000 and stidb=875920000;
select * from nsi.ways where stan1_id=872050000 and stan2_id=875920000;
select * from nsi.path_abinfo where id=87110871200000 or id=87129871200000;

select nord, st_id from mm.m2_sched where doc_id = 839823501
left join  nsi.path_ab on stida=872050000 and stidb=875920000;
select * from nsi.ways;
select * from nsi.path_abinfo where id=87110871200000 or id=87129871200000;
871100000
871200000

select distinct 'nsi.ways' as rowComment, tOne.nord, w.rail_type, w.id
from (select 'mm.m2_sched' as rowComment, nord, dt_arr, dt_dep, comm_km, w_brutto, w_netto, num_train, num_sostav, st_id from mm.m2_sched where doc_id = 839823501) tOne
left join nsi.path_ab p on (p.stida = tOne.st_id) and (p.stidb in
(select tTwo.st_id from
 (select 'mm.m2_sched' as rowComment, nord, dt_arr, dt_dep, comm_km, w_brutto, w_netto, num_train, num_sostav, st_id from mm.m2_sched where doc_id = 839823501) tTwo
 where tTwo.nord = tOne.nord +1)
)
left join nsi.ways w on (p.id_way = w.id);




select distinct 'nsi.ways' as rowComment, tOne.nord, w.rail_type, w.id
from t1 tOne
left join nsi.path_ab p on (p.stida = tOne.st_id) and (p.stidb in (select tTwo.st_id from t1 tTwo where tTwo.nord = tOne.nord +1))
left join nsi.ways w on (p.id_way = w.id) and (w.dtbegin <= docDate) and (w.dtend >= docDate)),




select 1 as typeData, doc_id as docID, extract(epoch from doc_date) * 1000 as docDate from mm.m2_marshrut where doc_id = 839824334;

select dt_cp_in, dt_cp_out, * from mm.m2_locsec where (doc_id =804451095);
select * from mm.m2_marshrut where doc_id = 839824334;
select minsectioncount, mincountercount, * from nsi.loc_ser where num_ser=138;
select minsectioncount, mincountercount, * from nsi.loc_ser where minsectioncount!= mincountercount;
select * from nsi.loc_type where code=5;
select * from nsi.loc_ch where num_ser=366;
select * from mm.m2_trains  where doc_id = 804451095;
select * from mm.m2_sched where doc_id = 804451095;
select * from mm.gr_sched where th_id = 804451095;
select * from mm.m2_sched_manevr where (doc_id =804451095);
select * from nsi.vag_resist_alg;

select * from mm.m2_loctg where (doc_id =839870328);
select * from mm.m2_locsec where (doc_id =839870328);
select * from mm.m2_sostav_info where (doc_id =839870328);

select * from nsi.loc_ser where name_ser like '%РА%';

select 'mm.gr_sched' as rowComment, tg.doc_id, gs.nord, gs.num_train, gs.dt_arr, gs.dt_dep, gs.st_id, gs.xline
from mm.gr_sched gs, mm.m2_trains_graphics tg
where (gs.th_id=tg.th_id) and (tg.doc_id = 804466880) and (tg.type_gr = 102)
