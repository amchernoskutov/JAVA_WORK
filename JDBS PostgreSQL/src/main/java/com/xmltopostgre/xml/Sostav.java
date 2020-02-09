package com.xmltopostgre.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "Sostav")
public class Sostav {

  @Attribute(name = "P_NSTR", required = false)
  private Integer p_nstr;

  @Attribute(name = "S_KOLFUL", required = false)
  private Integer s_kolful;

  @Attribute(name = "S_KOLEMP", required = false)
  private Integer s_kolemp;

  @Attribute(name = "S_RODWAG", required = false)
  private Integer s_rodwag;
}
