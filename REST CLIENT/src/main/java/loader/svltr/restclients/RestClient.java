package loader.svltr.restclients;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import loader.svltr.config.xml.LSConfig;
import loader.svltr.form.MainForm;
import loader.svltr.log.LSData;
import loader.svltr.log.LSLog;
import loader.svltr.model.xml.MRequest;
import loader.svltr.mqsender.MQSender;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * REST Client SVLTR
 * 
 * Формирует REST запрос на получение данных от СВЛ ТР - это 
 * получение token необходимого для авторизации в REST запросах и сами запросы на получение данных:
 * 
 * SPEED_HISTORY - История изменения скорости движения
 * COORD_HISTORY - История изменения географических координат
 * TRAFFIC_LIGHT_HISTORY - История сигналов АЛСН
 * MEK_HISTORY - История МЭК
 * FUEL - История изменения данных по топливу (тепловозы)
 * DIESEL_RUN_HISTORY - История запуска/остановки дизеля (тепловозы)
 * ELECTRIC_DATA - Данные по электроэнергии
 * VAGONS_DATA - Данные по вагонам 
 * ASOUP_DATA - История операций АСОУП
 *
 * Полученный ответ записывает в файл и направляет путь к файлу Active MQ сообщением в Disparcher.
 * 
 */

@Service
public class RestClient {
  private String token;
  private static final String DELIMETR = "/"; 
  private static final int COUNT_SEGMENT = 3; 
  private static final String ASSIGMENT_DATA = "assignment_data";
  private static final String VAGONS_DATA = "vagons_data";
  private static final SimpleDateFormat FORMAT_DATE_REST = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  private static final SimpleDateFormat FORMAT_DATE_ERROR_400 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  
  public ConcurrentHashMap<String, Object> intervalAndDateTime;  

  @Autowired
  private LSConfig lsConfig;

  @Autowired
  private LSData lsData;

  @Autowired
  private MQSender mqSender;

  public RestClient() {
    intervalAndDateTime = new ConcurrentHashMap<String, Object>();
    intervalAndDateTime.put("minInterval", 0);
    intervalAndDateTime.put("currentDateTime", new Date());
  }
  
  /**
   * Формирует REST запрос на получение token от СВЛ ТР
   * @return возвращает true при успешности получения token и false при неуспешности.
   * 
   */
  
  public boolean getToken() {
    String message = "Get token for create request";
    String messageTemporary;
    MainForm mainForm = new MainForm();
    mainForm.setDateCreate(new Date());
    mainForm.setName("TOKEN");
    mainForm.setDor("-");
    mainForm.setDepo("-");
    boolean soapRequestSuccessful = true;

    OkHttpClient client = new OkHttpClient();
    
    String[] subStrSegment = checkSegment(mainForm, message, lsConfig.getConfig().getRestRequestParam().getUrlapi()); 
    if (subStrSegment.length != COUNT_SEGMENT) return false;
    
    HttpUrl httpUrl = new HttpUrl.Builder()
        .scheme("https")
        .host(lsConfig.getConfig().getRestRequestParam().getUrl())
        .addPathSegment(subStrSegment[0])
        .addPathSegment(subStrSegment[1])
        .addPathSegment(subStrSegment[2])
        .addQueryParameter("pass", lsConfig.getConfig().getRestRequestParam().getPassword())
        .addQueryParameter("username", lsConfig.getConfig().getRestRequestParam().getUsername())
        .build();

    Request requesthttp = new Request.Builder()
        .url(httpUrl) 
        .addHeader("accept", "application/json")
        .build();

    try {
      Response response = client.newCall(requesthttp).execute();
      messageTemporary = "Successfully sent REST request"; 
      message = message + ";" + messageTemporary;
      mainForm.setRequest(messageTemporary);
      
      if (response.code() == 200) { 
        Object obj = new JSONParser().parse(response.body().string()); 
        JSONObject jo = (JSONObject) obj; 
        token = (String) jo.get("token"); 
        
        messageTemporary = "Successfully received request and token"; 
        message = message + ";" + messageTemporary;
        mainForm.setWrite(messageTemporary);
      } else {
        soapRequestSuccessful = false;
        messageTemporary = "ERROR received token for REST request, status code " + response.code(); 
        message = message + ";" + messageTemporary;
        mainForm.setWrite(messageTemporary);
      }
    } catch (Exception e) {
      soapRequestSuccessful = false;
      messageTemporary = "ERROR send REST request "+ e.getMessage();
      message = message + ";" + messageTemporary;
      mainForm.setRequest(messageTemporary);
      token = "";
    }
    mainForm.setMqSender("Don't use");
    
    if (soapRequestSuccessful) LSLog.Info(message); else LSLog.Severe(message);
    LSLog.mainForms.add(mainForm);
    if (LSLog.mainForms.size() > LSLog.MAX_MAINFORMS_SHOW) LSLog.mainForms.remove(0);

    return soapRequestSuccessful;
  }

  /**
   * Формирует REST запрос к СВЛ ТР на получение данных.
   *  
   * @param requestId - id запроса (номер системы из файла configloadersvltr.xml) 
   * @param requestIntervalTimeMinute (requestIntervalTimeMinute - количество минут за которое запрашиваются данные)
   * @return true - при успешности получения данных, false - при неуспешности, null - при получении информации о недоступности
   * сервиса и необходимости направить запрос через определенный промежуток времени.
   * 
   */
  
  public synchronized Boolean exec(MRequest mRequest, int requestIntervalTimeMinute) {
    String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
    String messageTemporary;
    MainForm mainForm = new MainForm();
    mainForm.setDateCreate(new Date());
    mainForm.setName(mRequest.getName());
    mainForm.setDor("-");
    mainForm.setDepo("-");
    Boolean soapRequestSuccessful = true;
    String filePathRespond=null;

    if (!checkToken(mainForm, message)) return false;
    String[] subStrSegment = checkSegment(mainForm, message, mRequest.getUrlapi()); 
    if (subStrSegment.length != COUNT_SEGMENT) return false;

    OkHttpClient client = new OkHttpClient();
    
    Date dtStart=mRequest.getDataStartTime();
    Date dtEnd=DateUtils.addMinutes(dtStart, requestIntervalTimeMinute);
    
    HttpUrl httpUrl;
    if ((ASSIGMENT_DATA.equals(subStrSegment[2].toLowerCase())) | (VAGONS_DATA.equals(subStrSegment[2].toLowerCase()))) { 
      httpUrl = new HttpUrl.Builder()
          .scheme("https")
          .host(lsConfig.getConfig().getRestRequestParam().getUrl())
          .addPathSegment(subStrSegment[0])
          .addPathSegment(subStrSegment[1])
          .addPathSegment(subStrSegment[2])
          .build();
      
    } else {
      httpUrl = new HttpUrl.Builder()
          .scheme("https")
          .host(lsConfig.getConfig().getRestRequestParam().getUrl())
          .addPathSegment(subStrSegment[0])
          .addPathSegment(subStrSegment[1])
          .addPathSegment(subStrSegment[2])
          .addQueryParameter("dtStart", FORMAT_DATE_REST.format(dtStart))
          .addQueryParameter("dtEnd", FORMAT_DATE_REST.format(dtEnd))
          .build();
    }

    Request requesthttp = new Request.Builder()
        .url(httpUrl) 
        .addHeader("accept", "application/json")
        .addHeader("Authorization", "Bearer " + token)
        .build();

    Response response = null;
    try {
      response = client.newCall(requesthttp).execute();
      messageTemporary = "Successfully send REST request on date;" + LSLog.FORMAT_DATE.format(mRequest.getDataStartTime()); 
    } catch (Exception e) {
      soapRequestSuccessful = false;
      messageTemporary = "ERROR send REST request on date;" + LSLog.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
      ";" + e.getMessage();
    }
    message = message + ";" + messageTemporary;
    mainForm.setRequest(messageTemporary.replace(";", ": "));
    
    switch (response.code()) {
      case 200: 
        try {
          filePathRespond = lsData.writeSOAPrespond(mRequest, response.body().byteStream(), requestIntervalTimeMinute, 0, 0);
          messageTemporary = "Successfully write REST respond to file;" + filePathRespond; 
        } catch (Exception e) {
          soapRequestSuccessful = false;
          messageTemporary = "ERROR write SOAP respond " + e.getMessage();
        }
        message = message + ";" + messageTemporary;
        mainForm.setWrite(messageTemporary.replace(";", ": "));

        if (soapRequestSuccessful) {
          try {
            createMQSender(filePathRespond, mRequest, requestIntervalTimeMinute);
            messageTemporary = "Successfully create MQ Sender"; 
          } catch (Exception e) {
            soapRequestSuccessful = false;
            messageTemporary = "ERROR create MQ Sender " + e.getMessage();
          }
        }
        message = message + ";" + messageTemporary;
        mainForm.setMqSender(messageTemporary);

        intervalAndDateTime.replace("minInterval", 0);
        intervalAndDateTime.replace("currentDateTime", new Date());

        break;
      case 400:
        try {
          String bodyString = response.body().string();
          
          intervalAndDateTime.replace("minInterval", 
                Integer.parseInt(bodyString.substring(
                bodyString.indexOf("min interval:") + 13,  
                bodyString.indexOf(", current datetime:")))+5);
          
          intervalAndDateTime.replace("currentDateTime",
            FORMAT_DATE_ERROR_400.parse(bodyString.substring(
                bodyString.indexOf("current datetime:") + 17, 
                bodyString.indexOf("current datetime:") + 17 + FORMAT_DATE_ERROR_400.toPattern().length())));
          
          soapRequestSuccessful = null;
        } catch (Exception e) {
          // 
        }
        
        break;
      default:
        soapRequestSuccessful = false;
        messageTemporary = "ERROR received REST request, status code " + response.code(); 
        message = message + ";" + messageTemporary;
        mainForm.setWrite(messageTemporary);

        messageTemporary = "ERROR create MQ Sender";
        message = message + ";" + messageTemporary;
        mainForm.setMqSender(messageTemporary);
    }
    
    if (soapRequestSuccessful) LSLog.Info(message); else LSLog.Severe(message);
    LSLog.mainForms.add(mainForm);
    if (LSLog.mainForms.size() > LSLog.MAX_MAINFORMS_SHOW) LSLog.mainForms.remove(0);
    
    return soapRequestSuccessful;
  }

  
  private boolean checkToken(MainForm mainForm, String message) {
    String messageTemporary;
    boolean check = true;
    
    if ("".equals(token)) {
      messageTemporary = "ERROR token is empty";
      mainForm.setRequest(messageTemporary);
      mainForm.setWrite(messageTemporary);
      mainForm.setMqSender("Don't use");

      message = message + ";" + messageTemporary; 
      
      LSLog.Severe(message);
      LSLog.mainForms.add(mainForm);
      if (LSLog.mainForms.size() > LSLog.MAX_MAINFORMS_SHOW) LSLog.mainForms.remove(0);
      check = false;
    }
    
    return check;
  }

  private String[] checkSegment(MainForm mainForm, String message, String strSegment) {
    String messageTemporary;
    
    String[] subStrSegment = strSegment.split(DELIMETR);
    if (subStrSegment.length != COUNT_SEGMENT) {
      messageTemporary = "ERROR count segment in config file in teg urlapi";
      mainForm.setRequest(messageTemporary);
      mainForm.setWrite(messageTemporary);
      mainForm.setMqSender("Don't use");

      message = message + ";" + messageTemporary; 
      
      LSLog.Severe(message);
      LSLog.mainForms.add(mainForm);
      if (LSLog.mainForms.size() > LSLog.MAX_MAINFORMS_SHOW) LSLog.mainForms.remove(0);
    }
    
    return subStrSegment;
  }

  // Формирование MQ сообщения
  private void createMQSender(String filePathRespond, MRequest mRequest,  int requestIntervalTimeMinute) throws Exception {
    mqSender.setId(LSLog.SHORT_LOADERSVLTR_ID + mRequest.getId());
    mqSender.setDate1(new Timestamp(mRequest.getDataStartTime().getTime()));
    mqSender.setDate2(new Timestamp(DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime()));
    mqSender.setData_name(mRequest.getName());
    mqSender.setSystem_name(LSLog.SHORT_LOADERSVLTR_NAME);
    mqSender.setXML_name(filePathRespond);
    mqSender.send("");
  }

}
