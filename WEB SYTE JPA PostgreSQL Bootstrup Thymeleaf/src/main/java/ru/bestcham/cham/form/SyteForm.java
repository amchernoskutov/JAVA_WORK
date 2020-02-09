package ru.bestcham.cham.form;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.domain.CategorySyte;
import ru.bestcham.cham.domain.СategoryMenu;

@Slf4j
@Data
public class SyteForm {
  private String sytenameadd;
  private String syteurladd;
  private String sytedescriptionadd;
  private String picture; 
  
  private String syteidedit;
  private String sytenameedit;
  private String syteurledit;
  private String sytedescriptionedit;

  private СategoryMenu categoryMenu;
  private List<Integer> categorysytes; 
  private List<CategorySyte> ganres; 

  public SyteForm() {
    sytenameadd = "Введите название сайта";
    syteurladd = "Введите url";
    sytedescriptionadd = "Введите описание сайта";
    categorysytes = new ArrayList<>();
    ganres = new ArrayList<>();

  }

}
