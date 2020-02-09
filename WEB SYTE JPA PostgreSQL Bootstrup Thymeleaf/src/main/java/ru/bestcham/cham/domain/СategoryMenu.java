package ru.bestcham.cham.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class СategoryMenu {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  
  @NotNull
  private String name; // Название пункта меню
  
}
