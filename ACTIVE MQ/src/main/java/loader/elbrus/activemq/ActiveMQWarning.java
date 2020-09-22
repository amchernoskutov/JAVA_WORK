package loader.elbrus.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.commons.lang3.time.DateUtils;
import org.jdom.Document;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import loader.elbrus.config.xml.LAConfig;
import loader.elbrus.form.MainForm;
import loader.elbrus.model.xml.MRequest;
import loader.elbrus.mqsender.MQSender;
import loader.elbrus.proto.CommonProto.TimeStringMessage;
import loader.elbrus.proto.ElbrusProto.GetTimetableRequestMessage;
import loader.elbrus.proto.ElbrusProto.TimetableTurMessage;
import loader.elbrus.proto.ElbrusProto.TimetableWarnsMessage;
import loader.elbrus.xml.XMLWarning;

/**
 * ActiveMQWarning
 * Получение информации от Эльбрус информации об ограничении скорости движения поездов
 * Создает подключение к ActiveMQ, ждет ответ, распарсивает ответ в класс полученный из файла protobuff (google),
 * формирует XML - экземпляр класса Document, записывает полученный XML файл на диск (СХД),
 * направлет сообщение диспатчеру с путем гре расположен полученный файл. 
 *
 */

public class ActiveMQWarning {
  public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  public static final SimpleDateFormat FORMAT_DATE_MARSHRUT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  
  private LBData lbData; 
  private LAConfig laConfig;

  public boolean onMessageRequest;
  public boolean onMessageRequestWrite;
  public String message;
  public String messageTemporary;
  public MainForm mainForm;
  public String filePathRespond;

  public ActiveMQWarning(LAConfig laConfig, LBData lbData) {
    this.laConfig=laConfig;
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
  
  public boolean sendMessage(MRequest mRequest, int codeRoad, int requestIntervalTimeMinute,
      Date dateStart, Date dateFinish, String url, String queueName) throws Exception {
    onMessageRequest = false;
    onMessageRequestWrite = false;
    mainForm = new MainForm();
    mainForm.setDateCreate(new Date());
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
          TimetableWarnsMessage timetableWarnsMessage = TimetableWarnsMessage.parseFrom(inputStream);

          // Формируем XML документ
          XMLWarning xmlWarning = new XMLWarning();
          Document document = xmlWarning.createXMLFile(timetableWarnsMessage, mRequest, dateStart, dateFinish, url);

          // Выводим документ в файл
          filePathRespond = lbData.writeXMLFile(mRequest, document, requestIntervalTimeMinute, codeRoad, dateStart, 0, "");
          messageTemporary = messageTemporary + " Successfully create XML file and write respond to file:" + filePathRespond;
          onMessageRequestWrite = true;
        } catch (Exception e) {
          messageTemporary = "ERROR create XML file and write respond to file:" + e.getMessage();
          onMessageRequestWrite = false;
        }

        onMessageRequest = true;
      }
    });

    ActiveMQBytesMessage bytesMessage = (ActiveMQBytesMessage) session.createBytesMessage();
    bytesMessage.setReplyTo((ActiveMQDestination) destResponce);
    bytesMessage.setStringProperty("emq:message_operation", "get_timetable_warns");
    bytesMessage.setStringProperty("emq:message_type", "protobuf");
    bytesMessage.setStringProperty("emq:status", "ok");
    bytesMessage.setStringProperty("emq:proto_type", "elbrus_get_timetable_request");
    bytesMessage.setStringProperty("emq:message_operation_part", "request");

    bytesMessage.writeBytes(GetTimetableRequestMessage.newBuilder()
        .setTimetableTur(TimetableTurMessage.newBuilder()
            .setType(mRequest.getTypeParam())
            .setUser(mRequest.getUserParam())
            )
        .setTimeFrom(TimeStringMessage.newBuilder()
            .setTimeValue(FORMAT_DATE_MARSHRUT.format(dateStart))
            .build()
            )
        .setTimeTo(TimeStringMessage.newBuilder()
            .setTimeValue(FORMAT_DATE_MARSHRUT.format(dateFinish))
            .build()
            )
        .build().toByteArray());
      
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
      messageTemporary = "ERROR recive ActiveMQ request - timeout > " + mRequest.getTimeout()*60*1000; 
    }
    message = message + ";" + messageTemporary;
    mainForm.setWrite(messageTemporary);

    onMessageRequest = onMessageRequestWrite;
    // Добавление в log-файл  информации об направлении в MQ сообщения с именем файла с данными  
    if (onMessageRequest) {
      try {
        createMQSender(filePathRespond, mRequest, codeRoad, requestIntervalTimeMinute, dateStart);
        messageTemporary = "Successfully create MQ Sender"; 
      } catch (Exception e) {
        messageTemporary = "ERROR create MQ Sender";
        onMessageRequest = false;
      }
      message = message + ";" + messageTemporary;
      mainForm.setMqSender(messageTemporary);
    } else {    
      messageTemporary = "Don't create MQ Sender";
      message = message + ";" + messageTemporary;
      mainForm.setMqSender(messageTemporary);
    }

    GeneralData.mainForms.add(mainForm);
    if (onMessageRequest) LoaderelbrusApplication.logger.fatal(message); else LoaderelbrusApplication.logger.fatal(message);
    if (GeneralData.mainForms.size() > GeneralData.MAX_MAINFORMS_SHOW) GeneralData.mainForms.remove(0);

    return onMessageRequest;
  }

  // Формирование MQ сообщения
  private void createMQSender(String filePathRespond, MRequest mRequest, int codeRoad, int requestIntervalTimeMinute, Date dateStart) throws Exception { 
    MQSender mqSender = new MQSender(laConfig);
    mqSender.setId(mRequest.getId());
    mqSender.setCodeRoad(codeRoad);
    mqSender.setDate1(new Timestamp(dateStart.getTime()));
    mqSender.setDate2(new Timestamp(DateUtils.addMinutes(dateStart, requestIntervalTimeMinute).getTime()));
    mqSender.setData_name(mRequest.getName());
    mqSender.setSystem_name(GeneralData.SHORT_LOADERCOMM_NAME);
    mqSender.setXML_name("/" + filePathRespond.replace("\\", "/"));
    mqSender.send("");
  }

}
