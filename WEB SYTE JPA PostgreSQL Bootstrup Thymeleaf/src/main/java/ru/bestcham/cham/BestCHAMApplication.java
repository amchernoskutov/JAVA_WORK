package ru.bestcham.cham;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"ru.bestcham"})
public class BestCHAMApplication {

	public static void main(String[] args) {
		SpringApplication.run(BestCHAMApplication.class, args);
	} 
 
}
