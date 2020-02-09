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
import ru.bestcham.cham.domain.Youtube;
import ru.bestcham.cham.form.FilmForm;
import ru.bestcham.cham.form.YoutubeForm;
import ru.bestcham.cham.repository.FilmRepository;
import ru.bestcham.cham.repository.YoutubeRepository;
import ru.bestcham.cham.service.SyteService.MenuSyte;

@Slf4j
@Service
public class YoutubeService {
  private YoutubeRepository youtubeRepo;

  @Autowired
  public YoutubeService(YoutubeRepository youtubeRepo) {
    this.youtubeRepo = youtubeRepo;
  }

  public boolean addYoutube(YoutubeForm youtubeForm) {
    if (youtubeRepo.findByNameIgnoreCase(youtubeForm.getYoutubenameadd().toUpperCase()).isEmpty()) {
      var youtube = new Youtube();
      youtube.setName(youtubeForm.getYoutubenameadd());
      youtube.setCategorymenu(youtubeForm.getCategoryMenu());
      youtube.setUrl(youtubeForm.getYoutubeurladd());
      youtube.setDescription(youtubeForm.getYoutubedescriptionadd());
      
      youtube.setPicture(youtubeForm.getPicture());
      
      youtube.setCategoryyoutubes(youtubeForm.getGanres());
      
      youtubeRepo.save(youtube);

      return true;
    } else {
      return false;
    }
  }
  
  public boolean editYoutube(YoutubeForm youtubeForm, String uploadPath) {
    if (youtubeRepo.findByNameIgnoreCase(youtubeForm.getYoutubenameedit().toUpperCase()).stream().filter(f -> !youtubeForm.getYoutubeidedit().equals(f.getId().toString())).count() == 0)  {
      var youtube = youtubeRepo.findById(Integer.parseInt(youtubeForm.getYoutubeidedit())); 
      youtube.setName(youtubeForm.getYoutubenameedit());

      youtube.setUrl(youtubeForm.getYoutubeurledit());
      youtube.setDescription(youtubeForm.getYoutubedescriptionedit());
      
      if (youtubeForm.getPicture() != null ) {
        var file = new File(uploadPath + "/" + youtube.getPicture());
        file.delete();
        youtube.setPicture(youtubeForm.getPicture());
      }

      youtube.setCategoryyoutubes(youtubeForm.getGanres());

      youtubeRepo.save(youtube);

      return true;
    } else {
      return false;
    }
  }

  public List<Youtube> getYoutubes() {
    List<Youtube> youtubes = new ArrayList<>();
    youtubeRepo.findAll().forEach(i -> youtubes.add(i));

    return youtubes;
  }
  
  public List<MenuYoutube> getmenuYoutubes() {
    var menuYoutubes = new ArrayList<MenuYoutube>();

    youtubeRepo.findAll().forEach(youtube -> {
      
      var catYoutubes = new ArrayList<String>();
      youtube.getCategoryyoutubes().forEach(f -> catYoutubes.add(f.getName()));

      menuYoutubes.add(new MenuYoutube(youtube.getId(), youtube.getName(), youtube.getPlacedAt(), 
          youtube.getUrl(), StringUtils.join(catYoutubes, ", "),  
          youtube.getDescription(), 
          youtube.getPicture()));
      
    });
    
    return menuYoutubes;
  }

  public ArrayList<String> buttionAdd(YoutubeForm youtubeForm) {
    var errorMessages = new ArrayList<String>();

    if (youtubeForm.getYoutubenameadd().length() < 1) {
      errorMessages.add("Ошибка: Не задано название youtube канала.");
    } else {
      if (!this.addYoutube(youtubeForm)) {
        errorMessages.add("Ошибка: Такое название youtube канала уже есть.");
      }
    }

    return errorMessages;
  }

  public ArrayList<String> buttionEdit(YoutubeForm youtubeFrom, String uploadPathin) {
    var errorMessages = new ArrayList<String>();

    if (youtubeFrom.getYoutubenameedit().length() < 1) {
      errorMessages.add("Ошибка: Не задано название youtube канала.");
    } else {
      if (!this.editYoutube(youtubeFrom, uploadPathin)) {
        errorMessages.add("Ошибка: Такое название youtube канала уже есть.");
      }
    }

    return errorMessages;
  }
  
  public void buttionDelete(String value, String uploadPath) {
    var youtube = getYoutubeFull(value);
    if (youtube != null) {
      youtube.getCategoryyoutubes().remove(youtube.getId());
      youtubeRepo.save(youtube);

      var file = new File(uploadPath + "/" + youtube.getPicture());
      file.delete();

      youtubeRepo.delete(youtube);
    }
  }

  public Youtube getYoutubeFull (String activButtion) {
    return youtubeRepo.findById(Integer.parseInt(activButtion.substring(activButtion.indexOf("*")+1))); 
  }

  public Youtube getYoutube (String id) {
    return youtubeRepo.findById(Integer.parseInt(id)); 
  }

  public String getParamId (String value) {
    return "/?id="+value.substring(value.indexOf("*")+1); 
  }

  @Data
  public class MenuYoutube {
    private MenuYoutube (Integer id, String name, Date placedAt, String url, 
        String categoryyoutubes, String description, String picture) {
      this.id=id;
      this.name=name;
      this.placedAt=placedAt;
      this.url=url; 
      this.categoryyoutubes=categoryyoutubes; 
      this.description=description; 
      this.picture=picture; 
      this.active="";
    }
    
    Integer id;
    String name; 
    Date placedAt;
    String url; 
    String categoryyoutubes; 
    String description; 
    String picture; 
    String active;
  }

}
