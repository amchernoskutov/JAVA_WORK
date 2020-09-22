package loader.elbrus.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.elbrus.LoaderelbrusApplication;
import loader.elbrus.activemq.ActiveMQGaps;
import loader.elbrus.activemq.ActiveMQGraphsAvailable;
import loader.elbrus.activemq.ActiveMQGraphsInfo;
import loader.elbrus.activemq.ActiveMQLimits;
import loader.elbrus.activemq.ActiveMQMarks;
import loader.elbrus.activemq.ActiveMQWarning;
import loader.elbrus.config.data.GeneralData;
import loader.elbrus.config.data.LBData;
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.form.MainForm;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.model.xml.RoadAndTime;
import loader.elbrus.proto.ElbrusProto.TimetableInfoMessage;

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
  private LBData lbData; 

  /**
   * exec - менеджер создания сообщений на получение информации о наличии графиков движения поездов,
   * самих графиков в том числе прочих и наличия ограничения по скорости движения поездов 
   * @param requestId - id запроса (номер системы из файла configloaderelbrus.xml) 
   * @param requestIntervalTimeMinute (requestIntervalTimeMinute - количество минут за которое запрашиваются данные)
   * @return true - при успешности получения данных, false - при неуспешности
   */
  public boolean exec(int requestId, int codeRoad, Date dataStartTime, String url, String queueName, int requestIntervalTimeMinute) {
    List<TimetableInfoMessage> timetableInfosMessageList = new ArrayList<TimetableInfoMessage>();
    boolean soapRequestSuccessful = true;

    MRequest mRequest = laConfig.getConfig().getRequest().getRequests().stream().filter(item ->
    item.getId() == requestId).findFirst().orElse(null);
    
    // Если нет записи в конфигурационном файле, то выходим
    if (mRequest == null) {
      LoaderelbrusApplication.logger.fatal("ERROR don't found record in config file requestId=" + requestId);
      return false;
    }
    
    // Если дата запроса данных больше текущей, то выходим
    if (DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute).getTime() > (new Date()).getTime()) {
      LoaderelbrusApplication.logger.fatal("Loger ID " + mRequest.getId() + " name " + mRequest.getName() +
       " can not start. [DataStartTime + RequestIntervalTimeMinute] > [DateNow]." +
       " DataStartTime=" + dataStartTime +
       " RequestIntervalTimeMinute=" + requestIntervalTimeMinute +
       " DateNow=" + new Date());
      return false;
    }
    
    // Последовательно запрашиваются данные из Эльбруса по всем графикам начиная с даты указанной 
    // в тэге dataStartTime к файле configloaderelbrus.xml
    while ((DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute).getTime() < (new Date()).getTime())&&(soapRequestSuccessful)) {
      Date dateStart = dataStartTime;
      Date dateFinish = DateUtils.addMinutes(dateStart, requestIntervalTimeMinute);
      MainForm mainForm = new MainForm();

      switch (requestId) {
        case 101:
        case 102:
        case 103:
          // Получаем информацию о ниличии графиков движения поездов в том числе прочих графиков
          try {
            ActiveMQGraphsAvailable activeMQGraphsAvailable = new ActiveMQGraphsAvailable(lbData);
            
            timetableInfosMessageList = activeMQGraphsAvailable.sendMessage(mRequest, codeRoad, requestIntervalTimeMinute, dateStart, dateFinish,
                url, queueName);
            if (timetableInfosMessageList.size() == 0) {
              soapRequestSuccessful = false;
            }
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + GeneralData.FORMAT_DATE.format(dataStartTime) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LoaderelbrusApplication.logger.fatal(message);
            GeneralData.mainForms.add(mainForm);
            if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
           
          if (timetableInfosMessageList.stream().filter(f -> f.getTimetableTur().getIsDefault() == true).count() == 0) {
            soapRequestSuccessful = false;
            break;
          }
           
          // Получаем информацию о графиках движения поездов в том числе прочих графиков
          try {
            ActiveMQGraphsInfo activeMQGraphsInfo = new ActiveMQGraphsInfo(laConfig, lbData);

            soapRequestSuccessful = activeMQGraphsInfo.sendMessage(mRequest, codeRoad, requestIntervalTimeMinute, 
                timetableInfosMessageList.stream().filter(f -> f.getTimetableTur().getIsDefault() == true).findFirst().get().getTimetableTur().getReference(), 
                dateStart, dateFinish, url, queueName);
            
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + GeneralData.FORMAT_DATE.format(dataStartTime) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LoaderelbrusApplication.logger.fatal(message);
            GeneralData.mainForms.add(mainForm);
            if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
          break;
        case 104:
          // Получаем информацию об ограничениях скорости 
          try {
            ActiveMQWarning activeMQWarning = new ActiveMQWarning(laConfig, lbData);
            soapRequestSuccessful = activeMQWarning.sendMessage(mRequest, codeRoad, requestIntervalTimeMinute, dateStart, dateFinish, url, queueName);
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + GeneralData.FORMAT_DATE.format(dataStartTime) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LoaderelbrusApplication.logger.fatal(message);
            GeneralData.mainForms.add(mainForm);
            if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
          break;
        case 105:
          // Получаем информацию о пометках 
          try {
            ActiveMQMarks activeMQMarks = new ActiveMQMarks(laConfig, lbData);
            soapRequestSuccessful = activeMQMarks.sendMessage(mRequest, codeRoad, requestIntervalTimeMinute, dateStart, dateFinish, url, queueName);
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + GeneralData.FORMAT_DATE.format(dataStartTime) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LoaderelbrusApplication.logger.fatal(message);
            GeneralData.mainForms.add(mainForm);
            if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
          break;
        case 106:
          // Получаем информацию об ограничениях 
          try {
            ActiveMQLimits activeMQLimits = new ActiveMQLimits(laConfig, lbData);
            soapRequestSuccessful = activeMQLimits.sendMessage(mRequest, codeRoad, requestIntervalTimeMinute, dateStart, dateFinish, url, queueName);
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + GeneralData.FORMAT_DATE.format(dataStartTime) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LoaderelbrusApplication.logger.fatal(message);
            GeneralData.mainForms.add(mainForm);
            if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
          break;
        case 107:
          // Получаем информацию об окнах 
          try {
            ActiveMQGaps activeMQGaps = new ActiveMQGaps(laConfig, lbData);
            soapRequestSuccessful = activeMQGaps.sendMessage(mRequest, codeRoad, requestIntervalTimeMinute, dateStart, dateFinish, url, queueName);
          } catch (Exception e) {
            String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
            mainForm.setName(message);
            message = message + ";ERROR create ActiveMQ request on date:" + GeneralData.FORMAT_DATE.format(dataStartTime) + 
                ":" + e.getMessage();
            mainForm.setRequest(message);
            LoaderelbrusApplication.logger.fatal(message);
            GeneralData.mainForms.add(mainForm);
            if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
            soapRequestSuccessful = false;
          }
          break;
      }
      if (soapRequestSuccessful) {
        laConfig.addDataStartTime(mRequest, codeRoad, dataStartTime, requestIntervalTimeMinute);
        laConfig.write();
        RoadAndTime roadAndTime = mRequest.getRoadsAndTime().stream().filter(item -> item.getCodeRoad()==codeRoad).findFirst().orElse(null);
        dataStartTime = roadAndTime.getDataStartTime();
      } else {
        break;
      }
    }

    return soapRequestSuccessful;
  }

}
