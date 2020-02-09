package ru.bestcham.cham.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.domain.Film;
import ru.bestcham.cham.domain.Syte;
import ru.bestcham.cham.form.FilmForm;
import ru.bestcham.cham.form.SyteForm;
import ru.bestcham.cham.repository.SyteRepository;
import ru.bestcham.cham.service.FilmService.MenuFilm;

@Slf4j
@Service
public class SyteService {
  private SyteRepository syteRepo;

  @Autowired
  public SyteService(SyteRepository syteRepo) {
    this.syteRepo = syteRepo;
  }

  public boolean addSyte(SyteForm syteForm) {
    if (syteRepo.findByNameIgnoreCase(syteForm.getSytenameadd().toUpperCase()).isEmpty()) {
      var syte = new Syte();
      syte.setName(syteForm.getSytenameadd());
      syte.setCategorymenu(syteForm.getCategoryMenu());
      syte.setUrl(syteForm.getSyteurladd());
      syte.setDescription(syteForm.getSytedescriptionadd());
      
      syte.setPicture(syteForm.getPicture());
      
      syte.setCategorysytes(syteForm.getGanres());

      syteRepo.save(syte);

      return true;
    } else {
      return false;
    }
  }
  
  public boolean editSyte(SyteForm syteForm, String uploadPath) {
    if (syteRepo.findByNameIgnoreCase(syteForm.getSytenameedit().toUpperCase()).stream().filter(f -> !syteForm.getSyteidedit().equals(f.getId().toString())).count() == 0)  {
      var syte = syteRepo.findById(Integer.parseInt(syteForm.getSyteidedit())); 
      syte.setName(syteForm.getSytenameedit());

      syte.setUrl(syteForm.getSyteurledit());
      syte.setDescription(syteForm.getSytedescriptionedit());
      
      if (syteForm.getPicture() != null ) {
        File file = new File(uploadPath + "/" + syte.getPicture());
        file.delete();
        syte.setPicture(syteForm.getPicture());
      }

      syte.setCategorysytes(syteForm.getGanres());
      
      syteRepo.save(syte);

      return true;
    } else {
      return false;
    }
  }

  public List<Syte> getSytes() {
    List<Syte> sytes = new ArrayList<>();
    syteRepo.findAll().forEach(i -> sytes.add(i));
    
    return sytes;
  }
  
  public List<MenuSyte> getmenuSytes() {
    var menuSytes = new ArrayList<MenuSyte>();

    syteRepo.findAll().forEach(syte -> {
      
      var catSytes = new ArrayList<String>();
      syte.getCategorysytes().forEach(f -> catSytes.add(f.getName()));

      menuSytes.add(new MenuSyte(syte.getId(), syte.getName(), syte.getPlacedAt(), 
          syte.getUrl(), StringUtils.join(catSytes, ", "),  
          syte.getDescription(), 
          syte.getPicture()));
      
    });
    
    return menuSytes;
  }

  public ArrayList<String> buttionAdd(SyteForm syteForm) {
    var errorMessages = new ArrayList<String>();

    if (syteForm.getSytenameadd().length() < 1) {
      errorMessages.add("Ошибка: Не задано название сайта.");
    } else {
      if (!this.addSyte(syteForm)) {
        errorMessages.add("Ошибка: Такое название сайта уже есть.");
      }
    }

    return errorMessages;
  }
  
  public ArrayList<String> buttionEdit(SyteForm syteFrom, String uploadPathin) {
    var errorMessages = new ArrayList<String>();

    if (syteFrom.getSytenameedit().length() < 1) {
      errorMessages.add("Ошибка: Не задано название сайта.");
    } else {
      if (!this.editSyte(syteFrom, uploadPathin)) {
        errorMessages.add("Ошибка: Такое название сайта уже есть.");
      }
    }

    return errorMessages;
  }
  
  public void buttionDelete(String value, String uploadPath) {
    var syte = getSyteFull(value);
    if (syte != null) {
      syte.getCategorysytes().remove(syte.getId());
      syteRepo.save(syte);

      var file = new File(uploadPath + "/" + syte.getPicture());
      file.delete();

      syteRepo.delete(syte);
    }
  }

  public Syte getSyteFull (String activButtion) {
    return syteRepo.findById(Integer.parseInt(activButtion.substring(activButtion.indexOf("*")+1))); 
  }

  public Syte getSyte (String id) {
    return syteRepo.findById(Integer.parseInt(id)); 
  }

  public String getParamId (String value) {
    return "/?id="+value.substring(value.indexOf("*")+1); 
  }

  @Data
  public class MenuSyte {
    private MenuSyte (Integer id, String name, Date placedAt, String url, 
        String categorysytes, String description, String picture) {
      this.id=id;
      this.name=name;
      this.placedAt=placedAt;
      this.url=url; 
      this.categorysytes=categorysytes; 
      this.description=description; 
      this.picture=picture; 
      this.active="";
    }
    
    Integer id;
    String name; 
    Date placedAt;
    String url; 
    String categorysytes; 
    String description; 
    String picture; 
    String active;
  }

}
