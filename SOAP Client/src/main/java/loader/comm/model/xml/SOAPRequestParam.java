package loader.comm.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "SOAPRequestParam")
public class SOAPRequestParam {

  @Element(name = "url")
  private String url;

  @Element(name = "soapActionOPER")
  private String soapActionOPER;
  
  @Element(name = "soapActionNSI")
  private String soapActionNSI;


  @Element(required=false, name = "login")
  private String login;

  @Element(required=false, name = "password")
  private String password;
}
