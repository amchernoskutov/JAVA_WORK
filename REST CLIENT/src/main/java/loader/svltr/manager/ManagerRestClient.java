package loader.svltr.manager;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.svltr.LoadersvltrApplication;
import loader.svltr.config.data.LSData;
import loader.svltr.config.xml.LSConfig;
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
  private LSData lsData;
  
  @Autowired
  ManagerTokenScheduler managerTokenScheduler;

  public boolean exec(int requestId, int requestIntervalTimeMinute) {
    MRequest mRequest = lsConfig.getConfig().getRequest().getRequests().stream().filter(item -> item.getId() == requestId).findFirst().orElse(null); 
    MRequest mRequestVagonData = lsConfig.getConfig().getRequest().getRequests().
    stream().filter(f -> RestClient.VAGONS_DATA.toLowerCase().equals(f.getName().toLowerCase())).findFirst().orElse(null);

    // Если нет записи в конфигурационном файле, то выходим
    if (mRequest == null) {
      LoadersvltrApplication.logger.fatal("ERROR don't found record in config file requestId=" + requestId);
      return false;  
    }
    
    // Если дата запроса данных больше текущей, то выходим
    if (DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime() > (new Date()).getTime()) {
      LoadersvltrApplication.logger.fatal("Loger ID " + mRequest.getId() + " name " + mRequest.getName() + 
                " can not start. [DataStartTime + RequestIntervalTimeMinute] > [DateNow]." + 
                " DataStartTime=" + mRequest.getDataStartTime() + 
                " RequestIntervalTimeMinute=" + requestIntervalTimeMinute +  
                " DateNow=" + new Date());
      return false;
    }

    Boolean soapRequestSuccessful = true;

// На реальных данных нужно будет раскоментарить чтобы данные запрашивались в случае отставания    
    Boolean stop = false;
//    while ((DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime() < (new Date()).getTime())&&(soapRequestSuccessful)) {
    switch (requestId) {
      case 201: // COORD_SPEED_HISTORY - Скорость и географические координаты
      case 202: // ELECTRIC_DATA - Показания счетчиков тяги, рекуперации
      case 203: // TRAFFIC_LIGHT_HISTORY - Сигнал АЛСН
      case 204: // MEK_HISTORY - Данные МЭК
      case 205: // FUEL - Данные о топливе
      case 206: // DIESEL_RUN_HISTORY - Работа дизеля
      case 207: // VAGONS_DATA - Данные по вагонам
      case 208: // ASOUP_DATA - История операций и состояний локомотивов
      case 209: // ASSIGNMENT_DATA - Приписка локомотива
      case 210: // TRACTION_GENERATOR_HISTORY - Ток, напряжение, электроэнергия тягового генератора
      case 211: // TEMPERATURES_HISTORY - Температуры воды, масла дизеля, окружающего воздуха
      case 212: // CONTROLLER_POSITION_HISTORY - Позиция контроллера 
      case 213: // NSI_DEPOTS - Депо 
      case 214: // NSI_OPERATIONS - Операции АСОУП 
      case 215: // NSI_RAILWAYS - Дороги
      case 216: // NSI_STATES - Состояния АСОУП
      case 217: // NSI_STATIONS - Станции
        RestClient restClient = new RestClient(lsConfig, lsData, managerTokenScheduler.getToken());
        while (stop == false) {
          Date currentВate = new Date();
          Date currentDateTime = DateUtils.addSeconds((Date)restClient.getIntervalAndDateTime().get("currentDateTime"), 
              (int)restClient.getIntervalAndDateTime().get("minInterval"));

          if (currentВate.getTime() >= currentDateTime.getTime()) {
            soapRequestSuccessful = restClient.exec(mRequest, requestIntervalTimeMinute, null, null);
            stop = soapRequestSuccessful;

            try {
              TimeUnit.SECONDS.sleep((int)restClient.getIntervalAndDateTime().get("minInterval"));
            } catch (InterruptedException e) {
              // 
            }
          } else {
            try {
              Thread.sleep(currentDateTime.getTime() - currentВate.getTime());
            } catch (InterruptedException e) {
              //
            }
          }  
        }
        
        if ((requestId==208)&(soapRequestSuccessful==true)) {
          soapRequestSuccessful = restClient.execVagonsData(mRequest, mRequestVagonData, requestIntervalTimeMinute);
        }
          
        break;
      }
      if (soapRequestSuccessful) {
        lsConfig.addDataStartTime(mRequest, requestIntervalTimeMinute);
        lsConfig.write();
        if (requestId==208) {
          lsConfig.addDataStartTime(mRequestVagonData, requestIntervalTimeMinute);
          lsConfig.write();
        }
      } else {
//        break;
      }
//    }

    return true;
  }

}
