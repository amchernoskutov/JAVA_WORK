package main.scheduler.manager;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.PostConstruct;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.Data;
import main.scheduler.config.MSConfig;
import main.scheduler.log.MSLog;

@Data
@EnableScheduling
@Service
public class ManagerScheduler {
  private static final Integer MINUTE = 60000;
  private MSConfig msConfig;
  private MSLog msLog;
  private final TaskScheduler taskScheduler;
  private HashSet<ScheduledFuture> taskSchedulers = new HashSet<ScheduledFuture>();

  public ManagerScheduler(TaskScheduler taskScheduler, MSConfig msConfig, MSLog msLog) {
    this.taskScheduler = taskScheduler;
    this.msConfig = msConfig;
    this.msLog = msLog;
  }

  @PostConstruct
  public void executeSchedule() {
    taskSchedulers.forEach(scheduled -> scheduled.cancel(true));
    taskSchedulers.clear();

    msConfig.getConfig().getSystemInformation().getSystemInformations().forEach(item -> {
      var scheduledFuture = taskScheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
              cheskConfig();
              var runtime = Runtime.getRuntime();
              try {
                runtime.exec(item.getCommandPath() + " " + item.getId() + " "  + item.getIntervalTimeMinute());
                MSLog.Info("SystemInformation::System swot name-" + item.getName()
                + "::Start command line[" + item.getCommandPath() + " " + item.getId() + " "
                + item.getIntervalTimeMinute() + "]::StartTime-"
                + MSLog.FORMAT_DATE.format(item.getStartTime()));
              } catch (IOException e){
                MSLog.Severe("SystemInformation::System swot name-" + item.getName()
                + "::ERROR Start command line[" + item.getCommandPath() + " " + item.getId() + " "
                + item.getIntervalTimeMinute() + "]::StartTime-"
                + MSLog.FORMAT_DATE.format(item.getStartTime()) + e.getMessage());
              }
            }
          }, item.getStartTime(), (item.getIntervalTimeMinute() * MINUTE));
      taskSchedulers.add(scheduledFuture);
    });
  }

  private void cheskConfig() {
    var msConfigNew = new MSConfig();
    msConfigNew.read();
    if (!msConfigNew.getConfig().equals(msConfig.getConfig())) {
      MSLog.Info("The file " + MSConfig.FILE_CONFIG + " was changed");
      MSLog.Info("Old file " + MSConfig.FILE_CONFIG + " is:" + msConfig.writeToString());
      msConfig.read();
      MSLog.Info("New file " + MSConfig.FILE_CONFIG + " is:" + msConfigNew.writeToString());

      msLog.initHandlers();
      executeSchedule();
    }
    if(!MSLog.oldDate.equals(MSLog.FORMAT_DATE.format(new Date()))) { 
      msLog.initHandlers();
    }  
  }

}
