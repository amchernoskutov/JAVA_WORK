package loader.elbrus.config.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.model.xml.MRequest;

/**
 * LBData
 * 
 * Предназначен для записи полученных ответов от ActiveMQ в файл по пути указанному в конфигурационном файле.
 */

@Service
public class LBData {
  @Autowired
  private LAConfig laConfig; 

  /**
   * writeXMLFile - запись XML досумента класса Document в файл на СХД. 
   * При необходимости создает путь и файл по данным переменной mRequest   
   * @param mRequest - элемент данных Request из файла configloaderelbrus.xml по которому формировалься запрос 
   * @param document - экземпляр класса Document с данными XML документа
   * @param requestIntervalTimeMinute - (requestIntervalTimeMinute - количество минут за которое запрашиваются данные)
   * @param codeRoad - номер дороги
   * @param depoNumber - номер документа
   * @param prefix - префикс 
   * @return - возвращает путь к файлу и имя файла
   * @throws Exception
   */
  public String writeXMLFile(MRequest mRequest, Document document, 
      Integer requestIntervalTimeMinute, int codeRoad, Date dataStartTime, int depoNumber, String prefix) throws Exception {
    String path = GeneralData.FORMAT_DATE_FILE_YYYYMMDD.format(dataStartTime);
    
    path = laConfig.getConfig().getLogAndDataServer().getName() +  
        mRequest.getDestPath() + 
        path + "/" + GeneralData.ADD_LOADERELBRUS_PATH; 
    path = path + codeRoad + "/";

    File theDir = new File(path);
    if (!theDir.exists()) theDir.mkdirs();
    String fileName;
    if (codeRoad!=0) {
      fileName = mRequest.getName().toLowerCase() + prefix + "_dor" + codeRoad + "_" +    
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(dataStartTime) + "_" +
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute)) + 
          ".xml";
      
    } else {
      fileName = mRequest.getName().toLowerCase() + prefix + "_" +    
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(dataStartTime) + "_" +
          GeneralData.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute)) + 
          ".xml";
    }
    File file = new File(path + fileName);
    file.setReadable(true, false);

    XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    FileOutputStream inputStream = new FileOutputStream(file);
    xmlOutputter.output(document, inputStream);

    return file.getPath(); 
  }

  
}
