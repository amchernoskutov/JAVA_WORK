package asuter.admin.config.system;

import java.util.HashSet;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SystemConfig {

  private Map<String, String> systems;

  private HashSet<ParamConfig> params;

}
