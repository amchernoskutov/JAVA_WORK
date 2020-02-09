package ru.bestcham.cham.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.domain.CategorySyte;
import ru.bestcham.cham.domain.CategoryYoutube;
import ru.bestcham.cham.repository.CategoryYoutubeRepository;
import ru.bestcham.cham.service.CategorySyteService.MenuCategorySyte;

@Slf4j
@Service
public class CategoryYoutubeService {

  private CategoryYoutubeRepository categoryYoutubeRepo;

  @Autowired
  public CategoryYoutubeService(CategoryYoutubeRepository categoryyoutubeRepo) {
    this.categoryYoutubeRepo = categoryyoutubeRepo;
  }
  
  public boolean addCategoryYoutube(String name) {
    if (categoryYoutubeRepo.findByNameIgnoreCase(name.toUpperCase()).isEmpty()) {
      var categoryYoutube = new CategoryYoutube();
      categoryYoutube.setName(name);
      categoryYoutubeRepo.save(categoryYoutube);

      return true;
    } else {
      return false;
    }
  }
  
  public boolean editCategoryYoutube(String categoryYoutubeIdEdit, String name) {
    if (categoryYoutubeRepo.findByNameIgnoreCase(name.toUpperCase()).stream().filter(f -> !categoryYoutubeIdEdit.equals(f.getId().toString())).count() == 0)  {
      var categoryYoutube = categoryYoutubeRepo.findById(Integer.parseInt(categoryYoutubeIdEdit)); 
      categoryYoutube.setName(name);
      categoryYoutubeRepo.save(categoryYoutube);

      return true;
    } else {
      return false;
    }
  }

  public List<CategoryYoutube> getCategoryYoutubes() {
    List<CategoryYoutube> categoryYoutubes = new ArrayList<>();
    categoryYoutubeRepo.findAll().forEach(i -> categoryYoutubes.add(i));

    return categoryYoutubes;
  }

  public List<MenuCategoryYoutube> getmenuCategoryYoutubes() {
    var menuCategoryYoutubes = new ArrayList<MenuCategoryYoutube>();
    var categoryYoutubes = new ArrayList<CategoryYoutube>();
    
    categoryYoutubeRepo.findAll().forEach(i -> categoryYoutubes.add(i));
    for(CategoryYoutube categoryYoutube : categoryYoutubes) {
      menuCategoryYoutubes.add(new MenuCategoryYoutube(categoryYoutube.getId(), categoryYoutube.getName()));
    }
    if (!menuCategoryYoutubes.isEmpty()) menuCategoryYoutubes.get(0).setActive("active");
    
    return menuCategoryYoutubes;
  }

  public ArrayList<String> buttionAdd(String categoryYoutubeNameAdd) {
    var errorMessages = new ArrayList<String>();

    if (categoryYoutubeNameAdd.length() < 1) {
      errorMessages.add("Ошибка: Не задана категория youtube канала.");
    } else {
      if (!this.addCategoryYoutube(categoryYoutubeNameAdd)) {
        errorMessages.add("Ошибка: Такая категория youtube канала уже есть.");
      }
    }

    return errorMessages;
  }

  public ArrayList<String> buttionEdit(String categoryYoutubeIdEdit, String categoryYoutubeNameEdit) {
    var errorMessages = new ArrayList<String>();

    if (categoryYoutubeNameEdit.length() < 1) {
      errorMessages.add("Ошибка: Не задана категория youtube канала.");
    } else {
      if (!this.editCategoryYoutube(categoryYoutubeIdEdit, categoryYoutubeNameEdit)) {
         errorMessages.add("Ошибка: Такая категория youtube канала уже есть.");
      }
    }

    return errorMessages;
  }

  public void buttionDelete(String value) {
    CategoryYoutube categoryYoutube = getCategoryYoutubeFull(value);
    if (categoryYoutube != null) categoryYoutubeRepo.delete(categoryYoutube);
  }

  public CategoryYoutube getCategoryYoutubeFull (String activButtion) {
    return categoryYoutubeRepo.findById(Integer.parseInt(activButtion.substring(activButtion.indexOf("*")+1))); 
  }

  public CategoryYoutube getCategoryYoutube (String id) {
    return categoryYoutubeRepo.findById(Integer.parseInt(id)); 
  }
  
  public String getParamId (String value) {
    return "/?id="+value.substring(value.indexOf("*")+1); 
  }

  public void InitCategoryYoutube() {
    addCategoryYoutube("Транспорт");
    addCategoryYoutube("Мода и красота");
    addCategoryYoutube("Юмор");
    addCategoryYoutube("Образование");
    addCategoryYoutube("Развлечения");
    addCategoryYoutube("Каналы для всей семьи");
    addCategoryYoutube("Кино и анимация");
    addCategoryYoutube("Кулинария");
    addCategoryYoutube("Игры");
    addCategoryYoutube("Хобби и стиль");
    addCategoryYoutube("Музыка");
    addCategoryYoutube("Новости и политика");
    addCategoryYoutube("Люди и блоги");
    addCategoryYoutube("Путешествия");
  }
  
  @Data
  public class MenuCategoryYoutube {
    private MenuCategoryYoutube (Integer id, String name) {
      this.id=id;
      this.name=name;
      this.active="";
    }
    
    private Integer id;
    private String name; 
    private String active;
  }

}
