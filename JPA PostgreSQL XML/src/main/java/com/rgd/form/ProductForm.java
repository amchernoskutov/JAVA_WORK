package com.rgd.form;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductForm {
  private Long id;
  private String productname;
  private BigDecimal amount;

  public ProductForm() {
    //
  }
}
