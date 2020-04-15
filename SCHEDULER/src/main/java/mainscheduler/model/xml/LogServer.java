package mainscheduler.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "logServer")
public class LogServer {
  @Element(name = "name")
  private String name;
}
