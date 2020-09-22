package mainscheduler.form;

import java.util.concurrent.ScheduledFuture;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * MainForm
 * 
 * Класс для вывода информации о работе системы на гланой странице 
 *
 */

@Data
@AllArgsConstructor()
public class MainForm {
  private int id;
  private String name;
  private String systemName;
  private String startTime;
  private String URLRESTService;
  private int intervalTimeMinute;
  private ScheduledFuture<?> scheduledFuture;
  
}
