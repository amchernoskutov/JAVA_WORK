package ru.bestcham.cham.form;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.domain.СategoryMenu;

@Slf4j
@Data
public class FilmForm {
  private String filmnameadd;

  private String filmoriginalfilmnameadd;
  private Integer filmyearofmanufactureadd;
  private Integer filmdurationminuteadd;
  private String filmproduceradd;
  private String filmintotheroleadd;
  private String filmdescriptionadd;
  private String picture; 
  
  private String filmidedit;
  private String filmnameedit;
  private String filmoriginalfilmnameedit;
  private Integer filmyearofmanufactureedit;
  private Integer filmdurationminuteedit;
  private String filmproduceredit;
  private String filmintotheroleedit;
  private String filmdescriptionedit;
  
  private СategoryMenu categoryMenu;
  private List<Integer> categoryfilms; 
  private List<CategoryFilm> ganres; 

  public FilmForm() {
    filmnameadd = "Введите название фильма";
    filmyearofmanufactureadd = 2020;
    filmdurationminuteadd = 130;
    filmoriginalfilmnameadd = "Введите оригинальное название фильма";
    filmproduceradd = "Введите данные режиссера фильма";
    filmintotheroleadd = "Введите данные актеров";
    filmdescriptionadd = "Введите описание фильма";
    categoryfilms = new ArrayList<>();
    ganres = new ArrayList<>();
  }

}
