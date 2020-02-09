package loader.comm.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import loader.comm.config.LCConfig;
import loader.comm.log.LCLog;
import loader.comm.xml.ConfigurationCOMM;
import loader.comm.xml.MRequest;

public class SOAPClient {
  private final String METHOD = "POST";
  private LCConfig lcConfig;
  private String request;
  private String namespaceURI;
  private String soapUrl;
  private String serviceName;
  private String shortMSName;
  private Integer requestIntervalTimeMinute;

  private String soapAction;

  public SOAPClient(LCConfig lcConfig, MRequest mRequest, String request, String shortMSName, Integer requestIntervalTimeMinute) {
    this.lcConfig = lcConfig;
    this.namespaceURI = mRequest.getNamespaceURI();
    this.soapUrl = mRequest.getUrl();
    this.serviceName = mRequest.getServiceName();
    this.request = request;
    this.shortMSName = shortMSName;
    this.requestIntervalTimeMinute = requestIntervalTimeMinute;
    soapAction = mRequest.getNamespaceURI() + "/" + mRequest.getServiceName();
  }

  public boolean createSOAPRequest(ConfigurationCOMM config, MRequest mRequest) {
    URL url = null;
    try {
      url = new URL(soapUrl);
    } catch (MalformedURLException e) {
      LCLog.Severe("ERROR create soapUrl - " + soapUrl + " " + e.getMessage());
    }

    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod(METHOD);
    } catch (IOException e) {
      LCLog.Severe("ERROR create connection " + e.getMessage());
    }

    connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
    connection.setRequestProperty("Content-Length", String.valueOf(request.length()));
    connection.setRequestProperty("SoapAction", soapAction);
    connection.setDoOutput(true);

    PrintWriter pw = null;
    try {
      pw = new PrintWriter(connection.getOutputStream());
    } catch (IOException e) {
      LCLog.Severe("ERROR write request " + e.getMessage());
    }
    pw.write(request);
    pw.flush();

    try {
      connection.connect();
    } catch (IOException e) {
      LCLog.Severe("ERROR connection " + e.getMessage());
    }

    BufferedReader rd = null;
    Date dateRespond = null;
    try {
      rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      dateRespond = new Date();
    } catch (IOException e) {
      LCLog.Severe("ERROR create request " + mRequest.getId()+"::"+
          mRequest.getName() +  
          "::soap request on date::" +
          LCLog.FORMAT_DATE.format(mRequest.getDataStartTime()) + "::" +
          e.getMessage());
      return false;
    }

    String line;
    String respond = "";

    try {
      respond = rd.readLine();
      while ((line = rd.readLine()) != null)
        respond = line;
    } catch (IOException e) {
      LCLog.Severe("ERROR create respond " + e.getMessage());
    }

    try {
      var filePathRespond = createRespondFile(config, mRequest, respond);
      System.out.println(filePathRespond);
      LCLog.Info(mRequest.getId()+"::"+
          mRequest.getName() +  
          "::Successfully sent soap request on date::" +
          LCLog.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
          "::Successfully sent soap respond on date::" +
          LCLog.FORMAT_DATE.format(dateRespond) + 
          "::file soap respond write on path::" + filePathRespond);
    
    } catch (IOException e) {
      LCLog.Info(mRequest.getId()+"::"+
          mRequest.getName() +  
          "::Successfully sent soap request on date::" +
          LCLog.FORMAT_DATE.format(mRequest.getDataStartTime()) + 
          "::Successfully sent soap respond on date::" +
          LCLog.FORMAT_DATE.format(dateRespond) + 
          "::ERROR file soap respond write on out path");
    }
    return true;
  }

  private String createRespondFile(ConfigurationCOMM config, MRequest mRequest, String respond) throws IOException {
    var path = LCLog.FORMAT_DATE_FILE_YYYYMMDD.format(mRequest.getDataStartTime());
    
    path = lcConfig.getConfig().getLogAndDataServer().getName() +  
        mRequest.getDestPath() + 
        path + "/" + LCLog.ADD_LOADERCOMM_PATH + "/";    

    var theDir = new File(path);

    if (!theDir.exists()) theDir.mkdirs();
    
    var fileName = this.shortMSName + "_" + 
          LCLog.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(mRequest.getDataStartTime()) + "_" +
          LCLog.FORMAT_DATE_FILE_YYYYMMDDHHMM.format(DateUtils.addMinutes(mRequest.getDataStartTime(), requestIntervalTimeMinute)) + ".txt";
    
    File file = new File(path + fileName);
    file.createNewFile();
    write(file, respond);
    return file.getPath(); 
  }

private void write(File file, String message) {
    try (var stream = new FileOutputStream(file, true)) {
      byte[] buffer = message.getBytes();
      stream.write(buffer, 0, buffer.length);
    } catch (IOException e) {
      LCLog.Severe("ERROR write file respond data " + e.getMessage());
    }
}

}
