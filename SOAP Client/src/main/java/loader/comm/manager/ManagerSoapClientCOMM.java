package loader.comm.manager;

import loader.comm.config.LCConfig;
import loader.comm.log.LCLog;
import loader.comm.soap.SOAPClient;
import loader.comm.xml.MRequest;

public class ManagerSoapClientCOMM {
  public final String SHORT_MS_NAME = "MS";
  private Integer requestId;
  private Integer requestIntervalTimeMinute;
  private LCConfig lcConfig;
  private MRequest mRequest;
  
  public ManagerSoapClientCOMM(Integer requestId, Integer requestIntervalTimeMinute, LCConfig lcConfig) {
    this.requestId = requestId;
    this.requestIntervalTimeMinute = requestIntervalTimeMinute;
    this.lcConfig = lcConfig;
    this.mRequest = lcConfig.getConfig().getRequest().getRequests().stream().filter(item -> item.getId() == requestId).findFirst().orElse(null); 
    exec();
  }
  
  private void execSoapClient() {
    var onDate = LCLog.FORMAT_DATE_SOAP.format(mRequest.getDataStartTime());
    var request = "<?xml version=\"1.0\"?> "
        + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        + "xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\"> " + "<soapenv:Header/> "
        + "<soapenv:Body> " + "<urn:Z63L_ASORT_GETOPER> " + "<CDEPO>10</CDEPO> "
        + "<CDOR>83</CDOR> " + "<MDATE>" + onDate + "</MDATE> " + "</urn:Z63L_AS_ER_GETOPER> "
        + "</soapenv:Body> " + "</soapenv:Envelope>";
    
    

    var systemOne = new SOAPClient(lcConfig, mRequest, request, SHORT_MS_NAME, requestIntervalTimeMinute);
    systemOne.createSOAPRequest(lcConfig.getConfig(), mRequest);
  }

  public void exec() {

    switch (this.requestId) {
      case 1:
        execSoapClient();
        break;
    }
  lcConfig.addDataStartTime(mRequest, requestIntervalTimeMinute);
  lcConfig.write();
  }

}
