package com.xmltopostgre.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "Toplivo")
public class Toplivo {
  @Attribute(name = "F_RECUP", required = false)
  private Float f_recup;

  @Attribute(name = "F_HEAT", required = false)
  private Float f_heat;

  @Attribute(name = "N_STBR", required = false)
  private Float n_stbr;

  @Attribute(name = "F_STBR", required = false)
  private Float f_stbr;

  @Attribute(name = "F_SUM", required = false)
  private Float f_sum;

  @Attribute(name = "N_TM", required = false)
  private Float n_tm;

  @Attribute(name = "N_LKM", required = false)
  private Float n_lkm;
  
  @Attribute(name = "N_DEP", required = false)
  private Float n_dep;

  @Attribute(name = "N_MIN_W", required = false)
  private Float n_min_w;

  @Attribute(name = "N_SUM", required = false)
  private Float n_sum;

  @Attribute(name = "S_NSTR", required = false)
  private Integer s_nstr;

  @Attribute(name = "S_RECK", required = false)
  private Integer s_reck;

  @Attribute(name = "S_RECN", required = false)
  private Integer s_recn;

  @Attribute(name = "S_TOPK", required = false)
  private Integer s_topk;

  @Attribute(name = "S_TOPN", required = false)
  private Integer s_topn;

  @Attribute(name = "S_TOPP", required = false)
  private Integer s_topp;

  @Attribute(name = "S_TOPPRED", required = false)
  private Integer s_toppred;
  
  @Attribute(name = "S_TOPS", required = false)
  private Integer s_tops;
  
  
}
