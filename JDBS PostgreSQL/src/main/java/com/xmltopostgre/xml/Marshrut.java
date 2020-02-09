package com.xmltopostgre.xml;

import java.util.Date;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "Marshrut")
public class Marshrut {

  @Attribute(name = "ID_MM")
  private Integer id_mm;

  @Attribute(name = "M_DATEN", required = false)
  private String m_daten;
  
  @Attribute(name = "M_DATETIMEBR", required = false)
  private Date m_datetimebr;
  
  @Attribute(name = "M_DATETIMEK", required = false)
  private Date m_datetimek;

  @Attribute(name = "M_DATETIMEKPVI", required = false)
  private Date m_datetimekpvi;

  @Attribute(name = "M_DATETIMEKPVX", required = false)
  private Date m_datetimekpvx;

  @Attribute(name = "M_DATETIMEPREK", required = false)
  private Date m_datetimeprek;

  @Attribute(name = "M_DATETIMEPRL", required = false)
  private Date m_datetimeprl;

  @Attribute(name = "M_DATETIMEPS", required = false)
  private Date m_datetimeps;

  @Attribute(name = "M_DATETIMESDL", required = false)
  private Date m_datetimesdl;
  
  @Attribute(name = "M_DEPO", required = false)
  private Integer m_depo ;

  @Attribute(name = "M_NMARSH", required = false)
  private Integer m_nmarsh ;

  @Attribute(name = "M_TABNMASH", required = false)
  private Integer m_tabnmash ;

  @Attribute(name = "M_TABNP1", required = false)
  private Integer m_tabnp1 ;

  @Attribute(name = "STATUS", required = false)
  private Integer status ;

  @Attribute(name = "S_DATETIMEPRE", required = false)
  private Date s_datetimepre;
  
  @ElementList(name = "m_Section", required = false)
  private List<Section> m_section;

  @ElementList(name = "m_Toplivo", required = false)
  private List<Toplivo> m_toplivo;

  @ElementList(name = "m_Poezdka", required = false)
  private List<Poezdka> m_poezdka;

  @ElementList(name = "m_Sostav", required = false)
  private List<Sostav> m_sostav;

  @ElementList(name = "m_Sled", required = false)
  private List<Sled> m_sled;
  
  @ElementList(name = "m_Ntts", required = false)
  private List<Ntts> m_ntts;
}
