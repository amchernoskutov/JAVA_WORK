package loader.elbrus.xml;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jdom.Document;
import org.jdom.Element;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.TimetableGapsMessage;

/**
 * XMLGaps 
 * 
 * Формирует XML - класса Document c информацией об ограничениях по данным Эльбрус
 * LIMITS (Ограничения)
 *  
 */

public class XMLGaps {
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

  public Document createXMLFile(TimetableGapsMessage timetableGapsMessage, MRequest mRequest,
      Date dateStart, Date dateFinish, String url) throws Exception {
    
    Document document = new Document();
    Element root = new Element("GapsInfo");
    root.setAttribute("dtRequest", FORMAT_DATE.format(new Date()));
    root.setAttribute("TypeGr", mRequest.getTypeParam());
    root.setAttribute("User", mRequest.getUserParam());
    root.setAttribute("Reference", "");
    root.setAttribute("dtStart", FORMAT_DATE.format(dateStart));
    root.setAttribute("dtFinish", FORMAT_DATE.format(dateFinish));
    root.setAttribute("mq", "activemq:" + url);
    document.setRootElement(root);
    
    timetableGapsMessage.getTimetableGapsList().forEach(item -> {
      Element gaps = new Element("Gaps")
          .setAttribute("guid", item.getGuid())
          .setAttribute("gap_eid", Long.toString(item.getGapEid()))
          .setAttribute("guid", item.getGuid())
          .setAttribute("station_begin_esr6", Integer.toString(item.getStationBeginEsr6()))
          .setAttribute("station_end_esr6", Integer.toString(item.getStationEndEsr6()))
          .setAttribute("length_to_begin", Double.toString(item.getLengthToBegin()))
          .setAttribute("length_to_end", Double.toString(item.getLengthToEnd()))
          .setAttribute("time_in_begin_from", item.getTimeInBeginFrom().getTimeValue().replace(".000", ""))
          .setAttribute("time_in_begin_to", item.getTimeInBeginTo().getTimeValue().replace(".000", ""))
          .setAttribute("time_in_end_from", item.getTimeInEndFrom().getTimeValue().replace(".000", ""))
          .setAttribute("time_in_end_to", item.getTimeInEndTo().getTimeValue().replace(".000", ""))
          .setAttribute("place", Integer.toString(item.getPlace()))
          .setAttribute("track_num", Integer.toString(item.getTrackNum()))
          .setAttribute("comment", item.getComment())
          .setAttribute("modify_time_elbrus", item.getModifyTimeElbrus().getTimeValue().replace(".000", ""))
          .setAttribute("color", Integer.toString(item.getColor()))
          .setAttribute("gid_flags", Integer.toString(item.getGidFlags()))
          .setAttribute("time_over", item.getTimeOver().getTimeValue().replace(".000", ""))
          .setAttribute("modify_time_gid", item.getModifyTimeGid().getTimeValue().replace(".000", ""))
          .setAttribute("extra_flags", Integer.toString(item.getExtraFlags()))
          .setAttribute("gap_enabled", Boolean.toString(item.getGapEnabled()))
          .setAttribute("gap_type", item.getGapType())
          .setAttribute("train_pack_size_odd", Integer.toString(item.getTrainPackSizeOdd()))
          .setAttribute("train_pack_size_even", Integer.toString(item.getTrainPackSizeEven()))
          .setAttribute("permanent", Boolean.toString(item.getPermanent()))
          .setAttribute("vmax_odd", Double.toString(item.getVmaxOdd()))
          .setAttribute("vmax_even", Double.toString(item.getVmaxEven()))
          .setAttribute("vmax_lenth_to_begin", Double.toString(item.getVmaxLenthToBegin()))
          .setAttribute("vmax_lenth_to_end", Double.toString(item.getVmaxLenthToEnd()))
          .setAttribute("enabled_wrong", Boolean.toString(item.getEnabledWrong()))
          .setAttribute("delta_time_odd", Integer.toString(item.getDeltaTimeOdd()))
          .setAttribute("delta_time_even", Integer.toString(item.getDeltaTimeEven()))
          .setAttribute("vmax_wrong_odd_begin", Double.toString(item.getVmaxWrongOddBegin()))
          .setAttribute("vmax_wrong_even_begin", Double.toString(item.getVmaxWrongEvenBegin()))
          .setAttribute("vmax_wrong_odd_end", Double.toString(item.getVmaxWrongOddEnd()))
          .setAttribute("vmax_wrong_even_end", Double.toString(item.getVmaxWrongEvenEnd()))
          .setAttribute("executing", Boolean.toString(item.getExecuting()))
          .setAttribute("rwc_begin_km", Integer.toString(item.getRwcBegin().getKm()))
          .setAttribute("rwc_begin_pk", Integer.toString(item.getRwcBegin().getPk()))
          .setAttribute("rwc_end_km", Integer.toString(item.getRwcEnd().getKm()))
          .setAttribute("rwc_end_pk", Integer.toString(item.getRwcEnd().getPk()))
          .setAttribute("park_num", Integer.toString(item.getParkNum()))
          .setAttribute("custom_data", new String(item.getCustomData().toByteArray(), StandardCharsets.UTF_8))
          .setAttribute("vmax_rwc_begin_km", Integer.toString(item.getVmaxRwcBegin().getKm()))
          .setAttribute("vmax_rwc_begin_pk", Integer.toString(item.getVmaxRwcBegin().getPk()))
          .setAttribute("vmax_rwc_end_km", Integer.toString(item.getVmaxRwcEnd().getKm()))
          .setAttribute("vmax_rwc_end_pk", Integer.toString(item.getVmaxRwcEnd().getPk()))
          .setAttribute("u_turn_time_odd", Integer.toString(item.getUTurnTimeOdd()))
          .setAttribute("u_turn_time_even", Integer.toString(item.getUTurnTimeEven()))
          .setAttribute("u_turn_time_stop_odd", Integer.toString(item.getUTurnTimeStopOdd()))
          .setAttribute("u_turn_time_stop_even", Integer.toString(item.getUTurnTimeStopEven()))
          .setAttribute("signal_type_odd", item.getSignalTypeOdd())
          .setAttribute("signal_type_even", item.getSignalTypeEven())
          .setAttribute("time_off_wrong_track", Integer.toString(item.getTimeOffWrongTrack()))
          .setAttribute("source", item.getSource())
          .setAttribute("source_guid", item.getSourceGuid());
      
      item.getGapDirectionsList().forEach(item2 -> {
        Element typeSpeeds = new Element("Directions")
          .setAttribute("enabled_odd", Boolean.toString(item2.getEnabledOdd()))
          .setAttribute("enabled_even", Boolean.toString(item2.getEnabledEven()))
          .setAttribute("time_from", item2.getTimeFrom().getTimeValue().replace(".000", ""));
        gaps.addContent(typeSpeeds);
      });
      
      item.getFeaturesList().forEach(item2 -> {
        Element features = new Element("Features")
          .setAttribute("key", item2.getKey())
          .setAttribute("value", item2.getValue());
        gaps.addContent(features);
      });
      
      item.getTrackParkListList().forEach(item2 -> {
        Element trackParkList = new Element("TrackParkList")
          .setAttribute("track_code", Integer.toString(item2.getTrackCode()))
          .setAttribute("park_code", Integer.toString(item2.getParkCode()));
        gaps.addContent(trackParkList);
      });

      root.addContent(gaps);
    });
    
    return document;
  }

}
