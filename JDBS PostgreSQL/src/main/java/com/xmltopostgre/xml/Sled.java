package com.xmltopostgre.xml;

import java.util.Date;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "Sled")
public class Sled {

  @Attribute(name = "P_NSTR_B", required = false)
  private Integer p_nstr_b;

  @Attribute(name = "P_NSTR_E", required = false)
  private Integer p_nstr_e;

  @Attribute(name = "P_VIDSLED", required = false)
  private Integer p_vidsled;

  @Attribute(name = "S_NSTR", required = false)
  private Integer s_nstr;
  
}
