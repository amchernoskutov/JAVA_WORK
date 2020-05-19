package loader.comm.soapclients;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import loader.comm.config.xml.LSConfig;
import loader.comm.form.MainForm;
import loader.comm.log.LSData;
import loader.comm.log.LSLog;
import loader.comm.model.wsdl.Z63LASEREPORTGETOPER;
import loader.comm.model.wsdl.Z63LASEREPORTGETOPERResponse;
import loader.comm.model.xml.MRequest;
import loader.comm.mqsender.MQSender;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * SoapClientMM
 * 
 * Формирует SOAP запрос на получение данных по маршруту машиниста, полученный ответ
 * записывает в файл и направляет путь к файлу Active MQ сообщением в Disparcher.
 * 
 */

@Service
public class SoapClientMM {

  @Autowired
  private Jaxb2Marshaller jaxb2Marshaller;
  
  @Autowired
  private LSConfig lsConfig;
  
  @Autowired
  private LSData lsData;
  
  @Autowired
  private MQSender mqSender;

  private WebServiceTemplate webServiceTemplate;
  
  public Z63LASEREPORTGETOPERResponse getItemInfo(Z63LASEREPORTGETOPER itemRequest){
      webServiceTemplate = new WebServiceTemplate(jaxb2Marshaller);
      
      return (Z63LASEREPORTGETOPERResponse) 
          webServiceTemplate.marshalSendAndReceive(lsConfig.getConfig().getSOAPRequestParam().getUrl(), itemRequest, 
              
              new WebServiceMessageCallback() {

            public void doWithMessage(WebServiceMessage message) throws IOException {
              
              TransportContext context = TransportContextHolder.getTransportContext();
              HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();

              connection.addRequestHeader("Content-Type", "text/xml; charset=utf-8");
              connection.addRequestHeader("SoapAction", lsConfig.getConfig().getSOAPRequestParam().getSoapActionOPER());  
              connection.addRequestHeader("Accept-Encoding", "gzip;deflate");

              byte[] encodedBytes = Base64.getEncoder().encode((lsConfig.getConfig().getSOAPRequestParam().getLogin() + ":" + 
              lsConfig.getConfig().getSOAPRequestParam().getPassword()).getBytes());  
              connection.addRequestHeader("Authorization", "Basic " + new String(encodedBytes));
            }
          });
  }
  
  /**
   * generateSOAPData
   * Формирование SOAP запроса, получение SOAP ответа. Ответ записывается в файл,
   * путь передается MQ сообщением.
   * @param mRequest - элемент данных Request из файла configloadercomm.xml по которому формировалься запрос 
   * @param dorNumber - номер догоги
   * @param depoNumber - номер пути
   * @param requestIntervalTimeMinute - интервал времени в минутах за который получены данные
   * @return - true при успешности получения данных, false - при неуспешности
   */
  
  public boolean generateSOAPData (MRequest mRequest, int dorNumber, int depoNumber, int requestIntervalTimeMinute) {
    String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
    String messageTemporary;
    MainForm mainForm = new MainForm();
    mainForm.setDateCreate(new Date());
    mainForm.setName(mRequest.getName());
    mainForm.setDor("-");
    mainForm.setDepo("-");
    
    boolean soapRequestSuccessful = true;
    InputStream respond = null;
    String filePathRespond=null;
    
    try {
      respond = createSOAPRequest(mRequest, dorNumber, depoNumber);
      messageTemporary = "Successfully send SOAP request on date;" + LSLog.FORMAT_DATE.format(mRequest.getDataStartTime()); 
    } catch (Exception e) {
      soapRequestSuccessful = false;
      messageTemporary = "ERROR create SOAP request on date;" + LSLog.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
      ";" + e.getMessage();
    }
    message = message + ";" + messageTemporary;
    mainForm.setRequest(messageTemporary.replace(";", ": "));

    if (soapRequestSuccessful) {
      try {
        filePathRespond = lsData.writeSOAPrespond(mRequest, respond, requestIntervalTimeMinute, 0, 0);
        messageTemporary = "Successfully write SOAP respond to file " + filePathRespond; 
      } catch (Exception e) {
        soapRequestSuccessful = false;
        messageTemporary = "ERROR write SOAP respond " + e.getMessage();
      }
    }
    message = message + ";" + messageTemporary;
    mainForm.setWrite(messageTemporary.replace(";", ": "));
      
    if (soapRequestSuccessful) {
      try {
        createMQSender(filePathRespond, mRequest, requestIntervalTimeMinute);
        messageTemporary = "Successfully create MQ Sender"; 
      } catch (Exception e) {
        soapRequestSuccessful = false;
        messageTemporary = "ERROR create MQ Sender "+ e.getMessage();
      }
    }
    message = message + ";" + messageTemporary;
    mainForm.setMqSender(messageTemporary);
    
    if (soapRequestSuccessful) LSLog.Info(message); else LSLog.Severe(message);
    LSLog.mainForms.add(mainForm);
    if (LSLog.mainForms.size() > LSLog.MAX_MAINFORMS_SHOW) LSLog.mainForms.remove(0);
    
    return soapRequestSuccessful;
  }

  // Формирование SOAP запроса и получение SOAP ответа
  private InputStream createSOAPRequest(MRequest mRequest, int dorNumber, int depoNumber) throws Exception {
    
    Z63LASEREPORTGETOPER request = new Z63LASEREPORTGETOPER();
    request.setCDOR(dorNumber);
    request.setCDEPO(depoNumber);
    request.setMDATE(LSLog.FORMAT_DATE_SOAP.format(mRequest.getDataStartTime()));
    
    Z63LASEREPORTGETOPERResponse response =
              (Z63LASEREPORTGETOPERResponse) this.getItemInfo(request);
    
    String spellResult = response.getRESULT();

    byte[] decodedBytes = Base64.getDecoder().decode(spellResult);
    InputStream respond = new ByteArrayInputStream(decodedBytes);
      
    byte[] bytes;
    bytes = IOUtils.toByteArray(new GZIPInputStream(respond));
    respond = new ByteArrayInputStream(bytes);
    return respond;
  }


  // Формирование MQ сообщения
  private void createMQSender(String filePathRespond, MRequest mRequest, int requestIntervalTimeMinute) throws Exception { 
    mqSender.setId(mRequest.getId());
    mqSender.setDate1(new Timestamp(mRequest.getDataStartTime().getTime()));
    mqSender.setDate2(new Timestamp(DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime()));
    mqSender.setData_name(mRequest.getName());
    mqSender.setSystem_name(LSLog.SHORT_LOADERCOMM_NAME);
    mqSender.setXML_name("/" + filePathRespond.replace("\\", "/"));
    mqSender.send("");
  }
  
}