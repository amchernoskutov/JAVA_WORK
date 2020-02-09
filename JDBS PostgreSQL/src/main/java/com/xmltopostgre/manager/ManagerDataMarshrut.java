package com.xmltopostgre.manager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import com.xmltopostgre.xml.DataMarshrut;
import lombok.Data;

@Data
public class ManagerDataMarshrut {
  private static final String FILE_IN = "18310_Marshruts.xml";

  private Persister persister;
  private File file;
  private DataMarshrut dataMarshrut;

  public ManagerDataMarshrut() {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    RegistryMatcher registryMatcher = new RegistryMatcher();
    registryMatcher.bind(Date.class, new DateFormatTransformer(format));
    
    persister = new Persister(registryMatcher);
    file = new File(FILE_IN);
    read();
  }

  public void read() {
    Date startDate; 
    Date finishDate;
    try {
      startDate = new Date();
      dataMarshrut = persister.read(DataMarshrut.class, file);
      finishDate = new Date();

      System.out.println("Начало распознавания xml: " + startDate.getTime());
      System.out.println("Завершение распознавания xml: " + finishDate.getTime());
      long milliseconds = startDate.getTime() - finishDate.getTime();
      
      System.out.println("Разница в секундах (xml): " + ((int) (milliseconds / (1000))));
      System.out.println("Разница в миллисекундах (xml): " + milliseconds);
      
    } catch (Exception e) {
      System.out.println("Ошибка чтения файла 18310_Marshruts.xml " + e.getMessage());
    }
  }

  public void write() {
    try {
      persister.write(dataMarshrut, System.out);
    } catch (Exception e) {
      System.out.println("Ошибка записи файла 18310_Marshruts.xml " + e.getMessage());
    }
  }

}
