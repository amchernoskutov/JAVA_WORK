package mainscheduler.config.data;

import java.text.SimpleDateFormat;
import org.springframework.stereotype.Service;

/**
 * MSLog
 *  
 * Предназначен для формирования LOG-файлов в которые записывается 
 * информация о сформированных SOAP запросах и полученных ответов.
 * 
 * LOG-файлы могут дублироваться в зависимости он заданных настроек в конфигурационном
 * файле configmainscheduler.xml 
 */

@Service
public class GeneralData {
  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
