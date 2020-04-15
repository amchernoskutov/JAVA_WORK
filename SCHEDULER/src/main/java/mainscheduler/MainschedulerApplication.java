package mainscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ManagerScheduler
 * 
 * Планировщик запуска заданий на создание SOAP запросов, MQ сообщений для получения данных от ЦОММ,
 * АПК ЭЛЬБРУС, СВЛ ТР. Задания запускаются по расписанию заданному в конфигурационном файле
 * configmainscheduler.xml
 * 
 * Возможно редактирование конфигурационного файла в режиме реального времени. В этом случае
 * происходит перепланирование всех ранее созданных заданий.
 * 
 * Планировщик запускается каждые IntervalTimeMinute начиная отсчет от установленного времени
 * StartTime. 
 *
 */

@SpringBootApplication
public class MainschedulerApplication {
  
  public static void main(String[] args) throws Exception {
    SpringApplication.run(MainschedulerApplication.class, args);
  }
  
}
