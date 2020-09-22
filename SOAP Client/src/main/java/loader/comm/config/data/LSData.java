package loader.comm.config.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.comm.config.xml.LSConfig;
import loader.comm.model.xml.MRequest;

/**
 * LCData
 * 
 * Предназначен для записи полученных SOAP ответов в файл по пути указанному в конфигурационном файле.
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
   * @param codeRoad - номер дороги
   * @param codeDepo - номер документа
   * @param prefix - префикс 
   * @return - возвращает путь к файлу и имя файла
   * @throws Exception
   */
  public String writeSOAPrespond(MRequest mRequest, InputStream respond, 
      Integer requestIntervalTimeMinute, int codeRoad, Date dataStartTime, int codeDepo) throws Exception {
    String path = GeneralData.FORMAT_DATE_FILE_YYYYMMDD.format(dataStartTime);
    
    path = lsConfig.getConfig().getLogAndDataServer().getName() +  
        mRequest.getDestPath() + 
        path + "/" + GeneralData.ADD_LOADERCOMM_PATH + codeRoad + "/";

    File theDir = new File(path);
    if (!theDir.exists()) { 
      theDir.mkdirs();
    }
    
    String fileName;
      fileName = mRequest.getName().toLowerCase() + "_dor" + codeRoad + "_" +  
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(dataStartTime) + "_" +
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute)) + 
          ".xml";
    File file = new File(path + fileName);

    copyInputStreamToFile(respond, file);
    return file.getPath(); 
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
