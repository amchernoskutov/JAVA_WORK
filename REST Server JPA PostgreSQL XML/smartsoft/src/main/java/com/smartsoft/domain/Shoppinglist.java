package com.smartsoft.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import lombok.Data;

@Data
@Entity
public class Shoppinglist {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;

  @NotNull 
  private Integer count; // Количество товара
  
  @NotNull
  private BigDecimal amount; // Сумма покупки 
  
  @NotNull
  @DateTimeFormat(iso = ISO.DATE)
  private LocalDate purchasedate;
  
  @NotNull
  @ManyToOne(optional=true, cascade=CascadeType.ALL)
  @JoinColumn (name="purchase_id")
  private Purchase purchases; 

  @NotNull
  @ManyToOne(optional=true, cascade=CascadeType.ALL)
  @JoinColumn (name="customer_id")
  private Customer customers; 
  
  @ElementCollection(targetClass = Article.class, fetch = FetchType.EAGER)
  @Size(min=1, message="Вы должны выбрать хотябы один товар")
  @CollectionTable(name = "shoppinglist_article", joinColumns = @JoinColumn(name = "shoppinglist_id"))
  @Enumerated(EnumType.STRING)
  private Set<Article> articles;

  
}



