package loader.svltr.restclients;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import loader.svltr.LoadersvltrApplication;
import loader.svltr.config.data.GeneralData;
import loader.svltr.config.data.LSData;
import loader.svltr.config.xml.LSConfig;
import loader.svltr.form.MainForm;
import loader.svltr.model.xml.MRequest;
import loader.svltr.mqsender.MQSender;
import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * REST Client SVLTR
 * 
 * Формирует REST запрос на получение данных от СВЛ ТР - это 
 * получение token необходимого для авторизации в REST запросах и сами запросы на получение данных:
 * 
 * COORD_SPEED_HISTORY - Скорость и географические координаты
 * ELECTRIC_DATA - Показания счетчиков тяги, рекуперации
 * TRAFFIC_LIGHT_HISTORY - Сигнал АЛСН
 * MEK_HISTORY - Данные МЭК
 * FUEL - Данные о топливе
 * DIESEL_RUN_HISTORY - Работа дизеля
 * ASSIGNMENT_DATA - Приписка локомотива
 * TRACTION_GENERATOR_HISTORY - Ток, напряжение, электроэнергия тягового генератора
 * TEMPERATURES_HISTORY - Температуры воды, масла дизеля, окружающего воздуха
 * CONTROLLER_POSITION_HISTORY - Позиция контроллера 
 * NSI_DEPOTS - Депо 
 * NSI_OPERATIONS - Операции АСОУП 
 * NSI_RAILWAYS - Дороги
 * NSI_STATES - Состояния АСОУП
 * NSI_STATIONS - Станции
 *
 * VAGONS_DATA - Данные по вагонам
 * ASOUP_DATA - История операций и состояний локомотивов
 * 
 * Полученный ответ записывает в файл и направляет путь к файлу Active MQ сообщением в Disparcher.
 * 
 */

@Data
public class RestClient {
  private String token;
  private static final String DELIMETR = "/"; 
  private static final int COUNT_SEGMENT = 3; 
  private static final String ASSIGMENT_DATA = "assignment_data";
  public static final String VAGONS_DATA = "vagons_data";
  private static final SimpleDateFormat FORMAT_DATE_REST = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  private static final SimpleDateFormat FORMAT_DATE_REST_VAGON = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private static final SimpleDateFormat FORMAT_DATE_ERROR_400 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  
  private HashMap<String, Object> intervalAndDateTime;  

  private LSConfig lsConfig;
  private LSData lsData;
  private MQSender mqSender;
  private String filePathRespond;
  private InputStream intupStreamResponseTrain;
  private int codeResponseTrain;

  public RestClient(LSConfig lsConfig, LSData lsData, String token) {
    intervalAndDateTime = new HashMap<String, Object>();
    intervalAndDateTime.put("minInterval", 0);
    intervalAndDateTime.put("currentDateTime", new Date());
    this.lsConfig=lsConfig;
    this.lsData=lsData;
    this.token=token;
    this.mqSender=new MQSender(lsConfig);
  }
  

  /**
   * Формирует REST запрос к СВЛ ТР на получение данных.
   *  
   * @param mRequest - ссылка на файл configloadersvltr.xml по настройке asoup_data 
   * @param requestIntervalTimeMinute (requestIntervalTimeMinute - количество минут за которое запрашиваются данные)
   * @return true - при успешности получения данных, false - при неуспешности, null - при получении информации о недоступности
   * сервиса и необходимости направить запрос через определенный промежуток времени.
   * 
   */
  
  public Boolean exec(MRequest mRequest, int requestIntervalTimeMinute, String operDt, String trainIndex) {
    String message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
    String messageTemporary;
    MainForm mainForm = new MainForm();
    mainForm.setDateCreate(new Date());
    mainForm.setName(mRequest.getName());
    mainForm.setDor("-");
    mainForm.setDepo("-");
    Boolean soapRequestSuccessful = true;

    if (!checkToken(mainForm, message)) return false;
    String[] subStrSegment = checkSegment(mainForm, message, mRequest.getUrlapi()); 
    if (subStrSegment.length != COUNT_SEGMENT) return false;

    OkHttpClient client = new OkHttpClient();
    
    Date dtStart=mRequest.getDataStartTime();
    Date dtEnd=DateUtils.addMinutes(dtStart, requestIntervalTimeMinute);
    
    HttpUrl httpUrl;
    if (ASSIGMENT_DATA.equals(subStrSegment[2].toLowerCase())) { 
      httpUrl = new HttpUrl.Builder()
          .scheme("http")
          .host(lsConfig.getConfig().getRestRequestParam().getUrl())
          .addPathSegment(subStrSegment[0])
          .addPathSegment(subStrSegment[1])
          .addPathSegment(subStrSegment[2])
          .build();
    } else if (VAGONS_DATA.equals(subStrSegment[2].toLowerCase())) {
        httpUrl = new HttpUrl.Builder()
            .scheme("http")
            .host(lsConfig.getConfig().getRestRequestParam().getUrl())
            .addPathSegment(subStrSegment[0])
            .addPathSegment(subStrSegment[1])
            .addPathSegment(subStrSegment[2])
            .addQueryParameter("train_index", trainIndex)
            .addQueryParameter("oper_dt", operDt)
            .build();
    } else {
      httpUrl = new HttpUrl.Builder()
          .scheme("http")
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
      messageTemporary = "Successfully send REST request on date;" + GeneralData.FORMAT_DATE.format(mRequest.getDataStartTime()); 
    } catch (Exception e) {
      soapRequestSuccessful = false;
      messageTemporary = "ERROR send REST request on date;" + GeneralData.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
      ";" + e.getMessage();
    }
    message = message + ";" + messageTemporary;
    mainForm.setRequest(messageTemporary.replace(";", ": "));
    
    if (trainIndex == null) {
      switch (response.code()) {
        case 200: 
          try {
            filePathRespond = lsData.writeSOAPrespond(mRequest, response.body().byteStream(), requestIntervalTimeMinute, 0, 0);
            messageTemporary = "Successfully write REST respond to file;" + filePathRespond; 
            soapRequestSuccessful = true;
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
              (int)Integer.parseInt(
                bodyString.substring(bodyString.indexOf("min interval:") + 13, 
                bodyString.indexOf(", current datetime:")))+5);

            intervalAndDateTime.replace("currentDateTime",
              FORMAT_DATE_ERROR_400.parse(bodyString.substring(
                  bodyString.indexOf("current datetime:") + 17, 
                  bodyString.indexOf("current datetime:") + 17 + FORMAT_DATE_ERROR_400.toPattern().length())));
          
            messageTemporary = "ERROR in getting a response: " + bodyString;
          } catch (Exception e) {
            messageTemporary = "ERROR in getting a response: " + e.getMessage();
          }
          message = message + ";" + messageTemporary;
          mainForm.setMqSender(messageTemporary);
          soapRequestSuccessful = false;
        
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
    
      if (soapRequestSuccessful) LoadersvltrApplication.logger.fatal(message); else LoadersvltrApplication.logger.fatal(message);
      GeneralData.mainForms.add(mainForm);
      if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
      
    } else {
      this.codeResponseTrain = response.code(); 
      try {
        this.intupStreamResponseTrain = response.body().byteStream();
      } catch (IOException e) {
        //
      }
    }
      
    
    return soapRequestSuccessful;
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
      
      LoadersvltrApplication.logger.fatal(message);
      GeneralData.mainForms.add(mainForm);
      if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
    }
    
    return subStrSegment;
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
      
      LoadersvltrApplication.logger.fatal(message);
      GeneralData.mainForms.add(mainForm);
      if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
      check = false;
    }
    
    return check;
  }


  // Формирование MQ сообщения
  private void createMQSender(String filePathRespond, MRequest mRequest,  int requestIntervalTimeMinute) throws Exception {
    mqSender.setId(mRequest.getId());
    mqSender.setDate1(new Timestamp(mRequest.getDataStartTime().getTime()));
    mqSender.setDate2(new Timestamp(DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute).getTime()));
    mqSender.setData_name(mRequest.getName());
    mqSender.setSystem_name(GeneralData.SHORT_LOADERSVLTR_NAME);
    mqSender.setXML_name("/" + filePathRespond.replace("\\", "/"));
    mqSender.send("");
  }

  /**
   * Формирует REST запрос к СВЛ ТР на получение данных по сервису vagons_data.
   *  
   * @param mRequest - ссылка на файл configloadersvltr.xml по настройке asoup_data 
   * @param mRequestVagonData - ссылка на файл configloadersvltr.xml по настройке vagons_data 
   * @param requestIntervalTimeMinute (requestIntervalTimeMinute - количество минут за которое запрашиваются данные)
   * @return true - при успешности получения данных, false - при неуспешности, null - при получении информации о недоступности
   * сервиса и необходимости направить запрос через определенный промежуток времени.
   * 
   */
  public Boolean execVagonsData(MRequest mRequest, MRequest mRequestVagonData, int requestIntervalTimeMinute) {
    MainForm mainForm = new MainForm();
    String message = "ID request-" + mRequestVagonData.getId() + ";Name request-" + mRequestVagonData.getName();
    Long operId;
    String operDt;
    String trainIndex;
    JSONArray jsonArrayTrain;
    JSONArray jsonArrayVagon;
    JSONArray jsonArrayAllVagons = new JSONArray();
    boolean soapRequestSuccessful = true;
    
    // Распарсиваем файл json полученный от сервиса ASOUP_DATA
    jsonArrayTrain = new JSONArray();
    try {
      JSONParser jsonParser = new JSONParser();
      jsonArrayTrain = (JSONArray) jsonParser.parse(new FileReader(this.filePathRespond));
    } catch (Exception e) {
      //
    }

    for (Object objectTrain : jsonArrayTrain) {
      try {
        JSONObject jsonTrainObject = (JSONObject) objectTrain;
        operId = (Long) jsonTrainObject.get("oper_id");
        operDt = (String) jsonTrainObject.get("oper_dt");
        trainIndex = (String) jsonTrainObject.get("train_index");
//trainIndex="841402662592204";
//operDt="2017-02-06T08:58:00";            
      } catch (Exception e) {
        message = message + ";ERROR get fields oper_id, oper_dt, train_index in asoup_data respond, file: "+this.filePathRespond;
        mainForm.setRequest(message);
        mainForm.setWrite(message);
        mainForm.setMqSender("Don't use");
        LoadersvltrApplication.logger.fatal(message);
        GeneralData.mainForms.add(mainForm);
        if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
        return false;
      }

      // Получаем информацию по вагонам только по операциям отправления поезда
      if (Arrays.asList(new Long[] {5L, 6L, 7L, 8L, 18L, 26L}).contains(operId)) {
        soapRequestSuccessful = this.exec(mRequestVagonData, requestIntervalTimeMinute, operDt, trainIndex);
        if (soapRequestSuccessful==false) break;
  
        // Распарсиваем файл json полученный от сервиса VAGONS_DATA
        jsonArrayVagon = new JSONArray();
        try {
          JSONParser jsonParser = new JSONParser();
          
          jsonArrayVagon = (JSONArray) jsonParser.parse(new BufferedReader(new InputStreamReader(this.intupStreamResponseTrain))
              .lines().collect(Collectors.joining("\n")));
          jsonArrayAllVagons.addAll(jsonArrayVagon);
        } catch (Exception e) {
          //
        }
      }
    }

    try {
      filePathRespond = lsData.writeSOAPrespondVagons(mRequest, mRequestVagonData, jsonArrayAllVagons, requestIntervalTimeMinute, 0, 0);
      message = message + ";Successfully send REST request on date;" + GeneralData.FORMAT_DATE.format(mRequestVagonData.getDataStartTime()) + ";" + 
        "Successfully write REST respond to file;" + filePathRespond; 
      
    } catch (Exception e) {
      soapRequestSuccessful = false;
      message = message + ";ERROR send REST request on date;" + GeneralData.FORMAT_DATE.format(mRequestVagonData.getDataStartTime()) + ";" + 
          "ERROR write REST respond to file;" + e.getMessage();
    }

    if (soapRequestSuccessful) {
      try {
        createMQSender(filePathRespond, mRequestVagonData, requestIntervalTimeMinute);
        message = message + ";Successfully create MQ Sender"; 
      } catch (Exception e) {
        soapRequestSuccessful = false;
        message = message + ";ERROR create MQ Sender " + e.getMessage();
      }
    }
    mainForm.setRequest(message);
    mainForm.setWrite(message);
    mainForm.setMqSender("Don't use");
    LoadersvltrApplication.logger.fatal(message);
    GeneralData.mainForms.add(mainForm);
    if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);

    return soapRequestSuccessful;
  }
  
  
}

