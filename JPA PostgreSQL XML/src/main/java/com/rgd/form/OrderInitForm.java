package com.rgd.form;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class OrderInitForm {
  private Long id;
  private Integer count;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate purchasedate;
  private Integer idcustomer;
  private Integer idproduct;

  public OrderInitForm() {
    this.idcustomer = 0;
    this.idproduct = 0;
    this.count = 0;
    this.purchasedate = LocalDate.now();
  }

}
