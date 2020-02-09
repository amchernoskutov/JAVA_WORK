package ru.bestcham.cham.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class CategoryFilm {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @NotNull
  @Size(min = 1, message = "Название жанра должно состоять минимум из одной буквы")
  private String name; // Название жанра

  public CategoryFilm() {
  }
}
