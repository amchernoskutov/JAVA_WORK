package main.scheduler.config;

import java.io.File;
import java.io.StringWriter;
import org.simpleframework.xml.core.Persister;
import org.springframework.stereotype.Service;
import lombok.Data;
import main.scheduler.log.MSLog;
import main.scheduler.xml.СonfigurationScheduler;

@Data
@Service
public class MSConfig {
  public static final String FILE_CONFIG = "configmainscheduler.xml";
  private Persister persister;
  private File file;
  private СonfigurationScheduler config;
  
  public MSConfig() {
    this.persister = new Persister();
    this.file = new File(FILE_CONFIG);
    read();
  }
  
  public void read() {
    try {
      this.config = persister.read(СonfigurationScheduler.class, this.file);
    } catch (Exception e) {
      MSLog.Severe("ERROR read configurational file:" + this.file + "::" + e.getMessage());
    } 
  }
  
  public void write() {
    try {
      this.persister.write(this.config, System.out);
    } catch (Exception e) {
      MSLog.Severe("ERROR write configurational file:" + this.file + "::" + e.getMessage());
    } 
  }
  
  public String writeToString() {
    StringWriter writer = new StringWriter();
    try {
      this.persister.write(this.config, writer);
    } catch (Exception e) {
      MSLog.Severe("ERROR write to string configurational file:" + this.file + "::" + e.getMessage());
    }  
    
    var rezult = writer.toString().replaceAll("\n","").replaceAll(" <","<").replaceAll(" >",">");
    while ((rezult.indexOf(" <") != -1) | (rezult.indexOf(" >") != -1)) {
      rezult = rezult.replaceAll(" <","<").replaceAll(" >",">");
    }
    return rezult;
  }
  
}
