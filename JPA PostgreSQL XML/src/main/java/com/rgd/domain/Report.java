package com.rgd.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class Report {
  private Long idorder;
  private LocalDate purchasedate;
  private String productname;
  private Integer count;
  private BigDecimal amount;
}
