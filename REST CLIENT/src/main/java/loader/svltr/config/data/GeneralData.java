package loader.svltr.config.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import loader.svltr.form.MainForm;

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
  public static final String ADD_LOADERSVLTR_PATH = "SVLTR";
  public static final String SHORT_LOADERSVLTR_NAME = "SVLTR";
  public static ArrayList<MainForm> mainForms = new ArrayList<MainForm>();
  public static final Integer MAX_MAINFORMS_SHOW = 20;
}
