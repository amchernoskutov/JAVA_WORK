package loader.comm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Loader COMM 
 * Loader COMM - SOAP Crient приложение, взаимодействует с Web Service ЦОММ.  
 * 
 * Приложение запускается из Main Scheduler-а по заданному расписанию и формирует
 * SAOP запрос или запросы к ЦОММ за определенный период времени.
 *  
 * Полученные ответы записывается в файл формата xml, далее Loader направляет 
 * MQ Dispacher-у MQ сообщение о получении данных.   
 * 
 * Приложение использует конфигурационный файл configloadercomm.xml для считывания 
 * параметров запроса.
 * 
 * Приложение получает два входящих параметра: первый это ID request запроса,
 * второй - время (количество минут) за которое нужно получить данные от даты 
 * последнего запроса.
 * 
 * Дата последнего запроса храниться в конфигурационном файле.  
 */

@SpringBootApplication
public class LoadercommApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadercommApplication.class, args);
	} 

}
