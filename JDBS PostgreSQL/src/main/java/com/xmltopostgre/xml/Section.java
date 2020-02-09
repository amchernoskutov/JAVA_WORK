package com.xmltopostgre.xml;

import java.util.Date;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "Section")
public class Section {

  @Attribute(name = "ID_CS", required = false)
  private Integer idcs;

  @Attribute(name = "IS_FIRST", required = false)
  private Integer is_first;
    
  @Attribute(name = "IS_TAX", required = false)
  private Integer is_tax;

  @Attribute(name = "KHIT", required = false)
  private Float khit;

  @Attribute(name = "KRECUP", required = false)
  private Float krecup;

  @Attribute(name = "KTRACT", required = false)
  private Float ktract;

  @Attribute(name = "S_KODDEPO", required = false)
  private Integer s_koddepo;
  
  @Attribute(name = "S_KODDOR", required = false)
  private Integer s_koddor;

  @Attribute(name = "S_NLOK", required = false)
  private Integer s_nlok;

  @Attribute(name = "S_NSTR", required = false)
  private Integer s_nstr;

  @Attribute(name = "S_SEK", required = false)
  private Integer s_sek;

  @Attribute(name = "S_SER", required = false)
  private Integer s_ser;
}
