package loader.elbrus.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "configurationELBRUS")
public class ConfigurationELBRUS {
  @Element(name = "logAndDataServer")
  private logAndDataServer logAndDataServer;
  
  @Element(name = "logPaths")
  private LogPaths logPath;

  @Element(name = "ActiveMQRequestParam")
  private ActiveMQRequestParam activeMQRequestParam;

  @Element(name = "MQSenderParam")
  private MQSenderParam MQSenderParam;

  @Element(name = "requests")
  private Requests request;
  
}
