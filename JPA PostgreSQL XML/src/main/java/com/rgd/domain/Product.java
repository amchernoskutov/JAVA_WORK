package com.rgd.domain;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  @Size(min = 3, message = "Название покупки должно быть не менее 3 символов в длинну")
  private String productname; // Название товара

  @NotNull
  private BigDecimal amount; // Цена

  @OneToOne(targetEntity = Order.class, fetch = FetchType.EAGER)
  private List<Order> orders;

  @Override
  public String toString() {

    return productname + " - цена: " + amount;
  }

}
