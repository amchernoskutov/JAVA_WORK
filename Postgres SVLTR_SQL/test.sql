select asoup_data.*, assigment_data.*
from svltr.asoup_data asoup_data
left join svltr.assigment_data assigment_data on (asoup_data.loc_type = assigment_data.loc_type) and (asoup_data.loc_num = assigment_data.loc_num)


select asoup_data.*
from svltr.asoup_data asoup_data
where asoup_data.train_index <> '000000000000000'
order by asoup_data.train_index;


select assigment_data.*
from svltr.assigment_data assigment_data
where (assigment_data.loc_type = 233) and (assigment_data.loc_num = 1720)

