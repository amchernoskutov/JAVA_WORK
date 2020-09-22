package loader.elbrus.model.xml;

import java.util.Date;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name="road")
public class RoadAndTime {

  @Attribute(name="codeRoad")
  private int codeRoad;
  
  @Attribute(name="dataStartTime")
  private Date dataStartTime;
  
  @Attribute(name = "url")
  private String url;

  @Attribute(name = "queueName")
  private String queueName;

}
