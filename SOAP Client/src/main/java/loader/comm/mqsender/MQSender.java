package loader.comm.mqsender;

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
import loader.comm.config.xml.LSConfig;
import lombok.Data;

/**
 * MQSender
 * 
 * Предназначен для создания MQ сообщения для MQ Dispacher-а об успешности полученных 
 * по SOAP запросу данных.
 * В MS сообщении передается путь и имя файла с полученным SOAP-ответом.
 */

@Data
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
  private int codeRoad = 0;
  private int system_id = 1; // COMM
  private String system_name = "";
  private Timestamp data_id = new Timestamp(0);
  private String data_name = "";
  private String XML_name = "";
  private Timestamp date1 = new Timestamp(0);
  private Timestamp date2 = new Timestamp(0);
  private Timestamp current_timestamp = new Timestamp(System.currentTimeMillis());
  private boolean cut = false;
  
  private LSConfig lsConfig;
  
  public MQSender(LSConfig lsConfig) {
    this.lsConfig = lsConfig;
  }

  public void initMQSender() throws JMSException {
    this.user = lsConfig.getConfig().getMQSenderParam().getLogin();
    this.password = lsConfig.getConfig().getMQSenderParam().getPassword();
    this.host = lsConfig.getConfig().getMQSenderParam().getHost();
    this.port = lsConfig.getConfig().getMQSenderParam().getPort();
    this.queue = lsConfig.getConfig().getMQSenderParam().getName();
    
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

  public void send(String body_txt) throws Exception {
    this.initMQSender();
    
    TextMessage msg = session.createTextMessage(body_txt);
    msg.setIntProperty("id", 0); // id сообщения
    msg.setIntProperty("request_id", id); // id запроса к системе
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
    msg.setIntProperty("coderoad", codeRoad); //Код дороги 
    producer.send(msg);

    connection.close();
  }

}
