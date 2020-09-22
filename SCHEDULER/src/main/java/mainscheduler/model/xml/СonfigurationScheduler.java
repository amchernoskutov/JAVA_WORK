package mainscheduler.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "configurationScheduler")
public class СonfigurationScheduler {
  @Element(name = "logServer")
  private LogServer logServer;
  
  @Element(name = "systemInformations")
  private SystemInformations systemInformation;
  
}
