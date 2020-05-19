package asuter.admin.config.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import asuter.admin.config.system.Setting;
import asuter.admin.config.system.SystemConfig;
import asuter.admin.domain.LoaderDatas;
import lombok.Data;

@Data
public class UserConfig {

  private JdbcTemplate jdbcTemplate;
  
  private List <Setting> settings = new ArrayList <Setting>();
  
  private Map<String, SystemConfig> modules = new HashMap<String, SystemConfig>();

  private List<LoaderDatas> books = new ArrayList<LoaderDatas>();
  
  private int progressBar;
}
