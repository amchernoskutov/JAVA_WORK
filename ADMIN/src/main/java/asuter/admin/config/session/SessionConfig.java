package asuter.admin.config.session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import asuter.admin.config.system.ParamConfig;
import asuter.admin.config.system.Setting;
import asuter.admin.config.system.SystemConfig;
import lombok.Data;

@Data
@Service
public class SessionConfig {
  @Autowired
  private DataSource dataSource;

  private ConcurrentHashMap<String, UserConfig> sessionConfig;

  /**
   * Конструктор
   */
  public SessionConfig() {
    this.sessionConfig = new ConcurrentHashMap<String, UserConfig>();
  }

  /**
   * Получить имя пользователя авторизованного в системе
   * @return имя пользователя 
   */
  public String getUser() throws Exception {
    return SecurityContextHolder.getContext().getAuthentication().getName().toString();
  }

  /**
   * Получить экземпляр класса UserConfig текущей сессии 
   * @return
   */
  public UserConfig getUserConfig() throws Exception {
    return sessionConfig.get(getUser());
  }

  /**
   * Инициализация сессии
   */
  public void initSession(String username) throws Exception {
    sessionConfig.put(username, new UserConfig());
  }

  /**
   * Инициализация JdbcTemplate
   * @param username имя пользователя
   * @param password пароль пользователя
   * @return 0 - инициализация была ранее, 1 - успешно
   * @throws Exception 
   */
  public int initJdbcTemplate(String username, String password) throws Exception {
    if (sessionConfig.get(username).getJdbcTemplate() != null) return 0;
    
    SimpleDriverDataSource dSource = new SimpleDriverDataSource();
    
    dSource.setDriverClass(org.postgresql.Driver.class);
    dSource.setUrl(dataSource.getConnection().getMetaData().getURL());
      
    dSource.setUsername(username);
    dSource.setPassword(password);

    JdbcTemplate jdbcTemplate = new JdbcTemplate(dSource);
      
    sessionConfig.get(username).setJdbcTemplate(jdbcTemplate);

    return 1;
  }
  
  /**
   * Инициализация конфигурации
   * @return 0 - инициализация была ранее, 1 - успешно
   */
  public int InitModuleConfig () throws Exception {
    if (!sessionConfig.get(getUser()).getModules().isEmpty()) return 0;
      
    sessionConfig.get(getUser()).setSettings(getData());
    createConfig();
    return 1;
  }
  
  public String getSett(String code_module, String property) throws Exception {
    return sessionConfig.get(getUser()).getModules().get(code_module.toLowerCase()).getSystems().get(property.toLowerCase());
  }
  
  public List <Setting> getData() throws Exception {
    return sessionConfig.get(getUser()).getJdbcTemplate().query("select code_module, id_data, data_name, property, val, mnem_system from config.get_settings()", 
        new RowMapper<Setting>() {

          @Override
          public Setting mapRow(ResultSet rs, int rowNum) throws SQLException {
            Setting setting = new Setting();
            if (rs.getString("code_module") == null) throw new NullPointerException("Exception not initialization code_module");
            setting.setCodeModule(rs.getString("code_module").toLowerCase());
            setting.setIdData(Math.toIntExact(rs.getLong("id_data")));
            if (rs.getString("data_name") == null) {
              setting.setDataName(rs.getString("data_name"));
            } else {
              setting.setDataName(rs.getString("data_name").toLowerCase());
            }
            if (rs.getString("property") == null) throw new NullPointerException("Exception not initialization property");
            setting.setProperty(rs.getString("property").toLowerCase());
            if (rs.getString("val") == null) throw new NullPointerException("Exception not initialization val");
            setting.setValue(rs.getString("val"));
            setting.setMnemSystem(rs.getString("mnem_system"));
            return setting;
          }
        });
  }
  
  public void createConfig() throws Exception {
    sessionConfig.get(getUser()).getSettings().forEach(item -> {
      try {
        if (sessionConfig.get(getUser()).getModules().get(item.getCodeModule()) == null) {
          SystemConfig systemConfig = new SystemConfig();

          if (item.getDataName() == null) {
            Map<String, String> systems = new HashMap<String, String>();
            systems.put(item.getProperty(), item.getValue());
          
            systemConfig.setSystems(systems);
          
            HashSet<ParamConfig> params = new HashSet<ParamConfig>();
            systemConfig.setParams(params);
          } else {
            ParamConfig paramConfig = new ParamConfig();
            paramConfig.setParam(item.getProperty());
            paramConfig.setRequest(item.getDataName());
            paramConfig.setIdRequest(item.getIdData());
            paramConfig.setValue(item.getValue());
            paramConfig.setMnemSystem(item.getMnemSystem());
          
            HashSet<ParamConfig> params = new HashSet<ParamConfig>();
            params.add(paramConfig);
          
            systemConfig.setParams(params);
          
            Map<String, String> systems = new HashMap<String, String>();
            systemConfig.setSystems(systems);
          }
        
          sessionConfig.get(getUser()).getModules().put(item.getCodeModule(), systemConfig);
        } else {
        
          if (item.getDataName() == null) {
            if (sessionConfig.get(getUser()).getModules().get(item.getCodeModule()).getSystems().isEmpty()) {
              Map<String, String> systems = new HashMap<String, String>();
              systems.put(item.getProperty(), item.getValue());
            
              sessionConfig.get(getUser()).getModules().get(item.getCodeModule()).setSystems(systems);
            } else {
              sessionConfig.get(getUser()).getModules().get(item.getCodeModule()).getSystems().put(item.getProperty(), item.getValue());
            }
          } else {
            if (sessionConfig.get(getUser()).getModules().get(item.getCodeModule()).getParams().isEmpty()) {
              ParamConfig paramConfig = new ParamConfig();
              paramConfig.setParam(item.getProperty());
              paramConfig.setRequest(item.getDataName());
              paramConfig.setIdRequest(item.getIdData());
              paramConfig.setValue(item.getValue());
              paramConfig.setMnemSystem(item.getMnemSystem());
          
              HashSet<ParamConfig> params = new HashSet<ParamConfig>();
              params.add(paramConfig);
          
              sessionConfig.get(getUser()).getModules().get(item.getCodeModule()).setParams(params);
            } else {
              ParamConfig paramConfig = new ParamConfig();
              paramConfig.setParam(item.getProperty());
              paramConfig.setRequest(item.getDataName());
              paramConfig.setIdRequest(item.getIdData());
              paramConfig.setValue(item.getValue());
              paramConfig.setMnemSystem(item.getMnemSystem());
          
              sessionConfig.get(getUser()).getModules().get(item.getCodeModule()).getParams().add(paramConfig);
            }
          }
        
        }
      } catch(Exception e) {
        //
      }
    });
  }

  

}
