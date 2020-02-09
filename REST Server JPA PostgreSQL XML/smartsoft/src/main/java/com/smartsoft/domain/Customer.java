package com.smartsoft.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Customer {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  @NotNull
  @Size(min=3, max=50, message="Имя покупателя должно быть не менее 3-х и не более 50 символов в длинну")
  private String customername; // Имя
  
  @Size(min=3, max=50, message="Фамилия покупателя должна быть не менее 3-х и не более 50 символов в длинну")
  private String lastname; // Фамилия

  @NotNull
  @Digits(integer=3, fraction=0, message = "Возраст покупателя не более 3-х знаков")
  @Min(0)
  @Max(130)
  private Integer age; // Возраст
  
  @OneToMany(mappedBy="customers", fetch = FetchType.EAGER)
  private List<Shoppinglist> shoppinglists;
}
