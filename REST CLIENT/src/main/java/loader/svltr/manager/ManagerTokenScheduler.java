package loader.svltr.manager;

import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import loader.svltr.LoadersvltrApplication;
import loader.svltr.config.data.GeneralData;
import loader.svltr.config.xml.LSConfig;
import loader.svltr.form.MainForm;
import lombok.Data;

@EnableScheduling
@Service
@Data
public class ManagerTokenScheduler {
  private static final int FIXED_DELAY = 23 * 60 *60 * 1000 + 15 * 60 * 1000;
  private static final String DELIMETR = "/"; 
  private static final int COUNT_SEGMENT = 3; 

  @Autowired
  private LSConfig lsConfig;
  
  public String token;

  /** 
   * Планировщик каждые 23 часа и 45 минут запрашивает новый token,
   * так как срок его действия 24 часа.
   * 
   */
  
  @Scheduled(initialDelay=0, fixedDelay = FIXED_DELAY)
  public void scheduleToken() {
    getMyToken();
  }  

  /**
   * Формирует REST запрос на получение token от СВЛ ТР
   * @return возвращает true при успешности получения token и false при неуспешности.
   * 
   */ 
  
  public boolean getMyToken() {
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
        .scheme("http") 
        .host(lsConfig.getConfig().getRestRequestParam().getUrl())
        .addPathSegment(subStrSegment[0]) 
        .addPathSegment(subStrSegment[1])
        .addPathSegment(subStrSegment[2])
        .build();
    Request requesthttp = new Request.Builder()
        .url(httpUrl) 
        .addHeader("password", lsConfig.getConfig().getRestRequestParam().getPassword())
        .addHeader("username", lsConfig.getConfig().getRestRequestParam().getUsername())
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
    
    if (soapRequestSuccessful) LoadersvltrApplication.logger.fatal(message); else LoadersvltrApplication.logger.fatal(message);
    GeneralData.mainForms.add(mainForm);
    if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);

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

}
