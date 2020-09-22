package loader.elbrus.xml;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.jdom.Document;
import org.jdom.Element;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.TimetableMarksMessage;

/**
 * XMLMarks 
 * 
 * Формирует XML - класса Document c информацией о пометках по данным Эльбрус
 * MARKS (Пометки)
 *  
 */

public class XMLMarks {
  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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

  public Document createXMLFile(TimetableMarksMessage timetableMarksMessage, MRequest mRequest,
      Date dateStart, Date dateFinish, String url) throws Exception {
    
    Document document = new Document();
    Element root = new Element("MarksInfo");
    root.setAttribute("dtRequest", FORMAT_DATE.format(new Date()));
    root.setAttribute("TypeGr", mRequest.getTypeParam());
    root.setAttribute("User", mRequest.getUserParam());
    root.setAttribute("Reference", "");
    root.setAttribute("dtStart", FORMAT_DATE.format(dateStart));
    root.setAttribute("dtFinish", FORMAT_DATE.format(dateFinish));
    root.setAttribute("mq", "activemq:" + url);
    document.setRootElement(root);
    
    timetableMarksMessage.getTimetableMarksList().forEach(item -> {
      Element warn = new Element("Mark")
          .setAttribute("guid", item.getGuid())
          .setAttribute("finalized", item.getFinalized())
          .setAttribute("modified", item.getModified())
          .setAttribute("mrinc", Integer.toString(item.getMrinc()))
          .setAttribute("mrcm", Integer.toString(item.getMrcm()))
          .setAttribute("mridenttyp", item.getMridenttyp())
          .setAttribute("mrwm", Integer.toString(item.getMrwm()))
          .setAttribute("mrsway", Integer.toString(item.getMrsway()))
          .setAttribute("mrspark", Integer.toString(item.getMrspark()))
          .setAttribute("mrcodea", Integer.toString(item.getMrcodea()))
          .setAttribute("mrcodeb", Integer.toString(item.getMrcodeb()))
          .setAttribute("mrup", Integer.toString(item.getMrup()))
          .setAttribute("mrdn", Integer.toString(item.getMrdn()))
          .setAttribute("mrb", item.getMrb().getTimeValue().replace(".000", ""))
          .setAttribute("mre", item.getMre().getTimeValue().replace(".000", ""))
          .setAttribute("mrs", item.getMrs())
          .setAttribute("mrvmax", Integer.toString(item.getMrvmax()))
          .setAttribute("mrwseat", Integer.toString(item.getMrwseat()))
          .setAttribute("mrcserv", Integer.toString(item.getMrcserv()))
          .setAttribute("mrcfault", Integer.toString(item.getMrcfault()))
          .setAttribute("mrcbadge", Integer.toString(item.getMrcbadge()))
          .setAttribute("mrmcolor", Integer.toString(item.getMrmcolor()))
          .setAttribute("mrtrainnum", Integer.toString(item.getMrtrainnum()))
          .setAttribute("mrtrainia1", Integer.toString(item.getMrtrainia1()))
          .setAttribute("mrtrainia2", Integer.toString(item.getMrtrainia2()))
          .setAttribute("mrtrainia3", Integer.toString(item.getMrtrainia3()))
          .setAttribute("mrlkmcount", Integer.toString(item.getMrlkmcount()))
          .setAttribute("mrspecflag", Integer.toString(item.getMrspecflag()))
          .setAttribute("mrowefact", item.getMrowefact().getTimeValue().replace(".000", ""))
          .setAttribute("mrowcserv", Integer.toString(item.getMrowcserv()))
          .setAttribute("mrowccause", Integer.toString(item.getMrowccause()))
          .setAttribute("mrowpassq", Integer.toString(item.getMrowpassq()))
          .setAttribute("mrowgruzq", Integer.toString(item.getMrowgruzq()))
          .setAttribute("mrowpassdel", Integer.toString(item.getMrowpassdel()))
          .setAttribute("mrowgruzdel", Integer.toString(item.getMrowgruzdel()))
          .setAttribute("mrdatecorr", item.getMrdatecorr().getTimeValue().replace(".000", ""))
          .setAttribute("mrtigid", Integer.toString(item.getMrtigid()))
          .setAttribute("mrtop", Integer.toString(item.getMrtop()))
          .setAttribute("mrtin", item.getMrtin().getTimeValue().replace(".000", ""))
          .setAttribute("mrwp", Integer.toString(item.getMrwp()))
          .setAttribute("mrtm", Integer.toString(item.getMrtm()))
          .setAttribute("train_guid", item.getTrainGuid())
          .setAttribute("linked_mark_guid", item.getLinkedMarkGuid())
          .setAttribute("timetable_id", Long.toString(item.getTimetableId()))
          .setAttribute("comments", item.getComments())
          .setAttribute("rwc_begin_km", Integer.toString(item.getRwcBegin().getKm()))
          .setAttribute("rwc_begin_pk", Integer.toString(item.getRwcBegin().getPk()))
          .setAttribute("rwc_end_km", Integer.toString(item.getRwcEnd().getKm()))
          .setAttribute("rwc_end_pk", Integer.toString(item.getRwcEnd().getPk()));
      
      item.getFeaturesList().forEach(item2 -> {
        Element feature = new Element("Features")
          .setAttribute("type", item2.getType())
          .setAttribute("source", item2.getSource())
          .setAttribute("time", item2.getTime().getTimeValue().replace(".000", ""))
          .setAttribute("name", item2.getName())
          .setAttribute("value", item2.getValue());
        warn.addContent(feature);
      });
        
      root.addContent(warn);
    });
    
    return document;
  }


}
