package ru.bestcham.cham.controller;

import java.util.ArrayList;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.Data;
import ru.bestcham.cham.form.CategoryFilmForm;
import ru.bestcham.cham.repository.CategoryFilmRepository;
import ru.bestcham.cham.service.CategoryFilmService;

@Controller
@Data
@RequestMapping("/categoryfilmedit")
public class CategoryFilmEditControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();
  private String categoryFilmIdEdit;
  private String categoryFilmNameEdit;

  private CategoryFilmRepository categoryFilmRepo;
  private CategoryFilmService categoryFilmService;

  public CategoryFilmEditControler(CategoryFilmRepository categoryFilmRepo) {
    this.categoryFilmRepo = categoryFilmRepo;
    this.categoryFilmService = new CategoryFilmService(this.categoryFilmRepo);
  }

  @GetMapping
  public String registerForm(
      @RequestParam(name = "id", required = false, defaultValue = "") String id,
      CategoryFilmForm categoryFilmForm, Model model) {
    if ("".equals(id)) id = categoryFilmIdEdit;
    model.addAttribute("categoryfilmidedit", id);  

    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    } else {
      categoryFilmNameEdit = categoryFilmService.getCategoryFilm(id).getName();
    }
    model.addAttribute("categoryfilmnameedit", categoryFilmNameEdit);

    return "categoryfilmedit";
  }

  @PostMapping
  public String processCollapseShowDesign(
      @RequestParam(value = "myButtion[]") String value,
      CategoryFilmForm categoryFilmForm, Map<String, Object> model) {

    errorMessages.clear();

    if ("categoryfilmbut#buttion#edit".equals(value)) {
      errorMessages.addAll(categoryFilmService.buttionEdit(categoryFilmForm.getCategoryfilmidedit(), categoryFilmForm.getCategoryfilmnameedit()));
      categoryFilmIdEdit = categoryFilmForm.getCategoryfilmidedit();
      categoryFilmNameEdit = categoryFilmForm.getCategoryfilmnameedit();
    }
    if ("categoryfilmbut#buttion#edit#cancel".equals(value))
      return "redirect:/categoryfilm";
    
    if (errorMessages.isEmpty()) {
      categoryFilmNameEdit = "";
      categoryFilmIdEdit = "";
      return "redirect:/categoryfilm";
    }


    return "redirect:/categoryfilmedit";
  }

}
