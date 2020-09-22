alter table nsi.loc_ser
	add koefaadditionalloss real;

comment on column nsi.loc_ser.koefaadditionalloss is 'Коэффициент, определяющий дополнительные потери электроэнергии, связанные с движением на маневренной позиции контроллера машиниста';

update nsi.loc_ser set koefaadditionalloss=1;
update nsi.loc_ser set koefaadditionalloss=0.9 where num_ser in (306, 366, 313, 371, 372, 318, 316, 322, 324, 321, 330, 327, 326, 328, 310);
