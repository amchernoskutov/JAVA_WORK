package com.rgd.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class ReportParamForm {
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate firstdate;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate seconddate;
  private Integer idcustomer;
  private BigDecimal sumamount;
  private Integer sumcount;

  public ReportParamForm() {
    this.firstdate = LocalDate.now();
    this.seconddate = LocalDate.now();
    this.idcustomer = 0;
  }
}
