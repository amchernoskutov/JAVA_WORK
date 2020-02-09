package loader.comm.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "configurationCOMM")
public class ConfigurationCOMM {
  @Element(name = "logAndDataServer")
  private logAndDataServer logAndDataServer;
  
  @Element(name = "logPaths")
  private LogPaths logPath;
  
  @Element(name = "requests")
  private Requests request;
  
}
