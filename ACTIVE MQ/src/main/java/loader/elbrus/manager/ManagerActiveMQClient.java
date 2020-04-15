package loader.elbrus.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.elbrus.activemq.ActiveMQGraphsAvailable;
import loader.elbrus.activemq.ActiveMQGraphsInfo;
import loader.elbrus.activemq.ActiveMQGraphsWarning;
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.form.MainForm;
import loader.elbrus.log.LALog;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.TimetableInfoMessage;
import loader.elbrus.xml.XMLGraphsInfo;

/**
 * ManagerActiveMQClient
 * 
 * Менеджер определяет вид ActiveMQ запроса, который необходимо создать и генерирует задание на его создание
 * 
 * Если есть отстование по дате получения данных, то процесс последовательно получает все
 * пропцщенные данные. Все данные получает последовательно, если сбой в получении данных, то к
 * получению следующих данных не переходит.
 * 
 */

@Service
public class ManagerActiveMQClient {

  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  @Autowired
  private LAConfig laConfig;

  @Autowired
  private ActiveMQGraphsAvailable activeMQGraphsAvailable;

  @Autowired
  ActiveMQGraphsWarning activeMQGraphsWarning;
  
  @Autowired
  ActiveMQGraphsInfo activeMQGraphsInfo;
  
  @Autowired
  XMLGraphsInfo xmlGraphsInfo;

  /**
   * exec - менеджер создания сообщений на получение информации о наличии графиков движения поездов,
   * самих графиков в том числе прочих и наличия ограничения по скорости движения поездов 
   * @param requestId - id запроса (номер системы из файла configloaderelbrus.xml) 
   * @param requestIntervalTimeMinute (requestIntervalTimeMinute - количество минут за которое запрашиваются данные)
   * @return true - при успешности получения данных, false - при неуспешности
   */
  public boolean exec(int requestId, int requestIntervalTimeMinute) {
    List<TimetableInfoMessage> timetableInfosMessageList = new ArrayList<TimetableInfoMessage>();
    boolean soapRequestSuccessful = true;

    MRequest mRequest = laConfig.getConfig().getRequest().getRequests().stream().filter(item ->
    item.getId() == requestId).findFirst().orElse(null);
    
    // Если нет записи в конфигурационном файле, то выходим
    if (mRequest == null) {
      LALog.Severe("ERROR don't found record in config file requestId=" + requestId);
      return false;
    }
    
    // Если дата запроса данных больше текущей, то выходим
    if (DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime() > (new Date()).getTime()) {
      LALog.Severe("Loger ID " + mRequest.getId() + " name " + mRequest.getName() +
       " can not start. [DataStartTime + RequestIntervalTimeMinute] > [DateNow]." +
       " DataStartTime=" + mRequest.getDataStartTime() +
       " RequestIntervalTimeMinute=" + requestIntervalTimeMinute +
       " DateNow=" + new Date());
      return false;
    }
    
    // Последовательно запрашиваются данные из Эльбруса по всем графикам начиная с даты указанной 
    // в тэге dataStartTime к файле configloaderelbrus.xml
    while ((DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime() < (new Date()).getTime())&&(soapRequestSuccessful)) {
      Date dateStart = mRequest.getDataStartTime();
      Date dateFinish = DateUtils.addMinutes(dateStart, requestIntervalTimeMinute);
      MainForm mainForm = new MainForm();

      switch (requestId) {
        case 101:
        case 102:
        case 103:
          // Получаем информацию о ниличии графиков движения поездов в том числе прочих графиков
          try {
            timetableInfosMessageList = activeMQGraphsAvailable.sendMessage(mRequest, requestIntervalTimeMinute, dateStart, dateFinish);
            if (timetableInfosMessageList.size() == 0) soapRequestSuccessful = false;
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + LALog.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LALog.Severe(message);
            LALog.mainForms.add(mainForm);
            if (LALog.mainForms.size() > LALog.MAX_MAINFORMS_SHOW) LALog.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
           
          if (timetableInfosMessageList.stream().filter(f -> f.getTimetableTur().getIsDefault() == true).count() == 0) {
            soapRequestSuccessful = true;
            break;
          }
           
          // Получаем информацию о графиках движения поездов в том числе прочих графиков
          try {
            soapRequestSuccessful = activeMQGraphsInfo.sendMessage(mRequest, requestIntervalTimeMinute, 
                timetableInfosMessageList.stream().filter(f -> f.getTimetableTur().getIsDefault() == true).findFirst().get().getTimetableTur().getReference(), 
                dateStart, dateFinish);
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + LALog.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LALog.Severe(message);
            LALog.mainForms.add(mainForm);
            if (LALog.mainForms.size() > LALog.MAX_MAINFORMS_SHOW) LALog.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
          break;
        case 104:
          // Получаем информацию об ограничениях скорости 
          try {
            soapRequestSuccessful = activeMQGraphsWarning.sendMessage(mRequest, requestIntervalTimeMinute, dateStart, dateFinish);
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + LALog.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LALog.Severe(message);
            LALog.mainForms.add(mainForm);
            if (LALog.mainForms.size() > LALog.MAX_MAINFORMS_SHOW) LALog.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
          break;
      }
      
      if (soapRequestSuccessful) {
        laConfig.addDataStartTime(mRequest, requestIntervalTimeMinute);
        laConfig.write();
      } else {
        break;
      }
    }

    return soapRequestSuccessful;
  }

}
