package loader.elbrus.xml;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jdom.Document;
import org.jdom.Element;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.TimetableSpeedLimitsMessage;

/**
 * XMLLimits 
 * 
 * Формирует XML - класса Document c информацией об ограничениях по данным Эльбрус
 * LIMITS (Ограничения)
 *  
 */

public class XMLLimits {
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

  public Document createXMLFile(TimetableSpeedLimitsMessage timetableSpeedLimitsMessage, MRequest mRequest,
      Date dateStart, Date dateFinish, String url) throws Exception {
    
    Document document = new Document();
    Element root = new Element("LimitsInfo");
    root.setAttribute("dtRequest", FORMAT_DATE.format(new Date()));
    root.setAttribute("TypeGr", mRequest.getTypeParam());
    root.setAttribute("User", mRequest.getUserParam());
    root.setAttribute("Reference", "");
    root.setAttribute("dtStart", FORMAT_DATE.format(dateStart));
    root.setAttribute("dtFinish", FORMAT_DATE.format(dateFinish));
    root.setAttribute("mq", "activemq:" + url);
    document.setRootElement(root);
    
    timetableSpeedLimitsMessage.getTimetableSpeedLimitsList().forEach(item -> {
      Element speedLimits = new Element("SpeedLimits")
          .setAttribute("guid", item.getGuid())
          .setAttribute("station_begin_esr6", Integer.toString(item.getStationBeginEsr6()))
          .setAttribute("station_end_esr6", Integer.toString(item.getStationEndEsr6()))
          .setAttribute("state", Integer.toString(item.getState()))
          .setAttribute("place", Integer.toString(item.getPlace()))
          .setAttribute("time_from", item.getTimeFrom().getTimeValue().replace(".000", ""))
          .setAttribute("time_to", item.getTimeTo().getTimeValue().replace(".000", ""))
          .setAttribute("nature", Integer.toString(item.getNature()))
          .setAttribute("spec_flag", Integer.toString(item.getSpecFlag()))
          .setAttribute("reason", Integer.toString(item.getReason()))
          .setAttribute("warn_direction", Integer.toString(item.getWarnDirection()))
          .setAttribute("time_confirm", item.getTimeConfirm().getTimeValue().replace(".000", ""))
          .setAttribute("track_num", Integer.toString(item.getTrackNum()))
          .setAttribute("rwc_begin_km", Integer.toString(item.getRwcBegin().getKm()))
          .setAttribute("rwc_begin_pk", Integer.toString(item.getRwcBegin().getPk()))
          .setAttribute("rwc_end_km", Integer.toString(item.getRwcEnd().getKm()))
          .setAttribute("rwc_end_pk", Integer.toString(item.getRwcEnd().getPk()))
          .setAttribute("mod_date", item.getModDate().getTimeValue().replace(".000", ""))
          .setAttribute("request_num", Integer.toString(item.getRequestNum()))
          .setAttribute("service_code", Integer.toString(item.getServiceCode()))
          .setAttribute("comments", item.getComments())
          .setAttribute("enabled", Boolean.toString(item.getEnabled()))
          .setAttribute("default_speed", Integer.toString(item.getDefaultSpeed()))
          .setAttribute("default_active", Boolean.toString(item.getDefaultActive()))
          .setAttribute("custom_data", new String(item.getCustomData().toByteArray(), StandardCharsets.UTF_8))
          .setAttribute("delta_time_odd", Integer.toString(item.getDeltaTimeOdd()))
          .setAttribute("delta_time_even", Integer.toString(item.getDeltaTimeEven()));
      
      item.getTypeSpeedsList().forEach(item2 -> {
        Element typeSpeeds = new Element("TypeSpeeds")
          .setAttribute("train_type", item2.getTrainType())
          .setAttribute("train_nums", item2.getTrainNums())
          .setAttribute("speed", Integer.toString(item2.getSpeed()))
          .setAttribute("active", Boolean.toString(item2.getActive()))
          .setAttribute("delta_time_odd", Integer.toString(item2.getDeltaTimeOdd()))
          .setAttribute("delta_time_even", Integer.toString(item2.getDeltaTimeEven()));
        speedLimits.addContent(typeSpeeds);
      });
      
      item.getFeaturesList().forEach(item2 -> {
        Element features = new Element("Features")
          .setAttribute("key", item2.getKey())
          .setAttribute("value", item2.getValue());
        speedLimits.addContent(features);
      });
      
      item.getTrackParkListList().forEach(item2 -> {
        Element trackParkList = new Element("TrackParkList")
          .setAttribute("track_code", Integer.toString(item2.getTrackCode()))
          .setAttribute("park_code", Integer.toString(item2.getParkCode()));
        speedLimits.addContent(trackParkList);
      });

      root.addContent(speedLimits);
    });
    
    return document;
  }


}
