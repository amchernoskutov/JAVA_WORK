package loader.elbrus.model.xml;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "logPaths")
public class LogPaths {
  @ElementList(inline = true, entry = "logPath")
  private List<String> logPaths;
  
}
