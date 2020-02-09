package ru.bestcham.cham.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.repository.CategoryFilmRepository;

@Slf4j
@Service
public class CategoryFilmService {

  private CategoryFilmRepository categoryFilmRepo;

  @Autowired
  public CategoryFilmService(CategoryFilmRepository categoryFilmRepo) {
    this.categoryFilmRepo = categoryFilmRepo;
  }
  
  public boolean addCategoryFilm(String name) {
    if (categoryFilmRepo.findByNameIgnoreCase(name.toUpperCase()).isEmpty()) {
      CategoryFilm categoryFilm = new CategoryFilm();
      categoryFilm.setName(name);
      categoryFilmRepo.save(categoryFilm);

      return true;
    } else {
      return false;
    }
  }
  
  public boolean editCategoryFilm(String categoryFilmIdEdit, String name) {
  if (categoryFilmRepo.findByNameIgnoreCase(name.toUpperCase()).stream().filter(f -> !categoryFilmIdEdit.equals(f.getId().toString())).count() == 0)  {
      CategoryFilm categoryFilm = categoryFilmRepo.findById(Integer.parseInt(categoryFilmIdEdit)); 
      categoryFilm.setName(name);
      categoryFilmRepo.save(categoryFilm);

      return true;
    } else {
      return false;
    }
  }

  public List<CategoryFilm> getCategoryFilms() {
    var categoryFilms = new ArrayList<CategoryFilm>();
    categoryFilmRepo.findAll().forEach(i -> categoryFilms.add(i));

    return categoryFilms;
  }

  public List<MenuCategoryFilm> getmenuCategoryFilms() {
    var menuCategoryFilms = new ArrayList<MenuCategoryFilm>();
    var categoryFilms = new ArrayList<CategoryFilm>();
    
    categoryFilmRepo.findAll().forEach(i -> categoryFilms.add(i));
    for(CategoryFilm categoryFilm : categoryFilms) {
      menuCategoryFilms.add(new MenuCategoryFilm(categoryFilm.getId(), categoryFilm.getName()));
    }
    if (!menuCategoryFilms.isEmpty()) menuCategoryFilms.get(0).setActive("active");
    
    return menuCategoryFilms;
  }

  public ArrayList<String> buttionAdd(String categoryFilmNameAdd) {
    var errorMessages = new ArrayList<String>();

    if (categoryFilmNameAdd.length() < 1) {
      errorMessages.add("Ошибка: Не задан жанр фильма.");
    } else {
      if (!this.addCategoryFilm(categoryFilmNameAdd)) {
        errorMessages.add("Ошибка: Такой жанр фильма уже есть.");
      }
    }

    return errorMessages;
  }

  public ArrayList<String> buttionEdit(String categoryFilmIdEdit, String categoryFilmNameEdit) {
    var errorMessages = new ArrayList<String>();

    if (categoryFilmNameEdit.length() < 1) {
      errorMessages.add("Ошибка: Не задан жанр фильма.");
    } else {
      if (!this.editCategoryFilm(categoryFilmIdEdit, categoryFilmNameEdit)) {
        errorMessages.add("Ошибка: Такой жанр фильма уже есть.");
      }
    }

    return errorMessages;
  }

  public void buttionDelete(String value) {
    var categoryFilm = getCategoryFilmFull(value);
    if (categoryFilm != null) categoryFilmRepo.delete(categoryFilm);
  }

  public CategoryFilm getCategoryFilmFull (String activButtion) {
    return categoryFilmRepo.findById(Integer.parseInt(activButtion.substring(activButtion.indexOf("*")+1))); 
  }

  public CategoryFilm getCategoryFilm (String id) {
    return categoryFilmRepo.findById(Integer.parseInt(id)); 
  }

  public String getParamId (String value) {
    return "/?id="+value.substring(value.indexOf("*")+1); 
  }

  public void InitCategoryFilm() {
    addCategoryFilm("Комедия");
    addCategoryFilm("Боевик");
    addCategoryFilm("Детектив");
    addCategoryFilm("Документальный");
    addCategoryFilm("Драма");
    addCategoryFilm("Исторический");
    addCategoryFilm("Мелодрама");
    addCategoryFilm("Мистика");
    addCategoryFilm("Мюзикл");
    addCategoryFilm("Приключения");
    addCategoryFilm("Семейный");
    addCategoryFilm("Триллер");
    addCategoryFilm("Ужасы");
    addCategoryFilm("Фантастика");
    addCategoryFilm("Биографический");
  }

  @Data
  public class MenuCategoryFilm {
    private MenuCategoryFilm (Integer id, String name) {
      this.id=id;
      this.name=name;
      this.active="";
    }
    
    private Integer id;
    private String name; 
    private String active;
  }

}
