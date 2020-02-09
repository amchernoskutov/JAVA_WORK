package ru.bestcham.cham.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.form.FilmForm;
import ru.bestcham.cham.repository.FilmRepository;
import ru.bestcham.cham.service.FilmService;

@Slf4j
@Controller
@Data
@RequestMapping("/film")
public class FilmControler {
  @Value("${upload.pathfilm}")
  private String uploadPathFilm;
  
  private FilmRepository filmRepo;
  private FilmService filmService;

  public FilmControler(FilmRepository filmRepo) {
    this.filmRepo = filmRepo;
    this.filmService = new FilmService(this.filmRepo);
  }

  @GetMapping
  public String registerForm(FilmForm filmForm, Model model) {
    model.addAttribute("films", filmService.getFilms());
    
    return "film";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, 
      Map<String, Object> model) {
    
    if (value.contains("film#add*")) { 
      return "redirect:/filmadd";
    }
    if (value.contains("film#edit*")) {
      return "redirect:/filmedit"+filmService.getParamId(value);
    }

    if (value.contains("film#delete*")) {
      filmService.buttionDelete(value, uploadPathFilm);
    }
    
    return "redirect:/film";
  }

}
