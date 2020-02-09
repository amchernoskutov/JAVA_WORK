package com.smartsoft.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Purchase {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  @NotNull
  @Size(min=3, message="Название покупки должно быть не менее 3 символов в длинну")
  private String purchasename; // Название покупки

  @OneToMany(mappedBy="purchases", fetch = FetchType.EAGER)
  private List<Shoppinglist> shoppingLists;
}
