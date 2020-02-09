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
import ru.bestcham.cham.domain.Film;
import ru.bestcham.cham.form.FilmForm;
import ru.bestcham.cham.repository.CategoryFilmRepository;
import ru.bestcham.cham.repository.FilmRepository;
import ru.bestcham.cham.service.CategoryFilmService;
import ru.bestcham.cham.service.FilmService;

@Slf4j
@Controller
@Data
@RequestMapping("/filmedit")
public class FilmEditControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();
  private String filmIdEdit;
  private String filmNameEdit;
  private String filmOriginalFilmNameEdit;
  private Integer filmYearOfManufactureEdit;
  private Integer filmDurationMinuteEdit;
  private String filmProducerEdit;
  private String filmIntotheroleEdit;
  private String filmDescriptionEdit;
  private String picture;

  private FilmRepository filmRepo;
  private FilmService filmService;
  private CategoryFilmRepository categoryFilmRepo;
  private CategoryFilmService categoryFilmService;

  private class CategoryFilmItem extends CategoryFilm {
    boolean checked;

    public void setChecked(boolean checked) {
      this.checked = checked;
    }

    public boolean getChecked() {
      return this.checked;
    }
  }

  @Value("${upload.pathfilm}")
  private String uploadPathinFilm;

  public FilmEditControler(FilmRepository filmRepo, CategoryFilmRepository categoryFilmRepo) {
    this.filmRepo = filmRepo;
    this.filmService = new FilmService(this.filmRepo);
    this.categoryFilmRepo = categoryFilmRepo;
    this.categoryFilmService = new CategoryFilmService(this.categoryFilmRepo);
  }

  @GetMapping
  public String registerForm(
      @RequestParam(name = "id", required = false, defaultValue = "") String id, FilmForm filmForm,
      Model model) {

    if ("".equals(id))
      id = filmIdEdit;
    model.addAttribute("filmidedit", id);

    Film film = new Film();
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    } else {
      film = filmService.getFilm(id);
      filmNameEdit = film.getName();
      filmOriginalFilmNameEdit = film.getOriginalfilmname();
      filmYearOfManufactureEdit = film.getYearofmanufacture();
      filmDurationMinuteEdit = film.getDurationminute();
      filmProducerEdit = film.getProducer();
      filmIntotheroleEdit = film.getIntotherole();
      filmDescriptionEdit = film.getDescription();
      if (!"".equals(film.getPicture()))
        picture = film.getPicture();
    }
    model.addAttribute("filmnameedit", filmNameEdit);
    model.addAttribute("filmoriginalfilmnameedit", filmOriginalFilmNameEdit);
    model.addAttribute("filmyearofmanufactureedit", filmYearOfManufactureEdit);
    model.addAttribute("filmdurationminuteedit", filmDurationMinuteEdit);
    model.addAttribute("filmproduceredit", filmProducerEdit);
    model.addAttribute("filmintotheroleedit", filmIntotheroleEdit);
    model.addAttribute("filmdescriptionedit", filmDescriptionEdit);

    List<CategoryFilmItem> categoryFilmItems = new ArrayList<CategoryFilmItem>();
    for (CategoryFilm categoryFilmInit : categoryFilmService.getCategoryFilms()) {
      CategoryFilmItem categoryFilmItem = new CategoryFilmItem();
      categoryFilmItem.setId(categoryFilmInit.getId());
      categoryFilmItem.setName(categoryFilmInit.getName());
      if (film.getCategoryfilms().stream().filter(f -> f.getId() == categoryFilmInit.getId())
          .count() == 1) {
        categoryFilmItem.setChecked(true);
      } else {
        categoryFilmItem.setChecked(false);
      }
      categoryFilmItems.add(categoryFilmItem);
    }
    model.addAttribute("categoryfilms", categoryFilmItems);

    if (picture != null) {
      // model.addAttribute("file", uploadPathinFilm + "/" + picture);
      model.addAttribute("file", picture);
    }

    return "filmedit";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value,
      FilmForm filmForm, @RequestParam("file") MultipartFile file, Map<String, Object> model)
      throws IOException {

    errorMessages.clear();

    if ("filmbut#buttion#edit".equals(value)) {

      List<CategoryFilm> categoryfilms = new ArrayList<CategoryFilm>();
      filmForm.getCategoryfilms().stream()
          .forEach(f -> categoryfilms.add(categoryFilmRepo.findById(f)));
      filmForm.setGanres(categoryfilms);

      if (file != null && !file.getOriginalFilename().isEmpty()) {
        File uploadDir = new File(uploadPathinFilm);

        if (!uploadDir.exists()) {
          uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo(new File(uploadPathinFilm + "/" + resultFilename));
        filmForm.setPicture(resultFilename);
      }

      errorMessages.addAll(filmService.buttionEdit(filmForm, uploadPathinFilm));
      filmIdEdit = filmForm.getFilmidedit();
      filmNameEdit = filmForm.getFilmnameedit();
      filmOriginalFilmNameEdit = filmForm.getFilmoriginalfilmnameedit();
      filmYearOfManufactureEdit = filmForm.getFilmyearofmanufactureedit();
      filmDurationMinuteEdit = filmForm.getFilmdurationminuteedit();
      filmProducerEdit = filmForm.getFilmproduceredit();
      filmIntotheroleEdit = filmForm.getFilmintotheroleedit();
      filmDescriptionEdit = filmForm.getFilmdescriptionedit();
      // picture = filmForm.getPicture();

      if (filmForm.getPicture() != null)
        picture = filmForm.getPicture();

    }
    if ("filmbut#buttion#edit#cancel".equals(value))
      return "redirect:/film";

    if (errorMessages.isEmpty()) {
      filmNameEdit = "";
      filmIdEdit = "";
      return "redirect:/film";
    }


    return "redirect:/filmedit";
  }


}


