package loader.elbrus.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "logAndDataServer")
public class logAndDataServer {
  @Element(name = "name")
  private String name;
}
