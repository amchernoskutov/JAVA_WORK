package loader.comm.model.xml;

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
}

