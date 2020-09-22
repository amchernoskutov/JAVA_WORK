package loader.elbrus.config.xml;

import java.io.File;
import java.io.StringWriter;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.simpleframework.xml.core.Persister;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import loader.elbrus.LoaderelbrusApplication;
import loader.elbrus.model.xml.ConfigurationELBRUS;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.model.xml.RoadAndTime;
import lombok.Data;

/**
 * MAConfig
 * 
 * Предназначен для чтения и записи конфигурационного файла configloaderelbrus.xml 
 */

@Data
@Service
@EnableConfigurationProperties
public class LAConfig {
  private Persister persister;
  private File file;
  private ConfigurationELBRUS config;
  private DataConfig dataConfig;

  public LAConfig(DataConfig dataConfig) {
    this.dataConfig = dataConfig;
    this.persister = new Persister();
    this.file = new File(dataConfig.getConfigfilepath() + dataConfig.getConfigfilename());
    read();
  }
  
  // Получение времени с которого должен будет сформирован следующий запрос
  public synchronized void addDataStartTime(MRequest mRequest, int codeRoad, Date dataStartTime, Integer requestIntervalTimeMinute) {
    RoadAndTime roadAndTime=mRequest.getRoadsAndTime().stream().filter(item -> item.getCodeRoad()==codeRoad).findFirst().orElse(null);
    roadAndTime.setDataStartTime(
        DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute));
  }

  // Чтение конфигурационного файла configloaderelbrus.xml в классы XML
  public void read() {
    try {
      this.config = persister.read(ConfigurationELBRUS.class, this.file);
    } catch (Exception e) {
      LoaderelbrusApplication.logger.fatal("ERROR read configurational file:" + this.file + ";" + e.getMessage());
    } 
  }
  
  // Запись классов XML в конфигурационный файл configloaderelbrus.xml
  public synchronized void write() {
    try {
      this.persister.write(this.config, file);
    } catch (Exception e) {
      LoaderelbrusApplication.logger.fatal("ERROR write configurational file:" + this.file + "::" + e.getMessage());
    } 
  }
  
  // Получение в одну строку конфигурационного файла формата XML из классов по которым
  // разложен файл configloaderelbrus.xml
  public synchronized String writeToString() {
    StringWriter writer = new StringWriter();
    try {
      this.persister.write(this.config, writer);
    } catch (Exception e) {
      LoaderelbrusApplication.logger.fatal("ERROR write to string configurational file:" + this.file + ";" + e.getMessage());
    }  
    
    String rezult = writer.toString().replaceAll("\n","").replaceAll(" <","<").replaceAll(" >",">");
    while ((rezult.indexOf(" <") != -1) | (rezult.indexOf(" >") != -1)) {
      rezult = rezult.replaceAll(" <","<").replaceAll(" >",">");
    }
    return rezult;
  }
  
}
