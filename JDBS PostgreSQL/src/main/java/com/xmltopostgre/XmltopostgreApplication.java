package com.xmltopostgre;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.xmltopostgre.manager.ManagerDataMarshrut;
import com.xmltopostgre.manager.ManagerSaveToPostgreSqlParallel;

@SpringBootApplication
public class XmltopostgreApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(XmltopostgreApplication.class, args);
	}

	  @Override
	  public void run(String... args) throws Exception {
	    var managerDataMarshrut = new ManagerDataMarshrut();

//	    var managerSaveToPostgreSqlNotParallel = new ManagerSaveToPostgreSqlNotParallel();
//	    managerSaveToPostgreSqlNotParallel.writeToPostgerSqlNotParallel(managerDataMarshrut.getDataMarshrut());
	    
        var managerSaveToPostgreSqlParallel = new ManagerSaveToPostgreSqlParallel();
        managerSaveToPostgreSqlParallel.writeToPostgerSqlParallel(managerDataMarshrut.getDataMarshrut());

	    
	  }
}
