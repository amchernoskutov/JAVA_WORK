package ru.bestcham.cham.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.domain.CategoryYoutube;
import ru.bestcham.cham.domain.Film;
import ru.bestcham.cham.domain.СategoryMenu;
import ru.bestcham.cham.form.FilmForm;
import ru.bestcham.cham.repository.FilmRepository;
import ru.bestcham.cham.service.CategoryYoutubeService.MenuCategoryYoutube;

@Slf4j
@Service
public class FilmService {
  private FilmRepository filmRepo;
  
  @Autowired
  public FilmService(FilmRepository filmRepo) {
    this.filmRepo = filmRepo;
  }

  public boolean addFilm(FilmForm filmForm) {
    if (filmRepo.findByNameIgnoreCase(filmForm.getFilmnameadd().toUpperCase()).isEmpty()) {
      var film = new Film();
      film.setName(filmForm.getFilmnameadd());
      film.setCategorymenu(filmForm.getCategoryMenu());

      film.setOriginalfilmname(filmForm.getFilmoriginalfilmnameadd());
      film.setYearofmanufacture(filmForm.getFilmyearofmanufactureadd());
      film.setDurationminute(filmForm.getFilmdurationminuteadd());
      film.setProducer(filmForm.getFilmproduceradd());
      film.setIntotherole(filmForm.getFilmintotheroleadd());
      film.setDescription(filmForm.getFilmdescriptionadd());
      
      film.setPicture(filmForm.getPicture());
      
      film.setCategoryfilms(filmForm.getGanres());
      
      filmRepo.save(film);

      return true;
    } else {
      return false;
    }
  }
  
  public boolean editFilm(FilmForm filmForm, String uploadPath) {
    if (filmRepo.findByNameIgnoreCase(filmForm.getFilmnameedit().toUpperCase()).stream().filter(f -> !filmForm.getFilmidedit().equals(f.getId().toString())).count() == 0)  {
      var film = filmRepo.findById(Integer.parseInt(filmForm.getFilmidedit())); 
      film.setName(filmForm.getFilmnameedit());
     
      film.setOriginalfilmname(filmForm.getFilmoriginalfilmnameedit());
      film.setYearofmanufacture(filmForm.getFilmyearofmanufactureedit());
      film.setDurationminute(filmForm.getFilmdurationminuteedit());
      film.setProducer(filmForm.getFilmproduceredit());
      film.setIntotherole(filmForm.getFilmintotheroleedit());
      film.setDescription(filmForm.getFilmdescriptionedit());
      
      if (filmForm.getPicture() != null ) {
        File file = new File(uploadPath + "/" + film.getPicture());
        file.delete();
        film.setPicture(filmForm.getPicture());
      }

      film.setCategoryfilms(filmForm.getGanres());
      
      filmRepo.save(film);

      return true;
    } else {
      return false;
    }
  }

  public List<Film> getFilms() {
    List<Film> films = new ArrayList<>();
    filmRepo.findAll().forEach(i -> films.add(i));

    return films;
  }
  
  public List<MenuFilm> getmenuFilms() {
    var menuFilms = new ArrayList<MenuFilm>();

    filmRepo.findAll().forEach(film -> {
      
      var catFilms = new ArrayList<String>();
      film.getCategoryfilms().forEach(f -> catFilms.add(f.getName()));

      menuFilms.add(new MenuFilm(film.getId(), film.getName(), film.getPlacedAt(), 
          film.getOriginalfilmname(), film.getYearofmanufacture(), StringUtils.join(catFilms, ", "),  
          film.getProducer(), film.getIntotherole(), film.getDurationminute(), film.getDescription(), 
          film.getPicture()));
      
    });
    
    return menuFilms;
  }
  
  public ArrayList<String> buttionAdd(FilmForm filmForm) {
    var errorMessages = new ArrayList<String>();

    if (filmForm.getFilmnameadd().length() < 1) {
      errorMessages.add("Ошибка: Не задано название фильма.");
    } else {
      if (!this.addFilm(filmForm)) {
        errorMessages.add("Ошибка: Такое название фильма уже есть.");
      }
    }

    return errorMessages;
  }

  public ArrayList<String> buttionEdit(FilmForm filmFrom, String uploadPathin) {
    var errorMessages = new ArrayList<String>();

    if (filmFrom.getFilmnameedit().length() < 1) {
      errorMessages.add("Ошибка: Не задано название фильма.");
    } else {
      if (!this.editFilm(filmFrom, uploadPathin)) {
        errorMessages.add("Ошибка: Такое название фильма уже есть.");
      }
    }

    return errorMessages;
  }

  public void buttionDelete(String value, String uploadPath) {
    var film = getFilmFull(value);
    if (film != null) {
      film.getCategoryfilms().remove(film.getId());
      filmRepo.save(film);

      var file = new File(uploadPath + "/" + film.getPicture());
      file.delete();

      filmRepo.delete(film);
    }
  }

  public Film getFilmFull (String activButtion) {
    return filmRepo.findById(Integer.parseInt(activButtion.substring(activButtion.indexOf("*")+1))); 
  }

  public Film getFilm (String id) {
    return filmRepo.findById(Integer.parseInt(id)); 
  }
  
  public String getParamId (String value) {
    return "/?id="+value.substring(value.indexOf("*")+1); 
  }

  @Data
  public class MenuFilm {
    private MenuFilm (Integer id, String name, Date placedAt, String originalfilmname, 
        Integer yearofmanufacture, String categoryfilms, String producer, 
        String intotherole, Integer durationminute, String description, String picture) {
      this.id=id;
      this.name=name;
      this.placedAt=placedAt;
      this.originalfilmname=originalfilmname; 
      this.yearofmanufacture=yearofmanufacture; 
      this.categoryfilms=categoryfilms; 
      this.producer=producer; 
      this.intotherole=intotherole; 
      this.durationminute=durationminute; 
      this.description=description; 
      this.picture=picture; 
      this.active="";
    }
    
    Integer id;
    String name; 
    Date placedAt;
    String originalfilmname; 
    Integer yearofmanufacture; 
    String categoryfilms; 
    String producer; 
    String intotherole; 
    Integer durationminute; 
    String description; 
    String picture; 
    String active;
  }

}
