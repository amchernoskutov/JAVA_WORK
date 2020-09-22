package mainscheduler.model.xml;

import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "systemInformation")
public class SystemInformation {
  @Element(name = "id")
  private int id;
  
  @Element(name = "name")
  private String name;
  
  @Element(name = "startTime")
  private Date startTime;
  
  @Element(name = "URLRESTService")
  private String URLRESTService;
  
  @Element(name = "intervalTimeMinute")
  private int intervalTimeMinute;
}
