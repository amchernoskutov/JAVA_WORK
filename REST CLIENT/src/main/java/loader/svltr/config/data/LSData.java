package loader.svltr.config.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.svltr.config.xml.LSConfig;
import loader.svltr.model.xml.MRequest;

/**
 * LCData
 * 
 * Предназначен для записи полученных REST ответов в файл по пути указанному в конфигурационном файле.
 */

@Service
public class LSData {

  @Autowired
  private LSConfig lsConfig; 
  
  /**
   * writeXMLFile - запись XML досумента класса Document в файл на СХД. 
   * При необходимости создает путь и файл по данным переменной mRequest   
   * @param mRequest - элемент данных Request из файла configloaderelbrus.xml по которому формировалься запрос 
   * @param document - экземпляр класса Document с данными XML документа
   * @param requestIntervalTimeMinute - (requestIntervalTimeMinute - количество минут за которое запрашиваются данные)
   * @param dorNumber - номер дороги
   * @param depoNumber - номер документа
   * @param prefix - префикс 
   * @return - возвращает путь к файлу и имя файла
   * @throws Exception
   */
  public String writeSOAPrespond(MRequest mRequest, InputStream respond, 
      Integer requestIntervalTimeMinute, int dorNumber, int depoNumber) throws Exception {
    String path = GeneralData.FORMAT_DATE_FILE_YYYYMMDD.format(mRequest.getDataStartTime());
    
    path = lsConfig.getConfig().getLogAndDataServer().getName() +  
        mRequest.getDestPath() + 
        path + "/" + GeneralData.ADD_LOADERSVLTR_PATH + "/";    

    File theDir = new File(path);
    if (!theDir.exists()) theDir.mkdirs();
    String fileName;
    if ((dorNumber!=0)&&(depoNumber!=0)) {
      fileName = mRequest.getName().toLowerCase() + "_dor" + dorNumber + "depo" + depoNumber + "_" +    
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(mRequest.getDataStartTime()) + "_" +
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute)) + 
          ".json";
      
    } else {
      fileName = mRequest.getName().toLowerCase() + "_" +   
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(mRequest.getDataStartTime()) + "_" +
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute)) + 
          ".json";
    }
    File file = new File(path + fileName);
    file.setReadable(true, false);

    copyInputStreamToFile(respond, file);
    
    return file.getPath(); 
  }

  public String writeSOAPrespondVagons(MRequest mRequest, MRequest mRequestVagonData, JSONArray jsonArrayAllVagons, 
        Integer requestIntervalTimeMinute, int dorNumber, int depoNumber) throws Exception {
      String path = GeneralData.FORMAT_DATE_FILE_YYYYMMDD.format(mRequest.getDataStartTime());
      
      path = lsConfig.getConfig().getLogAndDataServer().getName() +  
          mRequest.getDestPath() + 
          path + "/" + GeneralData.ADD_LOADERSVLTR_PATH + "/";    

      File theDir = new File(path);
      if (!theDir.exists()) theDir.mkdirs();
      String fileName;
      if ((dorNumber!=0)&&(depoNumber!=0)) {
        fileName = mRequestVagonData.getName().toLowerCase() + "_dor" + dorNumber + "depo" + depoNumber + "_" +    
            GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(mRequestVagonData.getDataStartTime()) + "_" +
            GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(mRequestVagonData.getDataStartTime(), requestIntervalTimeMinute)) + 
            ".json";
        
      } else {
        fileName = mRequestVagonData.getName().toLowerCase() + "_" +   
            GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(mRequestVagonData.getDataStartTime()) + "_" +
            GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(mRequestVagonData.getDataStartTime(), requestIntervalTimeMinute)) + 
            ".json";
      }
      
      try {
        File file = new File(path + fileName);
        file.setWritable(true);
        file.setReadable(true);
        FileWriter fileWriter = new FileWriter(file, false);
        jsonArrayAllVagons.writeJSONString(jsonArrayAllVagons, fileWriter);
        fileWriter.close();
      } catch (Exception e) {
        e.getStackTrace();
      }
      
      return (path+fileName).replace("/", "\\"); 
    }
   
  // Побайтная запись потока с ответом в файл
  public void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      int read;
      byte[] bytes = new byte[1024];

      while ((read = inputStream.read(bytes)) != -1) {
        outputStream.write(bytes, 0, read);
      }
    }
  }

}
