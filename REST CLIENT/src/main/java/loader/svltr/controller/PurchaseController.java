package loader.svltr.controller;

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
import loader.svltr.log.LSLog;
import loader.svltr.manager.ManagerRestClient;


/**
 * PurchaseController
 * 
 * REST контроллер, получает от Main Scheduler запрос на выполнение.
 * Запускает процесс создания REST запросов к системе СВЛ ТР. 
 *
 */

@RestController
@RequestMapping("loadersvltrupload")
public class PurchaseController {
  private static final Integer MAX_REQUEST_SHOW = 20;
  private ArrayList<HashSet<String>> requestsValidator = new ArrayList<HashSet<String>>();
  
  @Autowired
  private ManagerRestClient managerSoapClient;

  @Autowired
  private LSLog lsLog;

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

    if (!LSLog.oldDate.equals(LSLog.FORMAT_DATE_FILE.format(new Date()))) {
      lsLog.initHandlers();
    }

    if (requestsValidator.size() > MAX_REQUEST_SHOW) {
      requestsValidator.remove(0).clear(); 
    }

    request = LSLog.FORMAT_DATE.format(new Date()) + " Request accepted: idReqest=" + idRequest + " intervalTimeMinute=" + intervalTimeMinute;
    
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        managerSoapClient.exec(Integer.parseInt(idRequest), Integer.parseInt(intervalTimeMinute));
      }
    });
    thread.start();
    
    requests.add(request);
    requestsValidator.add(requests);
    
    return request;
  }

}
