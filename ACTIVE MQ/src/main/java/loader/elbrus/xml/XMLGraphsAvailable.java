package loader.elbrus.xml;

import java.text.SimpleDateFormat;
import java.util.Date;
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.TimetableInfosMessage;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * XMLGraphsAvailable 
 * 
 * Формирует XML - класса Document c информацией о наличии графиков движения поездов по данным Эльбрус
 * NORMATIVE (Нормативные графики)
 * FORECASE (Прогнозные графики)
 * GIDEXPORT (Прогнозный в ГИД)
 * ACTUAL (Исполненный)
 * ACTUAL (Прочие графики)
 */

@Service
public class XMLGraphsAvailable {
  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  @Autowired
  private LAConfig laConfig;

  /**
   * createXMLFile
   * Формирует XML - класса Document 
   * @param timetableInfosMessage - интервал времени в минутах за который получены данные
   * @param mRequest - элемент данных Request из файла configloaderelbrus.xml по которому формировалься запрос 
   * @param dateStart - дата с которой получены данные
   * @param dateFinish - дата до которой получены данные
   * @return - возвражает готовый файл XML в виде экземпляра класса Document
   * @throws Exception - любые ошибки связанные с формированием файл XML в виде экземпляра класса Document
   */
  
  public Document createXMLFile(TimetableInfosMessage timetableInfosMessage, MRequest mRequest,
      Date dateStart, Date dateFinish) throws Exception {
    Document document = new Document();
    Element root = new Element("GraficsInfo");
    root.setAttribute("dtRequest", FORMAT_DATE.format(new Date()));
    root.setAttribute("TypeGr", mRequest.getTypeParam());
    root.setAttribute("User", mRequest.getUserParam());
    root.setAttribute("Reference", "");
    root.setAttribute("dtStart", FORMAT_DATE.format(dateStart));
    root.setAttribute("dtFinish", FORMAT_DATE.format(dateFinish));
    root.setAttribute("mq", "activemq:" + laConfig.getConfig().getActiveMQRequestParam().getUrl());
    document.setRootElement(root);
    
    timetableInfosMessage.getTimetableInfosList().forEach(item -> {
      Element grafic = new Element("Grafic")
        .setAttribute("type", item.getTimetableTur().getType())
        .setAttribute("user", item.getTimetableTur().getUser())
        .setAttribute("reference", item.getTimetableTur().getReference())
        .setAttribute("is_default", Boolean.toString(item.getTimetableTur().getIsDefault()))
        .setAttribute("scope", item.getTimetableParameters().getScope())
        .setAttribute("desc", item.getTimetableParameters().getDesc())
        .setAttribute("modified", item.getTimetableParameters().getModified().getTimeValue())
        .setAttribute("first_date", item.getTimetableParameters().getFirstDate().getTimeValue())
        .setAttribute("last_date", item.getTimetableParameters().getLastDate().getTimeValue());
      root.addContent(grafic);
    });
    
    return document;
  }
  
}
