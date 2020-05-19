package asuter.admin.config.system;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParamConfig {

  private String request;
  private Integer idRequest;
  private String param;
  private String value;
  private String mnemSystem;
}
