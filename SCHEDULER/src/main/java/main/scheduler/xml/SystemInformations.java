package main.scheduler.xml;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "systemInformations")
public class SystemInformations {
  @ElementList(inline = true, entry = "systemInformation")
  private List<SystemInformation> systemInformations;
}
