package loader.elbrus.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.jdom.Document;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import loader.elbrus.LoaderelbrusApplication;
import loader.elbrus.config.data.GeneralData;
import loader.elbrus.config.data.LBData;
import loader.elbrus.form.MainForm;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.proto.ElbrusProto.GetTimetableInfosRequestMessage;
import loader.elbrus.proto.ElbrusProto.TimetableInfoMessage;
import loader.elbrus.proto.ElbrusProto.TimetableInfosMessage;
import loader.elbrus.proto.ElbrusProto.TimetableTurMessage;
import loader.elbrus.xml.XMLGraphsAvailable;

/**
 * ActiveMQGraphsAvailable
 * Получение информации от Эльбрус о наличии графиков движения поездов
 * Создает подключение к ActiveMQ, ждет ответ, распарсивает ответ в класс полученный из файла protobuff (google),
 * формирует XML - экземпляр класса Document, записывает полученный XML файл на диск (СХД),
 * направлет сообщение диспатчеру с путем гре расположен полученный файл. 
 *
 */

public class ActiveMQGraphsAvailable {

  private LBData lbData;
  
  public boolean onMessageRequest;
  public boolean onMessageRequestWrite;
  public List<TimetableInfoMessage> timetableInfosMessageList = new ArrayList<TimetableInfoMessage>();
  public String message;
  public String messageTemporary;
  public MainForm mainForm;
  public String filePathRespond;
  
  public ActiveMQGraphsAvailable(LBData lbData) {
    this.lbData=lbData;
  }
  
  /**
   * sendMessage
   * Направление сообщения - запроса в ActiveMQ Эльбрус для получения данных
   * @param mRequest - элемент данных Request из файла configloaderelbrus.xml по которому формировалься запрос 
   * @param requestIntervalTimeMinute - интервал времени в минутах за который получены данные
   * @param dateStart - дата с которой получены данные
   * @param dateFinish - дата до которой получены данные
   * @return возвращает список с элементами класса TimetableInfoMessage (protobuff) 
   * @throws Exception - все возможные ошибки
   */
  
  public List<TimetableInfoMessage> sendMessage(MRequest mRequest, int codeRoad, int requestIntervalTimeMinute, 
      Date dateStart, Date dateFinish, String url, String queueName) throws Exception {
    onMessageRequest = false;
    mainForm = new MainForm();
    mainForm.setDateCreate(new Date());
    timetableInfosMessageList.clear();
    message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
    mainForm.setName(message.replaceAll(";", ", "));
    messageTemporary = "";
    filePathRespond = "";

    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);    
    Connection connection = connectionFactory.createConnection();
    connection.start();

    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    Destination destRequest = session.createQueue(queueName);
    Destination destResponce = session.createTemporaryQueue();

    MessageProducer producer = session.createProducer(destRequest);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    MessageConsumer consumer = session.createConsumer(destResponce);

    consumer.setMessageListener(new MessageListener() {
      @Override
      public void onMessage(Message bytesMessage) {
        try {
          messageTemporary = GeneralData.FORMAT_DATE.format(new Date()) + " Successfully recive ActiveMQ;"; 

          ByteArrayInputStream inputStream = new ByteArrayInputStream(((ActiveMQBytesMessage) bytesMessage).getContent().getData());
          TimetableInfosMessage timetableInfosMessage = TimetableInfosMessage.parseFrom(inputStream);

          // Формируем XML документ
          XMLGraphsAvailable xmlGraphsAvailable = new XMLGraphsAvailable();
          Document document = xmlGraphsAvailable.createXMLFile(timetableInfosMessage, mRequest, dateStart, dateFinish, url);
          
          // Выводим документ в файл
          filePathRespond = lbData.writeXMLFile(mRequest, document, requestIntervalTimeMinute, codeRoad, dateStart, 0, "");
          messageTemporary =  messageTemporary + "Successfully create XML file and write respond to file:" + filePathRespond;
          timetableInfosMessageList.addAll(timetableInfosMessage.getTimetableInfosList());
          onMessageRequestWrite = true;
        } catch (Exception e) {
          messageTemporary = messageTemporary + "ERROR create XML file and write respond to file:" + e.getMessage();
          onMessageRequestWrite = false;
        }

        onMessageRequest = true;
      }
    });

    BytesMessage bytesMessage = (BytesMessage) session.createBytesMessage();
    bytesMessage.setJMSReplyTo(destResponce);
    bytesMessage.setStringProperty("emq:message_operation", "get_timetable_infos");
    bytesMessage.setStringProperty("emq:message_type", "protobuf");
    bytesMessage.setStringProperty("emq:status", "ok");
    bytesMessage.setStringProperty("emq:proto_type", "elbrus_get_timetable_infos_request");
    bytesMessage.setStringProperty("emq:message_operation_part", "request");
    
    bytesMessage.writeBytes(
    GetTimetableInfosRequestMessage.newBuilder()
    .setTimetableTur(TimetableTurMessage.newBuilder()
        .setType(mRequest.getTypeParam())
        .setUser(mRequest.getUserParam())
        .build()
    ).build().toByteArray());

    bytesMessage.setJMSTimestamp(new Date().getTime());
    if (mRequest.getTimeout()*60*1000 > 0) {
      bytesMessage.setJMSExpiration(System.currentTimeMillis() + mRequest.getTimeout()*60*1000);
    } else {
      bytesMessage.setJMSExpiration(0);
    }
    
    producer.send(bytesMessage);
    
    messageTemporary = GeneralData.FORMAT_DATE.format(new Date()) + " Successfully sent ActiveMQ request on date:" + GeneralData.FORMAT_DATE.format(dateStart); 
    message = message + ";" + messageTemporary;
    mainForm.setRequest(messageTemporary);
    messageTemporary = "";
    
    while ((bytesMessage.getJMSTimestamp() + mRequest.getTimeout()*60*1000 > System.currentTimeMillis())&(!onMessageRequest)) {
      Thread.sleep(60*1000);
    };
    
    producer.close();
    consumer.close();
    session.close();
    connection.close();

    // Добавление в log-файл  информации о получении ответа из ActiveMQ данных  
    if (!onMessageRequest) {
      messageTemporary = GeneralData.FORMAT_DATE.format(new Date()) + " ERROR recive ActiveMQ request - timeout > " + mRequest.getTimeout()*60*1000; 
    }
    message = message + ";" + messageTemporary;
    mainForm.setWrite(messageTemporary);

    onMessageRequest = onMessageRequestWrite;

    mainForm.setMqSender("Don't use");

    GeneralData.mainForms.add(mainForm);
    if (onMessageRequest) LoaderelbrusApplication.logger.fatal(message); else LoaderelbrusApplication.logger.fatal(message);
    if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);
    
    return timetableInfosMessageList;
  }

}
