package ru.bestcham.cham.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.form.CategoryFilmForm;
import ru.bestcham.cham.repository.CategoryFilmRepository;
import ru.bestcham.cham.service.CategoryFilmService;

@Slf4j
@Controller
@Data
@RequestMapping("/categoryfilm")
public class CategoryFilmControler {
  private CategoryFilmRepository categoryFilmRepo;
  private CategoryFilmService categoryFilmService;

  public CategoryFilmControler(CategoryFilmRepository categoryFilmRepo) {
    this.categoryFilmRepo = categoryFilmRepo;
    this.categoryFilmService = new CategoryFilmService(this.categoryFilmRepo);
  }

  @GetMapping
  public String registerForm(CategoryFilmForm categoryFilmForm, Model model) {
    model.addAttribute("categoryfilms", categoryFilmService.getCategoryFilms());
    
    return "categoryfilm";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, Map<String, Object> model) {
    
    if (value.contains("categoryfilm#add*")) { 
      return "redirect:/categoryfilmadd";
    }
    if (value.contains("categoryfilm#edit*")) {
      return "redirect:/categoryfilmedit"+categoryFilmService.getParamId(value);
    }
    if (value.contains("categoryfilm#delete*")) categoryFilmService.buttionDelete(value);
    if (value.contains("categoryfilm#init")) categoryFilmService.InitCategoryFilm();
    
    return "redirect:/categoryfilm";
  }

}
