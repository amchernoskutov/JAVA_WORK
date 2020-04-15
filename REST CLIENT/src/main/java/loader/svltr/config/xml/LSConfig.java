package loader.svltr.config.xml;

import java.io.File;
import java.io.StringWriter;
import org.apache.commons.lang3.time.DateUtils;
import org.simpleframework.xml.core.Persister;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import loader.svltr.log.LSLog;
import loader.svltr.model.xml.ConfigurationSVLTR;
import loader.svltr.model.xml.MRequest;
import lombok.Data;

/**
 * MSConfig
 * 
 * Предназначен для чтения и записи конфигурационного файла configloadersvltr.xml 
 */

@Data
@Service
@EnableConfigurationProperties
public class LSConfig {
  private Persister persister;
  private File file;
  private ConfigurationSVLTR config;
  private DataConfig dataConfig;

  public LSConfig(DataConfig dataConfig) {
    this.dataConfig = dataConfig;
    this.persister = new Persister();
    this.file = new File(dataConfig.getConfigfilepath() + dataConfig.getConfigfilename());
    read();
  }
  
  // Получение времени с которого должен будет сформирован следующий SOAP запрос
  public synchronized void addDataStartTime(MRequest mRequest, Integer requestIntervalTimeMinute) {
    mRequest.setDataStartTime(
        DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute));
  }

  // Чтение конфигурационного файла configloadersvltr.xml в классы XML
  public void read() {
    try {
      this.config = persister.read(ConfigurationSVLTR.class, this.file);
    } catch (Exception e) {
      LSLog.Severe("ERROR read configurational file:" + this.file + ";" + e.getMessage());
    } 
  }
  
  // Запись классов XML в конфигурационный файл configloadersvltr.xml
  public synchronized void write() {
    try {
      this.persister.write(this.config, file);
    } catch (Exception e) {
      LSLog.Severe("ERROR write configurational file:" + this.file + "::" + e.getMessage());
    } 
  }
  
  // Получение в одну строку конфигурационного файла формата XML из классов по которым
  // разложен файл configloadersvltr.xml
  public synchronized String writeToString() {
    StringWriter writer = new StringWriter();
    try {
      this.persister.write(this.config, writer);
    } catch (Exception e) {
      LSLog.Severe("ERROR write to string configurational file:" + this.file + ";" + e.getMessage());
    }  
    
    String rezult = writer.toString().replaceAll("\n","").replaceAll(" <","<").replaceAll(" >",">");
    while ((rezult.indexOf(" <") != -1) | (rezult.indexOf(" >") != -1)) {
      rezult = rezult.replaceAll(" <","<").replaceAll(" >",">");
    }
    return rezult;
  }
  
}
