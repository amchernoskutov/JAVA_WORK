package mainscheduler.config.xml;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DataConfig
 * 
 * Класс для считывания логина, пароля, пути к конфигурационному файлу после старта системы
 */

@Configuration 
@ConfigurationProperties(prefix = "mainscheduler")
public class DataConfig {
  private String username;
  private String password;
  private String configfilepath;
  private String configfilename;
  
  public String getUsername() {
    return username;
  }
  public void setUsername(final String username) {
    this.username = username;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(final String password) {
    this.password = password;
  }
  public String getConfigfilepath() {
    return configfilepath;
  }
  public void setConfigfilepath(final String configfilepath) {
    this.configfilepath = configfilepath;
  }
  public String getConfigfilename() {
    return configfilename;
  }
  public void setConfigfilename(final String configfilename) {
    this.configfilename = configfilename;
  }
  
  
}
