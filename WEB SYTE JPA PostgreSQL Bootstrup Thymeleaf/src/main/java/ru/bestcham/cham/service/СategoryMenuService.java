package ru.bestcham.cham.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.СategoryMenu;
import ru.bestcham.cham.repository.СategoryMenuRepository;

@Slf4j
@Service
public class СategoryMenuService {

  private СategoryMenuRepository categoryMenuRepo;
  
  @Autowired
  public СategoryMenuService(СategoryMenuRepository categoryMenuRepo) {
    this.categoryMenuRepo = categoryMenuRepo;
  }
  
  public СategoryMenu loadСategoryMenuByName(String name) {
    return categoryMenuRepo.findByNameIgnoreCase(name.toUpperCase()); 
  }

  public СategoryMenu addСategoryMenu(String name) {
    if (categoryMenuRepo.findByNameIgnoreCase(name.toUpperCase()) == null) {
      СategoryMenu categoryMenu = new СategoryMenu();
      categoryMenu.setName(name);
      return categoryMenuRepo.save(categoryMenu);
    } else {
       return null;
    }
  }
  
  public List<СategoryMenu> getCategoryMenus() {
    var categoryMenus = new ArrayList<СategoryMenu>();
    categoryMenuRepo.findAll().forEach(i -> categoryMenus.add(i));

    return categoryMenus;
  }
  
  public void InitCategoryfilm() {
    log.info(" --- Designing BestCHAM Добавлена запись в таблицу Categoryfilm " + addСategoryMenu("Фильмы").getName());
    log.info(" --- Designing BestCHAM Добавлена запись в таблицу Categoryfilm " + addСategoryMenu("Сайты").getName());
    log.info(" --- Designing BestCHAM Добавлена запись в таблицу Categoryfilm " + addСategoryMenu("Youtube").getName());
  }

}
