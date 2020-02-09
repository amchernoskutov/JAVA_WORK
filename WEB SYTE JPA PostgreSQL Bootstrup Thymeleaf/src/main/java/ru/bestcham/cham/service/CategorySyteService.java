package ru.bestcham.cham.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.domain.CategorySyte;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.service.CategoryFilmService.MenuCategoryFilm;

@Slf4j
@Service
public class CategorySyteService {

  private CategorySyteRepository categorySyteRepo;

  @Autowired
  public CategorySyteService(CategorySyteRepository categorySyteRepo) {
    this.categorySyteRepo = categorySyteRepo;
  }
  
  public boolean addCategorySyte(String name) {
    if (categorySyteRepo.findByNameIgnoreCase(name.toUpperCase()).isEmpty()) {
      var categorySyte = new CategorySyte();
      categorySyte.setName(name);
      categorySyteRepo.save(categorySyte);

      return true;
    } else {
      return false;
    }
  }
  
  public boolean editCategorySyte(String categorySyteIdEdit, String name) {
    if (categorySyteRepo.findByNameIgnoreCase(name.toUpperCase()).stream().filter(f -> !categorySyteIdEdit.equals(f.getId().toString())).count() == 0)  {
      var categorySyte = categorySyteRepo.findById(Integer.parseInt(categorySyteIdEdit)); 
      categorySyte.setName(name);
      categorySyteRepo.save(categorySyte);

      return true;
    } else {
      return false;
    }
  }
  
  public List<CategorySyte> getCategorySytes() {
    List<CategorySyte> categorySytes = new ArrayList<>();
    categorySyteRepo.findAll().forEach(i -> categorySytes.add(i));

    return categorySytes;
  }
  
  public List<MenuCategorySyte> getmenuCategorySytes() {
    var menuCategorySytes = new ArrayList<MenuCategorySyte>();
    var categorySytes = new ArrayList<CategorySyte>();
    
    categorySyteRepo.findAll().forEach(i -> categorySytes.add(i));
    for(CategorySyte categorySyte : categorySytes) {
      menuCategorySytes.add(new MenuCategorySyte(categorySyte.getId(), categorySyte.getName()));
    }
    if (!menuCategorySytes.isEmpty()) menuCategorySytes.get(0).setActive("active");
    
    return menuCategorySytes;
  }

  public ArrayList<String> buttionAdd(String categorySyteNameAdd) {
    var errorMessages = new ArrayList<String>();

    if (categorySyteNameAdd.length() < 1) {
      errorMessages.add("Ошибка: Не задана категория сайта.");
    } else {
      if (!this.addCategorySyte(categorySyteNameAdd)) {
        errorMessages.add("Ошибка: Такая категория сайта уже есть.");
      }
    }

    return errorMessages;
  }

  public ArrayList<String> buttionEdit(String categorySyteIdEdit, String categorySyteNameEdit) {
    var errorMessages = new ArrayList<String>();

    if (categorySyteNameEdit.length() < 1) {
      errorMessages.add("Ошибка: Не задана категория сайта.");
    } else {
      if (!this.editCategorySyte(categorySyteIdEdit, categorySyteNameEdit)) {
        errorMessages.add("Ошибка: Такая категория сайта уже есть.");
      }
    }

    return errorMessages;
  }

  public void buttionDelete(String value) {
    var categorySyte = getCategorySyteFull(value);
    if (categorySyte != null) categorySyteRepo.delete(categorySyte);
  }

  public CategorySyte getCategorySyteFull (String activButtion) {
    return categorySyteRepo.findById(Integer.parseInt(activButtion.substring(activButtion.indexOf("*")+1))); 
  }

  public CategorySyte getCategorySyte (String id) {
    return categorySyteRepo.findById(Integer.parseInt(id)); 
  }
  
  public String getParamId (String value) {
    return "/?id="+value.substring(value.indexOf("*")+1); 
  }
  
  public void InitCategorySyte() {
    addCategorySyte("Интернет-магазин");
    addCategorySyte("Информационный сайт");
    addCategorySyte("Игровой портал");
    addCategorySyte("Персональный проект");
    addCategorySyte("Сайт-форум");
    addCategorySyte("Блог");
    addCategorySyte("Сайт-визитка");
  }

  @Data
  public class MenuCategorySyte {
    private MenuCategorySyte (Integer id, String name) {
      this.id=id;
      this.name=name;
      this.active="";
    }
    
    private Integer id;
    private String name; 
    private String active;
  }

}
