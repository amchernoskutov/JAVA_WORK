package loader.elbrus.xml;

import loader.elbrus.proto.CommonProto.TimeStringMessage;
import lombok.Data;

/**
 * TTrainPointInfo
 * 
 * Класс используется в качестве служебного при формировании XML документа в классе XMLGraphsInfo
 *
 */

@Data
public class TTrainPointInfo {
  private int trNum;
  private int ESR; 
  private int codeGidOper;
  private TimeStringMessage dt; 
  private TimeStringMessage dtIn; 
  private TimeStringMessage dtOut;
  
  public TTrainPointInfo(int trNum, int ESR, int codeGidOper, TimeStringMessage dt) {
  this.trNum = trNum;
  this.ESR = ESR; 
  this.codeGidOper = codeGidOper;
  this.dt = dt;
  }
}
