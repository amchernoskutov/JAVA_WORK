package loader.comm.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import loader.comm.config.LCConfig;
import lombok.Data;

@Data
public class LCLog {
  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a zzz");
  public static final SimpleDateFormat FORMAT_DATE_FILE_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
  public static final SimpleDateFormat FORMAT_DATE_FILE_YYYYMMDDHHMM = new SimpleDateFormat("yyyyMMdd-HHmm");
  public static final SimpleDateFormat FORMAT_DATE_SOAP = new SimpleDateFormat("yyyy-MM-dd");
  public static final String ENCODINN = "UTF-8";
  public static final boolean CONSOLE_SHOW = true;
  public static final String ADD_LOADERCOMM_PATH = "COMM";
  public static final String SHORT_LOADERCOMM_NAME = "COMM";
  public static final String LOG_FILE_FORMAT = ".log";
  public static String oldDate = "";
  
  static Logger logger = Logger.getLogger("MainScheduler");
  private LCConfig lcConfig;

  public LCLog(LCConfig lcConfig) {
    this.lcConfig = lcConfig;
    initHandlers();
  }  

  public void initHandlers() {
    logger.setUseParentHandlers(CONSOLE_SHOW);
    
    for (var handler : logger.getHandlers()) {
      if (handler instanceof FileHandler) {
        handler.close();
      }
    }
    
    lcConfig.getConfig().getLogPath().getLogPaths().forEach(path -> {
      oldDate = FORMAT_DATE_FILE_YYYYMMDD.format(new Date());
      var filePath = lcConfig.getConfig().getLogAndDataServer().getName() + path + oldDate + "/" + ADD_LOADERCOMM_PATH + "/"; 
      var fileName = SHORT_LOADERCOMM_NAME + oldDate + LOG_FILE_FORMAT;

      try {
        makePath(filePath);
        logger.addHandler(makeFileHandler(filePath + fileName));
      } catch (SecurityException|IOException e) {
        System.out.println("Error creating path " + filePath);
        e.printStackTrace();
      }
    });
  }
  
  private Handler makeFileHandler(String filePath) throws SecurityException, IOException {  
      Handler fileHandler = new FileHandler(filePath, true);
      fileHandler.setEncoding(ENCODINN);
      fileHandler.setLevel(Level.FINE);
      
      fileHandler.setFormatter(new Formatter() {
        @Override
        public String format(LogRecord record) {
          return record.getSourceClassName() + "::" 
              + FORMAT_DATE.format(new Date()) + "::"
              + record.getSourceMethodName() + "::" 
              + record.getMessage() + "\n";
        }
      });
    return fileHandler;
  }

  private void makePath(String filePath) throws IOException {
    var path = new File(filePath); 
    if(!path.exists()) { 
      path.mkdirs(); 
    }  
  }
  
  public static void Info(String message) {
    logger.log(Level.INFO, message);
  }

  public static void Severe(String message) {
    logger.log(Level.SEVERE, message);
  }
}
