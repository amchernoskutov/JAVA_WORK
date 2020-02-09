package com.rgd.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ReportForm {
  private Long id;
  private LocalDate purchasedate;
  private BigDecimal amount; // Сумма покупки
  private Integer count; // Количество товара
  private String productname;

  public ReportForm() {
    //
  }

  public ReportForm(Long id, LocalDate purchasedate, String purchasename, BigDecimal amount,
      Integer count, String customername, String lastname, String productname) {
    this.id = id;
    this.purchasedate = purchasedate;
    this.amount = amount;
    this.count = count;
    this.productname = productname;

  }

}
