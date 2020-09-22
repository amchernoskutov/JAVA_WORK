package loader.comm.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "configurationCOMM")
public class ConfigurationCOMM {
  @Element(name = "logAndDataServer")
  private logAndDataServer logAndDataServer;
  
  @Element(name = "SOAPRequestParam")
  private SOAPRequestParam SOAPRequestParam;

  @Element(name = "MQSenderParam")
  private MQSenderParam MQSenderParam;

  @Element(name = "requests")
  private Requests request;
  
}
