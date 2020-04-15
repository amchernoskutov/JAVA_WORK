package loader.svltr.model.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import lombok.Data;

@Data
@Root(name = "RestRequestParam")
public class RestRequestParam {

  @Element(name = "url")
  private String url;

  @Element(name = "urlapi")
  private String urlapi;

  @Element(required=false, name = "username")
  private String username;

  @Element(required=false, name = "password")
  private String password;
}
