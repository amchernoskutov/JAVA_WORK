package asuter.admin.config.system;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Setting{
  private String codeModule;
  private Integer idData;
  private String dataName;
  private String property;
  private String value;
  private String mnemSystem;
}

