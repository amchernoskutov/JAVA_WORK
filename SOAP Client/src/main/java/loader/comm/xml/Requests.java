package loader.comm.xml;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "requests")
public class Requests {
  @ElementList(inline = true, entry = "request")
  private List<MRequest> requests;
}
