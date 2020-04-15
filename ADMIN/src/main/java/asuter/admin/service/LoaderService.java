package asuter.admin.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import asuter.admin.domain.LoaderDatas;

@Service
public class LoaderService {

  public List<LoaderDatas> readCSVFile(MultipartFile multipart) throws Exception {
    SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<LoaderDatas> books = new ArrayList<LoaderDatas>();
    String line;

    InputStream inputStream = multipart.getInputStream();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
   int numberString = 1;
    while ((line = bufferedReader.readLine()) != null) {
      String[] attributes = line.split(";");

      if (attributes.length != 6) throw new Exception("Недостаточно данных в строке № " + numberString + " (" + line + ")");
      if ("".equals(attributes[0])) throw new Exception("Не задан ID запроса в строке № " + numberString + " (" + line + ")");
      if (Integer.parseInt(attributes[0]) < 1) throw new Exception("Не задан ID запроса в строке № " + line);
      if ("".equals(attributes[1])) throw new Exception("Не задано имя запроса в строке № " + numberString + " (" + line + ")");
      if ("".equals(attributes[2])) throw new Exception("Не задано имя системы в строке № " + numberString + " (" + line + ")");
      if ("".equals(attributes[5])) throw new Exception("Не задан путь к файлу в строке № " + numberString + " (" + line + ")");

      LoaderDatas loaderDatas = new LoaderDatas (
          numberString,
          false,
          "",
          Integer.parseInt(attributes[0]), 
          attributes[1].toUpperCase(), 
          attributes[2].toUpperCase(), 
          FORMAT_DATE.parse(attributes[3]), 
          FORMAT_DATE.parse(attributes[4]), 
          attributes[5]);  

      books.add(loaderDatas);
      numberString++;
    }

    return books;
  }

}
