package loader.svltr.model.xml;

import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "request")
public class MRequest {
  @Element(name = "id")
  private int id;
  
  @Element(name = "name")
  private String name;
  
  @Element(name = "urlapi")
  private String urlapi;

  @Element(name = "dataStartTime")
  private Date dataStartTime;
  
  @Element(name = "destPath")
  private String destPath;
}
