package loader.svltr.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "MQSenderParam")
public class MQSenderParam {

  @Element(name = "name")
  private String name;

  @Element(name = "host")
  private String host;

  @Element(name = "port")
  private Integer port;

  @Element(name = "login")
  private String login;

  @Element(name = "password")
  private String password;

}
