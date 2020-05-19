package asuter.admin.domain;

import lombok.Data;

@Data
public class SystemName {
  
  private int idSystem;
  private String mnemSystem;
  private String destination;
  
  public SystemName() {
    //
  }
}
