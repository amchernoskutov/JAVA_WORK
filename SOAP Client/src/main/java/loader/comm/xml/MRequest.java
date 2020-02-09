package loader.comm.xml;

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

  @Element(name = "nameTurn")
  private String nameTurn;
  
  @Element(name = "namespaceURI")
  private String namespaceURI;

  @Element(name = "serviceName")
  private String serviceName;

  @Element(name = "url")
  private String url;

  @Element(required=false, name = "login")
  private String login;

  @Element(required=false, name = "password")
  private String password;

  @Element(name = "dataStartTime")
  private Date dataStartTime;
  
  @Element(name = "destPath")
  private String destPath;
}
