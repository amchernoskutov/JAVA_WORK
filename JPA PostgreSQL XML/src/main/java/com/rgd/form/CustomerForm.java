package com.rgd.form;

import com.rgd.domain.Customer.Gender;
import lombok.Data;

@Data
public class CustomerForm {
  private Long id;
  private String customername;
  private String lastname;
  private Integer age;
  private Gender gender;

  public CustomerForm() {
    //
  }
}
