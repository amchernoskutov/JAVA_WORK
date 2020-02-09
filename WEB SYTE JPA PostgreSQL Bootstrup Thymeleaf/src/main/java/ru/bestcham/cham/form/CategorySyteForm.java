package ru.bestcham.cham.form;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CategorySyteForm {
  private String categorysytenameadd;
  private String categorysyteidedit;
  private String categorysytenameedit;

  public CategorySyteForm() {
    categorysytenameadd = "Введите категорию сайта";
  }
}
