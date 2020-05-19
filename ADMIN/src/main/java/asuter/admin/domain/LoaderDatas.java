package asuter.admin.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoaderDatas {
  private int id;
  private boolean check;
  private String respond;  
  private int idRequest;
  private String shotNameRequest;
  private String shotNameSystem;
  private String shotNameData;
  private Date dateStart;
  private Date dateFinish;
  private String pathAndFileName;
}
