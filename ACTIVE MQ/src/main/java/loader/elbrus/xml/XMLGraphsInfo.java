package loader.elbrus.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.TimetableMessage;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * XMLGraphsInfo 
 * 
 * Формирует XML - класса Document c графиками движения поездов по данным Эльбрус
 * NORMATIVE (Нормативные графики)
 * FORECASE (Прогнозные графики)
 * GIDEXPORT (Прогнозный в ГИД)
 * ACTUAL (Исполненный)
 * ACTUAL (Прочие графики)
 *  
 */

@Service
public class XMLGraphsInfo {
  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  public static final SimpleDateFormat FORMAT_DATE_YYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
  public static final SimpleDateFormat FORMAT_DATE_MARSHRUT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  @Autowired
  private LAConfig laConfig;

  /**
   * createXMLFile
   * Формирует XML - класса Document 
   * @param timetableInfosMessage - интервал времени в минутах за который получены данные
   * @param mRequest - элемент данных Request из файла configloaderelbrus.xml по которому формировалься запрос
   * @param reference - значение тэга reference из файла с информацией о наличии графиков движения поездов  
   * @param dateStart - дата с которой получены данные
   * @param dateFinish - дата до которой получены данные
   * @return - возвражает готовый файл XML в виде экземпляра класса Document
   * @throws Exception - любые ошибки связанные с формированием файл XML в виде экземпляра класса Document
   */

  public Document createXMLFile(TimetableMessage timetableMessage, MRequest mRequest, 
      String reference, Date dateStart, Date dateFinish) throws Exception {

    Document document = new Document();
    Element root = new Element("DataMarshrut")
        .setAttribute("dtRequest", FORMAT_DATE.format(new Date()))
        .setAttribute("TypeGr", mRequest.getTypeParam())
        .setAttribute("User", mRequest.getUserParam())
        .setAttribute("Reference", reference)
        .setAttribute("dtStart", FORMAT_DATE.format(dateStart))
        .setAttribute("dtFinish", FORMAT_DATE.format(dateFinish))
        .setAttribute("mq", "activemq:" + laConfig.getConfig().getActiveMQRequestParam().getUrl());
    document.setRootElement(root);

    timetableMessage.getTrainThreadsList().stream().filter(f -> (f.getTrainPointsCount() >= 3))
        .forEach(item -> {

          List<TTrainPointInfo> lstTrPoint = new ArrayList<TTrainPointInfo>();
          int nMarsh = 0;

          item.getTrainPointsList().stream().filter(f1 -> ((f1.getCodeGidOperation() == 1)
              | (f1.getCodeGidOperation() == 3) | (f1.getCodeGidOperation() == 4)))
              .forEach(item1 -> {
                TTrainPointInfo tTrainPointInfo =
                    new TTrainPointInfo(item1.getTrainNum(), item1.getStationCurrentEsr6(),
                        item1.getCodeGidOperation(), item1.getEventTime());
                lstTrPoint.add(tTrainPointInfo);
              });

          if (lstTrPoint.size() >= 2) {
            TTrainPointInfo lt0 = lstTrPoint.get(0);
            int nnPoint = lstTrPoint.size(), i = 0;
            while (i < nnPoint) {
              TTrainPointInfo lt = lstTrPoint.get(i);
              lt.setDtIn(lt.getDt());
              lt.setDtOut(lt.getDt());
              if (i > 0) {
                if (i == 1 && lt0.getTrNum() != lt.getTrNum()) {
                  // часто номер поезда в первой строке отличается от номера в последующих
                  lt0.setTrNum(lt.getTrNum());
                }
                if (lt.getESR() == lt0.getESR()) {
                  if (lt.getDt().getTimeValue().compareTo(lt0.getDt().getTimeValue()) > 0) {
                    lt0.setDtOut(lt.getDt());
                  }
                  lstTrPoint.remove(i);
                  i--;
                  nnPoint--;
                } else
                  lt0 = lt;
              }
              i++;
            }

            nMarsh++;

            Element marshrut = new Element("Marshrut")
                .setAttribute("GRAFTYPE", mRequest.getTypeParam())
                .setAttribute("STATUS", "0")
                .setAttribute("M_NMARSH", Integer.toString(nMarsh))
                .setAttribute("M_DATEN", FORMAT_DATE_YYYYMMDD.format(dateStart))
                .setAttribute("train_eid", Long.toString(item.getTrainEid()))
                .setAttribute("train_uid", Long.toString(item.getTrainUid()))
                .setAttribute("train_num", Long.toString(item.getTrainNum()))
                .setAttribute("train_type_id", Long.toString(item.getTrainTypeId()))
                .setAttribute("begin_esr6", Long.toString(item.getBeginEsr6()))
                .setAttribute("end_esr6", Long.toString(item.getEndEsr6()))
                .setAttribute("comment", item.getComment())
                .setAttribute("calendar_label", item.getCalendarLabel())
                .setAttribute("fixed_thread", item.getFixedThread())
                .setAttribute("overlay_thread", Boolean.toString(item.getOverlayThread()));

            Element trainAttributes = new Element("train_attributes")
                .setAttribute("explosives", Boolean.toString(item.getTrainAttributes().getExplosives()));
            
            Element oversize = new Element("oversize")
                .setAttribute("top", Long.toString(item.getTrainAttributes().getOversize().getTop()))
                .setAttribute("bottom", Long.toString(item.getTrainAttributes().getOversize().getBottom()))
                .setAttribute("side", Long.toString(item.getTrainAttributes().getOversize().getSide()))
                .setAttribute("special", Long.toString(item.getTrainAttributes().getOversize().getSpecial()));
            trainAttributes.addContent(oversize);
            marshrut.addContent(trainAttributes);
            
            Element mPoezdka = new Element("m_Poezdka");

            for(int j=0; j < lstTrPoint.size(); j++) {
              TTrainPointInfo lt = lstTrPoint.get(j);
              Element poezdka;
              try {
                poezdka = new Element("Poezdka")
                    .setAttribute("P_ESR", Long.toString(lt.getESR()))
                    .setAttribute("P_NSTR", Long.toString(j+1))
                    .setAttribute("P_DATETIMEPR",   FORMAT_DATE.format(FORMAT_DATE_MARSHRUT.parse(lt.getDtIn().getTimeValue())))
                    .setAttribute("P_DATETIMEOTPR", FORMAT_DATE.format(FORMAT_DATE_MARSHRUT.parse(lt.getDtOut().getTimeValue())))
                    .setAttribute("P_POEZD", Long.toString(lt.getTrNum()));
                mPoezdka.addContent(poezdka);
              } catch (ParseException e) {
                e.printStackTrace();
              }
            };
            
            marshrut.addContent(mPoezdka);
            root.addContent(marshrut);
          }
    });
    return document;
  }

}
