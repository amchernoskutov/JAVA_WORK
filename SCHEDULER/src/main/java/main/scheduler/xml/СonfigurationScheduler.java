package main.scheduler.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "configurationScheduler")
public class СonfigurationScheduler {
  @Element(name = "logServer")
  private LogServer logServer;
  
  @Element(name = "logPaths")
  private LogPaths logPath;
  
  @Element(name = "systemInformations")
  private SystemInformations systemInformation;
  
}
