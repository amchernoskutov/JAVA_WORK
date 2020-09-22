package loader.elbrus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loader ELBRUS 
 * Loader ELBRUS - ActiveMQ Crient приложение, взаимодействует с Web Service АПК Эльбрус.  
 * 
 * Приложение запускается из Main Scheduler-а по заданному расписанию и формирует
 * ActiveMQ запрос или запросы к Эльбрус за определенный период времени.
 *  
 * Полученные ответы записывается в файл формата xml, далее Loader направляет 
 * MQ Dispacher-у MQ сообщение о получении данных.   
 * 
 * Приложение использует конфигурационный файл configloaderelbrus.xml для считывания 
 * параметров запроса.
 * 
 * Приложение получает два входящих параметра: первый это ID request запроса,
 * второй - время (количество минут) за которое нужно получить данные от даты 
 * последнего запроса.
 * 
 * Дата последнего запроса храниться в конфигурационном файле.  
 */

@SpringBootApplication
public class LoaderelbrusApplication {

  public static Logger logger = LogManager.getLogger(LoaderelbrusApplication.class);
  
  public static void main(String[] args) {
    SpringApplication.run(LoaderelbrusApplication.class, args);   
  }

}
