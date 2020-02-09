package ru.bestcham.cham.form;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CategoryFilmForm {
  private String categoryfilmnameadd;
  private String categoryfilmidedit;
  private String categoryfilmnameedit;

  public CategoryFilmForm() {
    categoryfilmnameadd = "Введите категорию фильма";
  }
}
