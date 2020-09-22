package loader.elbrus.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import loader.elbrus.config.data.GeneralData;
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.manager.ManagerActiveMQClient;


/**
 * PurchaseController
 * 
 * REST контроллер, получает от Main Scheduler запрос на выполнение.
 * Запускает процесс создания запроса к ActiveMQ для получения данных из Эльбрус. 
 *
 */

@RestController
@RequestMapping("loaderelbrusupload")
public class PurchaseController {
  private static final Integer MAX_REQUEST_SHOW = 20;
  private ArrayList<HashSet<String>> requestsValidator = new ArrayList<HashSet<String>>();

  @Autowired
  private LAConfig laConfig;

  @Autowired
  private ManagerActiveMQClient managerActiveMQClient;

  @GetMapping
  @ResponseBody
  public ArrayList<HashSet<String>> list() {
    return requestsValidator;
  }

  @PostMapping
  public @ResponseBody String create(
      @RequestHeader("idRequest") String idRequest,
      @RequestHeader("intervalTimeMinute") String intervalTimeMinute) {
    
    String request;
    HashSet<String> requests = new HashSet<String>();
    
    if (requestsValidator.size() > MAX_REQUEST_SHOW) {
      requestsValidator.remove(0).clear(); 
    }
    request = GeneralData.FORMAT_DATE.format(new Date()) + " Request accepted: idReqest=" + idRequest + " intervalTimeMinute=" + intervalTimeMinute;
    
    
    
    laConfig.getConfig().getRequest().getRequests().stream().filter(f -> f.getId() == Integer.parseInt(idRequest)).forEach(item -> {
      item.getRoadsAndTime().forEach(road -> {
        Thread thread = new Thread(new Runnable() {
          @Override
          public void run() {
            managerActiveMQClient.exec(Integer.parseInt(idRequest), road.getCodeRoad(), road.getDataStartTime(), road.getUrl(), road.getQueueName(), Integer.parseInt(intervalTimeMinute));
          }
        });
        thread.start();
      });
    });
    
    requests.add(request);
    requestsValidator.add(requests);
    
    return request;
  }

}
