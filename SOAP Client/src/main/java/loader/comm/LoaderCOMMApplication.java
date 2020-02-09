package loader.comm;

import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import loader.comm.config.LCConfig;
import loader.comm.log.LCLog;
import loader.comm.manager.ManagerSoapClientCOMM;

@SpringBootApplication
public class LoaderCOMMApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(LoaderCOMMApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    var lcConfig = new LCConfig();
    new LCLog(lcConfig);

    try {
      ArrayList<String> elements =  new ArrayList<String>(Arrays.asList(Arrays.toString(args).replace("]", "").replace("[", "").split(", ")));
      LCLog.Severe(elements.get(0) + " " + elements.get(1));
      new ManagerSoapClientCOMM(Integer.parseInt(elements.get(0)), Integer.parseInt(elements.get(1)), lcConfig);
    } catch (Exception e ) {
      var arg = "";
      for(var a:args) {
        arg=arg + " " + a;
      }
      LCLog.Severe("ERROR read command arguments - " + arg);
    }
  }

}
