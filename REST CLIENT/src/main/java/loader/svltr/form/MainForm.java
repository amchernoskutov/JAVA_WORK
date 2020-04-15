package loader.svltr.form;

import java.util.Date;
import lombok.Data;

/**
 * MainForm
 * 
 * Класс для вывода информации о работе системы на гланой странице 
 *
 */

@Data
public class MainForm {
  private String name;
  private String dor;
  private String depo;
  private String request;
  private String write;
  private String mqSender;
  private Date dateCreate;
}
