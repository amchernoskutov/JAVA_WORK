package loader.comm.manager;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import loader.comm.LoadercommApplication;
import loader.comm.config.data.LSData;
import loader.comm.config.xml.LSConfig;
import loader.comm.model.xml.MRequest;
import loader.comm.model.xml.RoadAndTime;
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
  private LSData lsData;
 
  @Autowired
  private Jaxb2Marshaller jaxb2Marshaller;

  public boolean exec(int requestId, int codeRoad, Date dataStartTime, int requestIntervalTimeMinute) {
    MRequest mRequest = lsConfig.getConfig().getRequest().getRequests().stream().filter(item -> item.getId() == requestId).findFirst().orElse(null); 

    // Если нет записи в конфигурационном файле, то выходим
    if (mRequest == null) {
      LoadercommApplication.logger.fatal("ERROR don't found record in config file requestId=" + requestId);
      return false;  
    }
    
    // Если дата запроса данных больше текущей, то выходим
    if (DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute).getTime() > (new Date()).getTime()) {
      LoadercommApplication.logger.fatal("Loger ID " + mRequest.getId() + " name " + mRequest.getName() + 
                " can not start. [DataStartTime + RequestIntervalTimeMinute] > [DateNow]." + 
                " DataStartTime=" + dataStartTime + 
                " RequestIntervalTimeMinute=" + requestIntervalTimeMinute +  
                " DateNow=" + new Date());
      return false;
    }

    // TODO: Чтобы получить по всем депо, нужно будет указать 0. На тестовом сервисе это не работает, это работает на новом боевом. 
    int[] depo = new int[] {3};
    boolean soapRequestSuccessful = true;

    while ((DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute).getTime() < (new Date()).getTime())&&(soapRequestSuccessful)) {
      switch (requestId) {
        case 1:
          for(int codeDepo: depo) {
            SoapClientMM soapClientMM = new SoapClientMM(lsConfig, lsData, jaxb2Marshaller);
            soapRequestSuccessful = soapClientMM.generateSOAPData(mRequest, codeRoad, dataStartTime, codeDepo, requestIntervalTimeMinute);
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
          for(int codeDepo: depo) {
            SoapClientNSI soapClientNSI = new SoapClientNSI(lsConfig, lsData, jaxb2Marshaller);
            soapRequestSuccessful = soapClientNSI.generateSOAPData(mRequest, codeRoad, dataStartTime, codeDepo, requestIntervalTimeMinute, requestId);
          }  
          break;
      }
      if (soapRequestSuccessful) {
        lsConfig.addDataStartTime(mRequest, codeRoad, dataStartTime, requestIntervalTimeMinute);
        lsConfig.write();
        RoadAndTime roadAndTime = mRequest.getRoadsAndTime().stream().filter(item -> item.getCodeRoad()==codeRoad).findFirst().orElse(null);
        dataStartTime = roadAndTime.getDataStartTime();
      } else {
        break;
      }
      
    }

    return true;
  }

}
