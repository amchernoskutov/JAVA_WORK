package loader.svltr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Loader SVLTR Loader SVLTR - REST Crient приложение, взаимодействует с Web Service СВЛ ТР.
 * 
 * Приложение запускается из Main Scheduler-а по заданному расписанию и формирует REST запрос или
 * запросы к СВЛ ТР за определенный период времени.
 * 
 * Полученные ответы записывается в файл формата JSON, далее Loader направляет MQ Dispacher-у MQ
 * сообщение о получении данных.
 * 
 * Приложение использует конфигурационный файл configloadersvltr.xml для считывания параметров
 * запроса.
 * 
 * Приложение получает два входящих параметра: первый это ID request запроса, второй - время
 * (количество минут) за которое нужно получить данные от даты последнего запроса.
 * 
 * Дата последнего запроса храниться в конфигурационном файле.
 */

@SpringBootApplication
public class LoadersvltrApplication {
  public static Logger logger = LogManager.getLogger(LoadersvltrApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(LoadersvltrApplication.class, args);
  }
} 
