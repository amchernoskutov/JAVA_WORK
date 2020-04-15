package loader.comm.config.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * ConfigModel
 * 
 * Указывает пакет в котором необходимо сформировать классы  
 * из файла resources\wsdl\Z63L_AS_EREPORT.wsdl
 *
 */

@Configuration
public class ConfigModel {
  @Bean
  public Jaxb2Marshaller marshaller() {
      Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
      marshaller.setPackagesToScan("loader.comm.model.wsdl");
      return marshaller;
  }

}
