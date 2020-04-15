package loader.elbrus.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.commons.lang3.time.DateUtils;
import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
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
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.form.MainForm;
import loader.elbrus.log.LALog;
import loader.elbrus.log.LBData;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.mqsender.MQSender;
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

@Service
public class ActiveMQGraphsAvailable {

  @Autowired
  private LBData lbData; 

  @Autowired
  XMLGraphsAvailable xmlGraphsAvailable;

  @Autowired
  private LAConfig laConfig;
  
  @Autowired
  private MQSender mqSender;

  public boolean onMessageRequest;
  public boolean onMessageRequestWrite;
  public List<TimetableInfoMessage> timetableInfosMessageList = new ArrayList<TimetableInfoMessage>();
  public String message;
  public String messageTemporary;
  public MainForm mainForm;
  public String filePathRespond;
  
  public ActiveMQGraphsAvailable() {
    //
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
  
  public synchronized List<TimetableInfoMessage> sendMessage(MRequest mRequest, int requestIntervalTimeMinute, 
      Date dateStart, Date dateFinish) throws Exception {
    onMessageRequest = false;
    mainForm = new MainForm();
    mainForm.setDateCreate(new Date());
    timetableInfosMessageList.clear();
    message = "ID request-" + mRequest.getId() + ";Name request-" + mRequest.getName();
    mainForm.setName(message.replaceAll(";", ", "));
    messageTemporary = "";
    filePathRespond = "";

    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(laConfig.getConfig().getActiveMQRequestParam().getUrl());    
    Connection connection = connectionFactory.createConnection();
    connection.start();

    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    Destination destRequest = session.createQueue(laConfig.getConfig().getActiveMQRequestParam().getQueueName());
    Destination destResponce = session.createTemporaryQueue();

    MessageProducer producer = session.createProducer(destRequest);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    MessageConsumer consumer = session.createConsumer(destResponce);

    consumer.setMessageListener(new MessageListener() {
      @Override
      public void onMessage(Message bytesMessage) {
        try {
          ByteArrayInputStream inputStream = new ByteArrayInputStream(((ActiveMQBytesMessage) bytesMessage).getContent().getData());
          TimetableInfosMessage timetableInfosMessage = TimetableInfosMessage.parseFrom(inputStream);

          // Формируем XML документ
          Document document = xmlGraphsAvailable.createXMLFile(timetableInfosMessage, mRequest, dateStart, dateFinish);
          
          // Выводим документ в файл
          filePathRespond = lbData.writeXMLFile(mRequest, document, requestIntervalTimeMinute, 0, 0, "");
          messageTemporary = "Successfully create XML file and write respond to file:" + filePathRespond;
          timetableInfosMessageList.addAll(timetableInfosMessage.getTimetableInfosList());
          onMessageRequestWrite = true;
        } catch (Exception e) {
          messageTemporary = "ERROR create XML file and write respond to file:" + e.getMessage();
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
    
    messageTemporary = "Successfully sent ActiveMQ request on date:" + LALog.FORMAT_DATE.format(mRequest.getDataStartTime()); 
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
    if (onMessageRequest) {
      messageTemporary = "Successfully recive ActiveMQ" + messageTemporary; 
    } else {
      messageTemporary = "ERROR recive ActiveMQ request - timeout > " + mRequest.getTimeout()*60*1000; 
    }
    message = message + ";" + messageTemporary;
    mainForm.setWrite(messageTemporary);

    onMessageRequest = onMessageRequestWrite;

    mainForm.setMqSender("Don't use");

    LALog.mainForms.add(mainForm);
    if (onMessageRequest) LALog.Info(message); else LALog.Severe(message);
    if (LALog.mainForms.size() > LALog.MAX_MAINFORMS_SHOW) LALog.mainForms.remove(0);
    
    return timetableInfosMessageList;
  }

}
