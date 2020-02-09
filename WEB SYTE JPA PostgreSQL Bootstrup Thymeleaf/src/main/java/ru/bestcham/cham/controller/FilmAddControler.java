package ru.bestcham.cham.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.form.FilmForm;
import ru.bestcham.cham.repository.CategoryFilmRepository;
import ru.bestcham.cham.repository.FilmRepository;
import ru.bestcham.cham.repository.СategoryMenuRepository;
import ru.bestcham.cham.service.CategoryFilmService;
import ru.bestcham.cham.service.FilmService;

@Slf4j
@Controller
@Data
@RequestMapping("/filmadd")
public class FilmAddControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();

  private String filmNameAdd; 
  private String filmOriginalFilmNameAdd;
  private Integer filmYearOfManufactureadd;
  private Integer filmDurationMinuteAdd;
  private String filmProducerAdd;
  private String filmIntotheroleAdd;
  private String filmDescriptionAdd;
  
  private FilmRepository filmRepo;
  private FilmService filmService;
  private CategoryFilmRepository categoryFilmRepo;
  private CategoryFilmService categoryFilmService;
  private СategoryMenuRepository categoryMenuRepo;
  
  @Value("${upload.pathfilm}")
  private String uploadPathFilm;

  public FilmAddControler(FilmRepository filmRepo, CategoryFilmRepository categoryFilmRepo, СategoryMenuRepository categoryMenuRepo) {
    this.filmRepo = filmRepo;
    this.filmService = new FilmService(this.filmRepo);
    this.categoryFilmRepo = categoryFilmRepo;
    this.categoryFilmService = new CategoryFilmService(this.categoryFilmRepo);
    this.categoryMenuRepo = categoryMenuRepo;
  }
  
  @GetMapping
  public String registerForm(FilmForm filmForm, Model model) {
    
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    }
    
    if ("".equals(filmNameAdd) || (filmNameAdd == null)) filmNameAdd = filmForm.getFilmnameadd();
    model.addAttribute("filmnameadd", filmNameAdd); 

    if ("".equals(filmOriginalFilmNameAdd) || (filmOriginalFilmNameAdd == null)) filmOriginalFilmNameAdd = filmForm.getFilmoriginalfilmnameadd();
    model.addAttribute("filmoriginalfilmnameadd", filmOriginalFilmNameAdd); 

    if ("".equals(filmYearOfManufactureadd) || (filmYearOfManufactureadd == null)) filmYearOfManufactureadd = filmForm.getFilmyearofmanufactureadd();
    model.addAttribute("filmyearofmanufactureadd", filmYearOfManufactureadd); 

    if ("".equals(filmDurationMinuteAdd) || (filmDurationMinuteAdd == null)) filmDurationMinuteAdd = filmForm.getFilmdurationminuteadd();
    model.addAttribute("filmdurationminuteadd", filmDurationMinuteAdd); 

    if ("".equals(filmProducerAdd) || (filmProducerAdd == null)) filmProducerAdd = filmForm.getFilmproduceradd();
    model.addAttribute("filmproduceradd", filmProducerAdd); 

    if ("".equals(filmIntotheroleAdd) || (filmIntotheroleAdd == null)) filmIntotheroleAdd = filmForm.getFilmintotheroleadd();
    model.addAttribute("filmintotheroleadd", filmIntotheroleAdd); 

    if ("".equals(filmDescriptionAdd) || (filmDescriptionAdd == null)) filmDescriptionAdd = filmForm.getFilmdescriptionadd();
    model.addAttribute("filmdescriptionadd", filmDescriptionAdd); 

    model.addAttribute("categoryfilms", categoryFilmService.getCategoryFilms());
    
    return "filmadd";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, 
      @RequestParam("file") MultipartFile file,  
      FilmForm filmForm, Map<String, Object> model) throws IOException {
    
    errorMessages.clear();

    if ("filmbut#buttion#add".equals(value)) {
      filmForm.setCategoryMenu(categoryMenuRepo.findByNameIgnoreCase("Фильмы"));

      List<CategoryFilm> categoryfilms = new ArrayList<CategoryFilm>(); 
      filmForm.getCategoryfilms().stream().forEach(f -> categoryfilms.add(categoryFilmRepo.findById(f)));
      filmForm.setGanres(categoryfilms);
      
      if (file != null && !file.getOriginalFilename().isEmpty()) {
        File uploadDir = new File(uploadPathFilm);

        if (!uploadDir.exists()) {
          uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo((new File(uploadPathFilm + "/" + resultFilename)));

        filmForm.setPicture(resultFilename);
      }
      
      
      errorMessages.addAll(filmService.buttionAdd(filmForm));
      filmNameAdd = filmForm.getFilmnameadd();
    }
    if ("filmbut#buttion#add#cancel".equals(value)) return "redirect:/film"; 
    if (errorMessages.isEmpty()) {
      filmNameAdd = "";
      return "redirect:/film";
    }
  
    return "redirect:/filmadd";
  }

}
