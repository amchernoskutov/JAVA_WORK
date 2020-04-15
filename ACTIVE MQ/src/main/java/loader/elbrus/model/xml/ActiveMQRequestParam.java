package loader.elbrus.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "ActiveMQRequestParam")
public class ActiveMQRequestParam {

  @Element(name = "url")
  private String url;

  @Element(name = "queueName")
  private String queueName;
}
