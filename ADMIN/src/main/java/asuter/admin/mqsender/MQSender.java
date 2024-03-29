package asuter.admin.mqsender;

import java.sql.Timestamp;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import asuter.admin.config.session.SessionConfig;
import lombok.Data; 

/**
 * MQSender
 * 
 * Предназначен для создания MQ сообщения для MQ Dispacher-а об успешности полученных 
 * по SOAP запросу данных.
 * В MS сообщении передается путь и имя файла с полученным SOAP-ответом.
 */

@Data
@Service
public class MQSender {
  private String user;
  private String password;
  private String host;
  private int port;
  private String queue = "";
  private StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
  private Connection connection = null;
  private Session session;
  private Destination dest;
  private MessageProducer producer;

  private int id = 0;
  private int system_id = 0;
  private String system_name = "";
  private Timestamp data_id = new Timestamp(0);
  private String data_name = "";
  private String XML_name = "";
  private Timestamp date1 = new Timestamp(0);
  private Timestamp date2 = new Timestamp(0);
  private Timestamp current_timestamp = new Timestamp(System.currentTimeMillis());
  private boolean cut = false;
  
  @Autowired
  private SessionConfig sessionConfig;
  
  public void initMQSender(String code_module) throws Exception {
    this.user = sessionConfig.getSett(code_module, "MQSenderParam.login");   
    this.password = sessionConfig.getSett(code_module, "MQSenderParam.password");
    this.host = sessionConfig.getSett(code_module, "MQSenderParam.host");
    this.port = Integer.parseInt(sessionConfig.getSett(code_module, "MQSenderParam.port"));
    this.queue = sessionConfig.getSett(code_module, "MQSenderParam.name");
    
    factory = new StompJmsConnectionFactory();
    factory.setBrokerURI("tcp://" + host + ":" + port);

    connection = factory.createConnection(user, password);
    connection.start();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    dest = new StompJmsDestination(queue);
    producer = session.createProducer(dest);
    producer.setDeliveryMode(DeliveryMode.PERSISTENT);
  }

  /**
   * send - направляет сообщение в очередь 
   * @param body_txt - текст сообщения с информацией о нахождении файла с данными
   * @throws Exception - любые ошибки возникающие при направлении сообщения
   */

  public void send(String body_txt, String code_module) throws Exception {
    this.initMQSender(code_module);
    
    TextMessage msg = session.createTextMessage(body_txt);
    msg.setIntProperty("id", id); // id сообщения
    msg.setIntProperty("system_id", system_id); // id системы
    msg.setStringProperty("system_name", system_name); // наименование системы
    msg.setLongProperty("data_id", data_id.getTime()); // id типа данных
    msg.setStringProperty("data_name", data_name); // назавание данных
    msg.setStringProperty("XML_name", XML_name); // назавание XML-файла
    msg.setLongProperty("date1", date1.getTime()); // дата/время начала периода данных
    msg.setLongProperty("date2", date2.getTime()); // дата/время конца периода данных
    msg.setLongProperty("current_timestamp", current_timestamp.getTime()); // дата/время текущее
    msg.setBooleanProperty("cut", cut);
    msg.setIntProperty("part", 0); //часть сообщения (0 - целый файл, >0 - номер куска файла) сообщения
    producer.send(msg);

    connection.close();
  }

}
