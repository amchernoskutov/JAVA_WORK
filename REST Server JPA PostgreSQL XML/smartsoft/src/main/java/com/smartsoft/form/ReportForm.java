package com.smartsoft.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import com.smartsoft.domain.Article;
import lombok.Data;

@Data
public class ReportForm {
  private String purchasename;
  private String customername; // Имя
  private String lastname; // Фамилия
  private Integer age; // Возраст
  private Integer count; // Количество товара
  private BigDecimal amount; // Сумма покупки
  private LocalDate purchasedate;
  private Set<Article> articles;
  private String article;

  public ReportForm() {
    //
  }

  public ReportForm(String purchasename, String customername, String lastname, Integer age,
      Integer count, BigDecimal amount, LocalDate purchasedate, Set<Article> articles,
      String article) {
    this.purchasename = purchasename;
    this.customername = customername;
    this.lastname = lastname;
    this.age = age;
    this.count = count;
    this.amount = amount;
    this.purchasedate = purchasedate;
    this.articles = articles;
    this.article = article;
  }

}
