package loader.elbrus.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.form.MainForm;
import java.util.logging.Formatter;

/**
 * MALog
 *  
 * Предназначен для формирования LOG-файлов в которые записывается 
 * информация о сформированных ActiveMQ запросах и полученных ответов.
 * 
 * LOG-файлы могут дублироваться в зависимости он заданных настроек в конфигурационном
 * файле configloaderelbrus.xml 
 */

@Service
public class LALog {

  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final SimpleDateFormat FORMAT_DATE_FILE = new SimpleDateFormat("yyyyMMdd");
  public static final SimpleDateFormat FORMAT_DATE_FILE_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
  public static final SimpleDateFormat FORMAT_DATE_FILE_YYYYMMDDHHMM = new SimpleDateFormat("yyyyMMdd-HHmm");
  public static final SimpleDateFormat FORMAT_DATE_SOAP = new SimpleDateFormat("yyyyMMdd");
  public static final String ENCODINN = "UTF-8";
  public static final boolean CONSOLE_SHOW = false;
  public static final String ADD_LOADERELBRUS_PATH = "ELBRUS";
  public static final String ADD_LOADER_PATH = "LOADERELBRUS";
  public static final String SHORT_LOADERCOMM_NAME = "ELBRUS";
  public static final String SHORT_LOADER_NAME = "ELBRUS";
  public static final String LOG_FILE_FORMAT = ".log";
  public static String oldDate = "";
  public static ArrayList<MainForm> mainForms = new ArrayList<MainForm>();
  public static final Integer MAX_MAINFORMS_SHOW = 20;
  
  static Logger logger = Logger.getLogger("LoaderELBRUS");
  private LAConfig laConfig;

  public LALog(LAConfig managerConfig) {
    this.laConfig = managerConfig;
    initHandlers();
    LALog.Info("LoaderELBRUS started successfully"); 
  }  

  /**
   * initHandlers
   * Инициализиация и создание LOG-файлов, задание количества LOG-файлов определенных
   * конфигурационным файлом. 
   */

  public void initHandlers() {
    logger.setUseParentHandlers(CONSOLE_SHOW);
    
    for (Handler handler : logger.getHandlers()) {
      if (handler instanceof FileHandler) {
        handler.close();
      }
    }
    
    laConfig.getConfig().getLogPath().getLogPaths().forEach(path -> {
      oldDate = FORMAT_DATE_FILE.format(new Date());
      String filePath = laConfig.getConfig().getLogAndDataServer().getName() + path + oldDate + "/" + ADD_LOADER_PATH + "/"; 
      String fileName = SHORT_LOADER_NAME + oldDate + LOG_FILE_FORMAT;

      try {
        makePath(filePath);
        logger.addHandler(makeFileHandler(filePath + fileName));
      } catch (SecurityException|IOException e) {
        System.out.println("Error creating path " + filePath);
        e.printStackTrace();
      }
    });
  }
  
  // Задание параметров с которых начинается каждая строчка в LOG-файлах
  private Handler makeFileHandler(String filePath) throws SecurityException, IOException {  
      Handler fileHandler = new FileHandler(filePath, true);
      fileHandler.setEncoding(ENCODINN);
      fileHandler.setLevel(Level.FINE);
      
      fileHandler.setFormatter(new Formatter() {
        @Override
        public String format(LogRecord record) {
          return record.getSourceClassName() + ";" 
              + FORMAT_DATE.format(new Date()) + ";"
              + record.getSourceMethodName() + ";" 
              + record.getMessage() + "\n";
        }
      });
    return fileHandler;
  }

  // Создание дирректории для хранения LOG-файлов
  private void makePath(String filePath) throws IOException {
    File file = new File(filePath); 
    if(!file.exists()) { 
      file.mkdirs(); 
    }  
  }
  
  // Создание сообщения типа INFO - информационное сообщение 
  public static void Info(String message) {
    logger.log(Level.INFO, message);
  }

  // Создание сообщения типа SEVERE - критичная ошибка 
  public static void Severe(String message) {
    logger.log(Level.SEVERE, message);
  }
}
