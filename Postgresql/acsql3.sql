/* электровозы */
select lc.*
from nsi.loc_ser ls, nsi.loc_type lt, mm.m2_locsec lc
where (ls.code_typeloc = lt.code) and (strpos(lower(lt.type_name), 'электровоз') > 0) and
      (lc.loc_ser = ls.num_ser) and (lc.doc_id=839823713);

/* тепловозы */
select lc.*
from nsi.loc_ser ls, nsi.loc_type lt, mm.m2_locsec lc
where (ls.code_typeloc = lt.code) and (strpos(lower(lt.type_name), 'тепловоз') > 0) and
      (lc.loc_ser = ls.num_ser) and (lc.doc_id=839823713);

/* электропоезда МВПС */
select lc.*
from nsi.loc_ser ls, nsi.loc_type lt, mm.m2_locsec lc
where (ls.code_typeloc = lt.code) and (strpos(lower(lt.type_name), 'электропоезд') > 0) and
      (lc.loc_ser = ls.num_ser) and (lc.doc_id in (select t.doc_id from mm.m2_trains_graphics t));

/* электропоезда АМВПС */
select lc.*
from nsi.loc_ser ls, nsi.loc_type lt, mm.m2_locsec lc
where (ls.code_typeloc = lt.code) and
      ((strpos(lower(lt.type_name), 'электровоз') <= 0) and
       (strpos(lower(lt.type_name), 'тепловоз') <= 0) and
       (strpos(lower(lt.type_name), 'электропоезд') <= 0)) and
      (lc.loc_ser = ls.num_ser) and (lc.doc_id=839823565);
