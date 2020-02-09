package loader.comm.config;

import java.io.File;
import org.apache.commons.lang3.time.DateUtils;
import org.simpleframework.xml.core.Persister;
import loader.comm.log.LCLog;
import loader.comm.xml.ConfigurationCOMM;
import loader.comm.xml.MRequest;
import lombok.Data;

@Data
public class LCConfig {
  public static final String FILE_CONFIG = "configloadercomm.xml";
  private Persister persister;
  private File file;
  private ConfigurationCOMM config;
  
  public LCConfig() {
    this.persister = new Persister();
    this.file = new File(FILE_CONFIG);
    read();
  }
  
  public void addDataStartTime(MRequest mRequest, Integer requestIntervalTimeMinute) {
    mRequest.setDataStartTime(
        DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute));
  }
  
  public void read() {
    try {
      this.config = persister.read(ConfigurationCOMM.class, this.file);
    } catch (Exception e) {
      LCLog.Severe("ERROR read configurational file:" + this.file + "::" + e.getMessage());
    } 
  }
  
  public void write() {
    try {
      this.persister.write(this.config, file);
    } catch (Exception e) {
      LCLog.Severe("ERROR write configurational file:" + this.file + "::" + e.getMessage());
    } 
  }
}
