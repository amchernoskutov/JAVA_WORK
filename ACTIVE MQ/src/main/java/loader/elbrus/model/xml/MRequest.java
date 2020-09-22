package loader.elbrus.model.xml;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "request")
public class MRequest {
  @Element(name = "id")
  private int id;
  
  @Element(name = "name")
  private String name;

  @Element(name = "timeout")
  private Integer timeout;

  @ElementList(entry="road", inline=true)
  private List<RoadAndTime> roadsAndTime;
  
  @Element(name = "destPath")
  private String destPath;
  
  @Element(name = "typeParam")
  private String typeParam;

  @Element(name = "userParam")
  private String userParam;
}
