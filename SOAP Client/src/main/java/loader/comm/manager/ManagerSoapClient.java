package loader.comm.manager;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.comm.config.xml.LSConfig;
import loader.comm.log.LSLog;
import loader.comm.model.xml.MRequest;
import loader.comm.soapclients.SoapClientMM;
import loader.comm.soapclients.SoapClientNSI;

/**
 * ManagerSoapClientMM
 * 
 * Менеджер определяет вид SOAP запроса, который необходимо создать и
 * генерирует задание на создание запроса. 
 * 
 * Если есть отстование по дате получения данных, то процесс последовательно получает все пропцщенные данные.
 * Все данные получает последовательно, если сбой в получении данных, то к получению следующих данных не переходит.
 * 
 */

@Service
public class ManagerSoapClient {
  @Autowired
  private LSConfig lsConfig;
  
  @Autowired
  private SoapClientMM soapClientMM;

  @Autowired
  private SoapClientNSI soapClientNSI;

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

    int[] dor = new int[] {83};
//    int[] depo = new int[] {2, 3, 4};
    int[] depo = new int[] {3};
    boolean soapRequestSuccessful = true;

    while ((DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime() < (new Date()).getTime())&&(soapRequestSuccessful)) {
      switch (requestId) {
        case 1:
          for(int dorNumber: dor) {
            for(int depoNumber: depo) {
              soapRequestSuccessful = soapClientMM.generateSOAPData(mRequest, dorNumber, depoNumber, requestIntervalTimeMinute);
            }  
          }
          break;
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
          for(int dorNumber: dor) {
            for(int depoNumber: depo) {
              soapRequestSuccessful = soapClientNSI.generateSOAPData(mRequest, dorNumber, depoNumber, requestIntervalTimeMinute, requestId);
            }  
          }
          break;
      }
      if (soapRequestSuccessful) {
        lsConfig.addDataStartTime(mRequest, requestIntervalTimeMinute);
        lsConfig.write();
      } else {
        break;
      }
      
    }

    return true;
  }

}
