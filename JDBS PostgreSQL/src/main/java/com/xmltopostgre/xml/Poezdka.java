package com.xmltopostgre.xml;

import java.util.Date;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "Poezdka")
public class Poezdka {
  @Attribute(name = "ARR_DT_SHD", required = false)
  private Date arr_dt_shd;

  @Attribute(name = "ID_TRAIN_GVC", required = false)
  private Integer id_traingvc;

  @Attribute(name = "P_BRUTTO", required = false)
  private Integer p_brutto;

  @Attribute(name = "P_DATETIMEOTPR", required = false)
  private Date p_datetimeotpr;

  @Attribute(name = "P_DATETIMEPR", required = false)
  private Date p_datetimepr;
  
  @Attribute(name = "P_ESR", required = false)
  private Integer p_esr;

  @Attribute(name = "P_FXOZRAB", required = false)
  private Float p_fxozrab;

  @Attribute(name = "P_HOZRAB", required = false)
  private Integer p_hozrab;

  @Attribute(name = "P_FMANEVR", required = false)
  private Float p_fmanevr;

  @Attribute(name = "P_MANEVR", required = false)
  private Integer p_manevr;

  @Attribute(name = "P_NMANEVR", required = false)
  private Float p_nmanevr;
  
  @Attribute(name = "P_FACT", required = false)
  private Float p_fact;

  @Attribute(name = "P_HIT", required = false)
  private Float p_hit;

  @Attribute(name = "P_NETTO", required = false)
  private Integer p_netto;

  @Attribute(name = "P_NORM", required = false)
  private Float p_norm;

  @Attribute(name = "P_NSTR", required = false)
  private Integer p_nstr;

  @Attribute(name = "P_NXOZRAB", required = false)
  private Float p_nxozrab;
  
  @Attribute(name = "P_OTKL", required = false)
  private Integer p_otkl;

  @Attribute(name = "P_POEZD", required = false)
  private Integer p_poezd;

  @Attribute(name = "P_PRODZS", required = false)
  private Integer p_prodzs;

  @Attribute(name = "P_PROBEG", required = false)
  private Float p_probeg;

  @Attribute(name = "P_RECUP", required = false)
  private Float p_recup;

  @Attribute(name = "P_RODRABOT", required = false)
  private Integer p_rodrabot;

  @Attribute(name = "S_KOLAXIS", required = false)
  private Integer s_kolaxis;
}
