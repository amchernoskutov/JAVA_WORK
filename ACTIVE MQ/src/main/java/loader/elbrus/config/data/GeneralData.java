package loader.elbrus.config.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import loader.elbrus.form.MainForm;

/**
 * GeneralData
 *  
 * Предназначен для конфигурации общих параметров для классов 
 */

@Service
public class GeneralData {
  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public static final SimpleDateFormat FORMAT_DATE_FILE_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
  public static final SimpleDateFormat FORMAT_DATE_FILE_YYYYMMDDHHMM = new SimpleDateFormat("yyyyMMdd-HHmm");
  public static final String ENCODINN = "UTF-8";
  public static final String ADD_LOADERELBRUS_PATH = "ELBRUS";
  public static final String ADD_LOADER_PATH = "LOADERELBRUS";
  public static final String SHORT_LOADERCOMM_NAME = "ELBRUS";
  public static ArrayList<MainForm> mainForms = new ArrayList<MainForm>();
  public static final Integer MAX_MAINFORMS_SHOW = 20;
}
