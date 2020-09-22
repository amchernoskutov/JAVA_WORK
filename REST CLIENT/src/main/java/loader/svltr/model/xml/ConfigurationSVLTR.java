package loader.svltr.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "configurationSVLTR")
public class ConfigurationSVLTR {
  @Element(name = "logAndDataServer")
  private logAndDataServer logAndDataServer;
  
  @Element(name = "RestRequestParam")
  private RestRequestParam RestRequestParam;

  @Element(name = "MQSenderParam")
  private MQSenderParam MQSenderParam;

  @Element(name = "requests")
  private Requests request;
  
}
