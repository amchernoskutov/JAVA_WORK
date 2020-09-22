package mainscheduler.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import mainscheduler.config.data.GeneralData;
import mainscheduler.manager.ManagerScheduler;

/**
 * MainschedulerController
 * 
 * Контроллер для главной формы
 *
 */

@Controller
public class MainschedulerController {

  private ManagerScheduler managerScheduler;

  public MainschedulerController(ManagerScheduler managerScheduler) {
    this.managerScheduler = managerScheduler;
  }

  @GetMapping("/mainscheduler")
  public String senDnoneGet(Map<String, Object> model) {
    managerScheduler.getMainForms().forEach(item -> {
      if (item.getScheduledFuture().getDelay(TimeUnit.MILLISECONDS) > 0) {
        item.setStartTime(GeneralData.FORMAT_DATE.format(DateUtils.round(DateUtils.addMilliseconds(new Date(),
            (int) item.getScheduledFuture().getDelay(TimeUnit.MILLISECONDS)), Calendar.MINUTE)));
      } else {
        item.setStartTime("Задание выполняется");
      }
    });
    model.put("mainForms", managerScheduler.getMainForms());
    
    return "mainscheduler";
  }
}
