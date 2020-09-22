package loader.elbrus.xml;

import java.text.SimpleDateFormat;
import java.util.Date;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.TimetableWarnsMessage;
import org.jdom.Document;
import org.jdom.Element;

/**
 * XMLWarning 
 * 
 * Формирует XML - класса Document c информацией о наличии ограничения движения поездов по данным Эльбрус
 * LIMITSPEED (Ограничения скорости)
 *  
 */

public class XMLWarning {
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

  public Document createXMLFile(TimetableWarnsMessage timetableWarnsMessage, MRequest mRequest,
      Date dateStart, Date dateFinish, String url) throws Exception {
    
    Document document = new Document();
    Element root = new Element("WarnInfo");
    root.setAttribute("dtRequest", FORMAT_DATE.format(new Date()));
    root.setAttribute("TypeGr", mRequest.getTypeParam());
    root.setAttribute("User", mRequest.getUserParam());
    root.setAttribute("Reference", "");
    root.setAttribute("dtStart", FORMAT_DATE.format(dateStart));
    root.setAttribute("dtFinish", FORMAT_DATE.format(dateFinish));
    root.setAttribute("mq", "activemq:" + url);
    document.setRootElement(root);
    
    timetableWarnsMessage.getTimetableWarnsList().forEach(item -> {
      Element warn = new Element("Warn")
        .setAttribute("eid", Long.toString(item.getWarnEid()))
        .setAttribute("begin_esr6", Long.toString(item.getStationBeginEsr6()))
        .setAttribute("end_esr6", Long.toString(item.getStationEndEsr6()))
        .setAttribute("state", Long.toString(item.getState()))
        .setAttribute("place", Long.toString(item.getPlace()))
        .setAttribute("time_from", item.getTimeFrom().getTimeValue().replace(".000", ""))
        .setAttribute("time_to", item.getTimeTo().getTimeValue().replace(".000", ""))
        .setAttribute("nature", Long.toString(item.getNature()))
        .setAttribute("v_pass", Long.toString(item.getSpeedPass()))
        .setAttribute("v_cargo", Long.toString(item.getSpeedCargo()))
        .setAttribute("v_fast_pass", Long.toString(item.getSpeedFastPass()))
        .setAttribute("v_mixed_cargo", Long.toString(item.getSpeedMixedCargo()))
        .setAttribute("spec_flag", Long.toString(item.getSpecFlag()))
        .setAttribute("reason", Long.toString(item.getReason()))
        .setAttribute("direct", Long.toString(item.getWarnDirection()))
        .setAttribute("time_confirm", item.getTimeConfirm().getTimeValue().replace(".000", ""))
        .setAttribute("track_num", Long.toString(item.getTrackNum()))
        .setAttribute("begin_km", Long.toString(item.getRwcBegin().getKm()))
        .setAttribute("begin_pk", Long.toString(item.getRwcBegin().getPk()))
        .setAttribute("end_km", Long.toString(item.getRwcEnd().getKm()))
        .setAttribute("end_pk", Long.toString(item.getRwcEnd().getPk()))
        .setAttribute("mod_date", item.getModDate().getTimeValue().replace(".000", ""));
      root.addContent(warn);
    });
    
    return document;
  }

}
