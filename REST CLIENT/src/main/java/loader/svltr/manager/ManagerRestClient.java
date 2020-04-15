package loader.svltr.manager;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.svltr.config.xml.LSConfig;
import loader.svltr.log.LSLog;
import loader.svltr.model.xml.MRequest;
import loader.svltr.restclients.RestClient;

/**
 * ManagerRestClient
 * 
 * Менеджер определяет вид REST запроса, который необходимо создать и
 * генерирует задание на создание запроса. 
 * 
 * Если есть отстование по дате получения данных, то процесс последовательно получает все пропцщенные данные.
 * Все данные получает последовательно, если сбой в получении данных, то к получению следующих данных не переходит.
 * 
 * Если от сервиса СВЛ ТР приходит ответ с кодом 400 и информацией когда сервис будет доступен. Запрос
 * на получение данных формируется заново, через интервал времени указанный в ответе.
 * 
 */

@Service
public class ManagerRestClient {
  @Autowired
  private LSConfig lsConfig;
  
  @Autowired
  private RestClient restClient; 
  
  public boolean exec(int requestId, int requestIntervalTimeMinute) {
    MRequest mRequest = lsConfig.getConfig().getRequest().getRequests().stream().filter(item -> item.getId() == requestId).findFirst().orElse(null); 

    // Если нет записи в конфигурационном файле, то выходим
    if (mRequest == null) {
      LSLog.Severe("ERROR don't found record in config file requestId=" + requestId);
      return false;  
    }
    
    // Если дата запроса данных больше текущей, то выходим
    if (DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime() > (new Date()).getTime()) {
      LSLog.Severe("Loger ID " + mRequest.getId() + " name " + mRequest.getName() + 
                " can not start. [DataStartTime + RequestIntervalTimeMinute] > [DateNow]." + 
                " DataStartTime=" + mRequest.getDataStartTime() + 
                " RequestIntervalTimeMinute=" + requestIntervalTimeMinute +  
                " DateNow=" + new Date());
      return false;
    }

    Boolean soapRequestSuccessful = true;

// На реальных данных нужно будет раскоментарить чтобы данные запрашивались в случае отставания    
//    while ((DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime() < (new Date()).getTime())&&(soapRequestSuccessful)) {
      switch (requestId) {
        case 201: // SPEED_HISTORY - История изменения скорости движения
        case 202: // COORD_HISTORY - История изменения географических координат
        case 203: // TRAFFIC_LIGHT_HISTORY - История сигналов АЛСН
        case 204: // MEK_HISTORY - История МЭК
        case 205: // FUEL - История изменения данных по топливу (тепловозы)
        case 206: // DIESEL_RUN_HISTORY - История запуска/остановки дизеля (тепловозы)
        case 207: // ELECTRIC_DATA - Данные по электроэнергии
        case 208: // VAGONS_DATA - Данные по вагонам 
        case 209: // ASOUP_DATA - История операций АСОУП
          Boolean stop = null;
          while (stop == null) {
            Date currentВate = new Date();
            Date currentDateTime = DateUtils.addSeconds((Date)restClient.intervalAndDateTime.get("currentDateTime"), 
                (int)restClient.intervalAndDateTime.get("minInterval"));

            if (currentВate.getTime() >= currentDateTime.getTime()) {
              soapRequestSuccessful = restClient.exec(mRequest, requestIntervalTimeMinute);
              stop = soapRequestSuccessful;
            } else {
              try {
                Thread.sleep(currentDateTime.getTime() - currentВate.getTime());
              } catch (InterruptedException e) {
                //
              }
            }  
          }
          
          break;
      }
      if (soapRequestSuccessful) {
        lsConfig.addDataStartTime(mRequest, requestIntervalTimeMinute);
        lsConfig.write();
      } else {
//        break;
      }
//    }

    return true;
  }

}
