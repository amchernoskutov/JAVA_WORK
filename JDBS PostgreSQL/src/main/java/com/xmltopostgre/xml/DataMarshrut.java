package com.xmltopostgre.xml;

import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "DataMarshrut")
public class DataMarshrut {
  
  @ElementList(inline = true, entry = "Marshrut")
  private List<Marshrut> Marshruts;
  
  @Attribute(name = "KODIOMM")
  private Integer kodiomm;

  @Attribute(name = "fversion")
  private String fversion;
}

