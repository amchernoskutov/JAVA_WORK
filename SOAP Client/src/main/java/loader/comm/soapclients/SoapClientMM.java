package loader.comm.soapclients;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import loader.comm.LoadercommApplication;
import loader.comm.config.data.GeneralData;
import loader.comm.config.data.LSData;
import loader.comm.config.xml.LSConfig;
import loader.comm.form.MainForm;
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

public class SoapClientMM {
  private Jaxb2Marshaller jaxb2Marshaller;
  private WebServiceTemplate webServiceTemplate;
  private LSConfig lsConfig;
  private LSData lsData;
  
  public SoapClientMM(LSConfig lsConfig, LSData lsData, Jaxb2Marshaller jaxb2Marshaller) {
    this.lsConfig=lsConfig;
    this.lsData=lsData;
    this.jaxb2Marshaller = jaxb2Marshaller;
  }
  
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
   * @param codeRoad - номер догоги
   * @param codeDepo - номер пути
   * @param requestIntervalTimeMinute - интервал времени в минутах за который получены данные
   * @return - true при успешности получения данных, false - при неуспешности
   */
  
  public boolean generateSOAPData (MRequest mRequest, int codeRoad, Date dataStartTime, int codeDepo, int requestIntervalTimeMinute) {
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
      respond = createSOAPRequest(mRequest, codeRoad, dataStartTime, codeDepo);
      messageTemporary = "Successfully send SOAP request on date;" + GeneralData.FORMAT_DATE.format(dataStartTime); 
    } catch (Exception e) {
      soapRequestSuccessful = false;
      messageTemporary = "ERROR create SOAP request on date;" + GeneralData.FORMAT_DATE.format(dataStartTime) + 
      ";" + e.getMessage();
    }
    message = message + ";" + messageTemporary;
    mainForm.setRequest(messageTemporary.replace(";", ": "));

    if (soapRequestSuccessful) {
      try {
        filePathRespond = lsData.writeSOAPrespond(mRequest, respond, requestIntervalTimeMinute, codeRoad, dataStartTime, codeDepo);
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
        createMQSender(filePathRespond, mRequest, dataStartTime, requestIntervalTimeMinute, codeRoad);
        messageTemporary = "Successfully create MQ Sender"; 
      } catch (Exception e) {
        soapRequestSuccessful = false;
        messageTemporary = "ERROR create MQ Sender "+ e.getMessage();
      }
    }
    message = message + ";" + messageTemporary;
    mainForm.setMqSender(messageTemporary);
    
    if (soapRequestSuccessful) LoadercommApplication.logger.fatal(message); else LoadercommApplication.logger.fatal(message);
    GeneralData.mainForms.add(mainForm);
    if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
    
    return soapRequestSuccessful;
  }

  // Формирование SOAP запроса и получение SOAP ответа
  private InputStream createSOAPRequest(MRequest mRequest, int codeRoad, Date dataStartTime, int codeDepo) throws Exception {
    
    Z63LASEREPORTGETOPER request = new Z63LASEREPORTGETOPER();  
    if (codeRoad != 0) {
      request.setCDOR(codeRoad);
    }
    request.setCDEPO(codeDepo); 
    request.setMDATE(GeneralData.FORMAT_DATE_SOAP.format(dataStartTime));
    
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
  private void createMQSender(String filePathRespond, MRequest mRequest, Date dataStartTime, int requestIntervalTimeMinute, int codeRoad) throws Exception {
    MQSender mqSender = new MQSender(this.lsConfig);
    mqSender.setId(mRequest.getId());
    mqSender.setCodeRoad(codeRoad);
    mqSender.setDate1(new Timestamp(dataStartTime.getTime()));
    mqSender.setDate2(new Timestamp(DateUtils.addMinutes(dataStartTime, requestIntervalTimeMinute).getTime()));
    mqSender.setData_name(mRequest.getName());
    mqSender.setSystem_name(GeneralData.SHORT_LOADERCOMM_NAME);
    mqSender.setXML_name("/" + filePathRespond.replace("\\", "/"));
    mqSender.send("");
  }
  
}