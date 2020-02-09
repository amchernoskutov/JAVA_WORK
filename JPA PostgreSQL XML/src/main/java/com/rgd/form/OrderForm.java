package com.rgd.form;

import java.time.LocalDate;
import com.rgd.domain.Customer;
import com.rgd.domain.Product;
import lombok.Data;

@Data
public class OrderForm {
  private Long id;
  private Integer count;
  private LocalDate purchasedate;
  private Product product;
  private Customer customer;

  public OrderForm() {
    //
  }
}
