package com.rgd.domain;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import lombok.Data;

@Data
@Entity
@Table(name = "rgd_order")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull
  private Integer count; // Количество товара

  @NotNull
  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate purchasedate;

  @NotNull
  @ManyToOne(optional = true)
  private Product product;

  @NotNull
  @ManyToOne(optional = true)
  private Customer customer;
}
