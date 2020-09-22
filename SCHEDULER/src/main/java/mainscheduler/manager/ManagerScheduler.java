package mainscheduler.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.Data;
import mainscheduler.MainschedulerApplication;
import mainscheduler.config.data.GeneralData;
import mainscheduler.config.xml.DataConfig;
import mainscheduler.config.xml.MSConfig;
import mainscheduler.form.MainForm;

/**
 * Планирование запуска Loger-ов по расписанию заданному в конфигурационном файле configmainscheduler.xml
 */

@Data
@EnableScheduling
@Service
public class ManagerScheduler {
  private static final Integer MINUTE = 60000;
  private static final String SYSTEM_COMM = "comm";
  private static final String SYSTEM_ELBRUS = "elbrus";
  private static final String SYSTEM_SVL = "svl";

  @Autowired
  private MSConfig msConfig;
  
  private final TaskScheduler taskScheduler;
  private ArrayList<MainForm> mainForms = new ArrayList<MainForm>();
  private final String CORN ="*/59 * * * * *";
  private DataConfig dataConfig;

  public ManagerScheduler(TaskScheduler taskScheduler, DataConfig dataConfig) {
    this.taskScheduler = taskScheduler;
    this.dataConfig = dataConfig;
  }
  
  /** Каждые 59 секунд проверяется изменение файла configmainscheduler.xml.
  * Если файл был изменен все задания заново перепланируются.
  * Так же создаются новые LOG-файлы в случае изменения пути к ним.
  */
  @Scheduled(cron = CORN)
  public void checkConfig() {
    MSConfig msConfigNew = new MSConfig(dataConfig); 
    msConfigNew.read();
    if (!msConfigNew.getConfig().equals(msConfig.getConfig())) {
      MainschedulerApplication.logger.fatal("The file " + dataConfig.getConfigfilename() + " was changed");
      MainschedulerApplication.logger.fatal("Old file " + dataConfig.getConfigfilename() + " is:" + msConfig.writeToString());
      msConfig.read();
      MainschedulerApplication.logger.fatal("New file " + dataConfig.getConfigfilename() + " is:" + msConfigNew.writeToString());
      mainForms.stream().forEach(scheduled -> scheduled.getScheduledFuture().cancel(true));
      mainForms.clear();
      executeSchedule();
    }
    
  }  

  // Создание планировщика, каждого в отдельном потоке 
  @PostConstruct
  public void executeSchedule() {
    int corePoolSize = (int) msConfig.getConfig().getSystemInformation().getSystemInformations().stream().filter(item -> item.getIntervalTimeMinute()>0).count();
    msConfig.getConfig().getSystemInformation().getSystemInformations().forEach(item -> {
      if (item.getIntervalTimeMinute() > 0) {
        
        // Вычисление времи запуска Loader-а. 
        Date DateNow = new Date();
        Date dateStart = item.getStartTime();
        Integer intervalTimeMinute = item.getIntervalTimeMinute();
        Long InitialDelay = (Long)(TimeUnit.MILLISECONDS.toMillis(DateNow.getTime() - item.getStartTime().getTime())); 
        if (InitialDelay >= 0) {
          Long koef = TimeUnit.MILLISECONDS.toMinutes(DateNow.getTime() - item.getStartTime().getTime()) / intervalTimeMinute;
          dateStart = DateUtils.addMinutes(item.getStartTime(), (int)(long)(koef*intervalTimeMinute + intervalTimeMinute));
          InitialDelay = (Long)(TimeUnit.MILLISECONDS.toMillis(dateStart.getTime()-DateNow.getTime())); 
        } 
        
        // Создание планировщиков для каждого Loader-a  
        ScheduledFuture<?> scheduledFuture = Executors.newScheduledThreadPool(corePoolSize).scheduleAtFixedRate(new Runnable() {
          
          public void run() {
            Date dateStartScheduled = DateUtils.round(new Date(), Calendar.MINUTE);
            try {
              HttpHeaders headers = new HttpHeaders();
              headers.set("idRequest", Integer.toString(item.getId()));
              headers.set("intervalTimeMinute", Integer.toString(item.getIntervalTimeMinute()));
              headers.set("Accept-Encoding", "gzip, deflate, br");
              headers.set("Content-Type", "text/plain; charset=utf-8");
       
              HttpEntity<String> request = new HttpEntity<String>(headers);
              
              RestTemplate restTemplate = new RestTemplate();
              ResponseEntity<String> result = 
                  restTemplate.exchange(item.getURLRESTService(), HttpMethod.POST, request, String.class);
              
              if (result.getStatusCodeValue() == 200) {
                MainschedulerApplication.logger.fatal("Start time=" + GeneralData.FORMAT_DATE.format(dateStartScheduled) + ";System short name=" + item.getName()
                    + ";Send POST REST request " + item.getURLRESTService() + " idRequest=" + item.getId() + " intervalTimeMinute="
                    + item.getIntervalTimeMinute() + ";Recive responce Status=" + result.getStatusCodeValue() + " " 
                    + result.getBody());
              } else {
                MainschedulerApplication.logger.fatal("Start time=" + GeneralData.FORMAT_DATE.format(dateStartScheduled) + ";System short name=" + item.getName()
                    + ";Send POST REST request " + item.getURLRESTService() + " idRequest=" + item.getId() + " intervalTimeMinute="
                    + item.getIntervalTimeMinute() + ";ERROR Recive Status=" + result.getStatusCodeValue() + " "
                    + result.getBody());
              }
            } catch (Exception e) {
              
              MainschedulerApplication.logger.fatal("Start time=" + GeneralData.FORMAT_DATE.format(dateStartScheduled) + ";System short name=" + item.getName()
                    + ";ERROR Send POST REST request " + item.getURLRESTService() + " idRequest=" + item.getId() + " intervalTimeMinute="
                    + item.getIntervalTimeMinute() + ";Recive responce "          
                    + e.getMessage());
            }
          }
        }, InitialDelay, (item.getIntervalTimeMinute() * MINUTE), TimeUnit.MILLISECONDS);

        String systemName = "";
        if (item.getURLRESTService().toLowerCase().indexOf(SYSTEM_COMM) >=0 ) systemName = SYSTEM_COMM; 
        if (item.getURLRESTService().toLowerCase().indexOf(SYSTEM_ELBRUS) >=0 ) systemName = SYSTEM_ELBRUS; 
        if (item.getURLRESTService().toLowerCase().indexOf(SYSTEM_SVL) >=0 ) systemName = SYSTEM_SVL; 
        mainForms.add(new MainForm(item.getId(),
            item.getName(),
            systemName.toUpperCase(),
            "",
            item.getURLRESTService(),
            item.getIntervalTimeMinute(),            
            scheduledFuture));

        }
    });
    
  }
  
  public ArrayList<MainForm> getMainForms() {
    mainForms.sort(Comparator.comparing(MainForm::getId));
    return mainForms;
  }

}
