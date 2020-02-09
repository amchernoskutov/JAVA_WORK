package ru.bestcham.cham.form;

import lombok.Data;

@Data
public class HomeForm {
  private String homefind;
  
  public HomeForm() {
    homefind = "Поиск по сайту";
  }
  
}
