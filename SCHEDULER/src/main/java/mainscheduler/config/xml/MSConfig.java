package mainscheduler.config.xml;

import java.io.File;
import java.io.StringWriter;
import org.simpleframework.xml.core.Persister;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import lombok.Data;
import mainscheduler.log.MSLog;
import mainscheduler.model.xml.СonfigurationScheduler;

/**
 * MSConfig
 * 
 * Предназначен для чтения и записи конфигурационного файла configmainscheduler.xml 
 */

@Data
@Service
@EnableConfigurationProperties
public class MSConfig {
  private Persister persister;
  private File file;
  private СonfigurationScheduler config;
  private DataConfig dataConfig;

  public MSConfig(DataConfig dataConfig) {
    this.dataConfig = dataConfig;
    this.persister = new Persister();
    this.file = new File(dataConfig.getConfigfilepath() + dataConfig.getConfigfilename());
    read();
  }
  
  // Чтение конфигурационного файла configmainscheduler.xml в классы XML
  public void read() {
    try {
      this.config = persister.read(СonfigurationScheduler.class, this.file);
    } catch (Exception e) {
      MSLog.Severe("ERROR read configurational file:" + this.file + ";" + e.getMessage());
    } 
  }
  
  // Запись классов XML в конфигурационный файл configmainscheduler.xml
  public void write() {
    try {
      this.persister.write(this.config, System.out);
    } catch (Exception e) {
      MSLog.Severe("ERROR write configurational file:" + this.file + ";" + e.getMessage());
    } 
  }
  
  // Получение в одну строку конфигурационного файла формата XML из классов по которым
  // разложен файл configmainscheduler.xml
  public synchronized String writeToString() {
    StringWriter writer = new StringWriter();
    try {
      this.persister.write(this.config, writer);
    } catch (Exception e) {
      MSLog.Severe("ERROR write to string configurational file:" + this.file + ";" + e.getMessage());
    }  
    
    String rezult = writer.toString().replaceAll("\n","").replaceAll(" <","<").replaceAll(" >",">");
    while ((rezult.indexOf(" <") != -1) | (rezult.indexOf(" >") != -1)) {
      rezult = rezult.replaceAll(" <","<").replaceAll(" >",">");
    }
    return rezult;
  }
  
}
