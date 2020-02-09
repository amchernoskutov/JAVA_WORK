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
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.form.CategoryFilmForm;
import ru.bestcham.cham.repository.CategoryFilmRepository;
import ru.bestcham.cham.service.CategoryFilmService;

@Slf4j
@Controller
@Data
@RequestMapping("/categoryfilmadd")
public class CategoryFilmAddControler {

  private ArrayList<String> errorMessages = new ArrayList<String>();
  private String categoryFilmNameAdd; 
  
  private CategoryFilmRepository categoryFilmRepo;
  private CategoryFilmService categoryFilmService;

  public CategoryFilmAddControler(CategoryFilmRepository categoryFilmRepo) {
    this.categoryFilmRepo = categoryFilmRepo;
    this.categoryFilmService = new CategoryFilmService(this.categoryFilmRepo);
  }
  
  @GetMapping
  public String registerForm(CategoryFilmForm categoryFilmForm, Model model) {
    
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    }
    
    if ("".equals(categoryFilmNameAdd) || (categoryFilmNameAdd == null)) categoryFilmNameAdd = categoryFilmForm.getCategoryfilmnameadd();
    model.addAttribute("categoryfilmnameadd", categoryFilmNameAdd); 

    return "categoryfilmadd";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, CategoryFilmForm categoryFilmForm, Map<String, Object> model) {
    
    errorMessages.clear();

    if ("categoryfilmbut#buttion#add".equals(value)) {
      errorMessages.addAll(categoryFilmService.buttionAdd(categoryFilmForm.getCategoryfilmnameadd()));
      categoryFilmNameAdd = categoryFilmForm.getCategoryfilmnameadd();
    }
    if ("categoryfilmbut#buttion#add#cancel".equals(value)) return "redirect:/categoryfilm"; 
    if (errorMessages.isEmpty()) {
      categoryFilmNameAdd = "";
      return "redirect:/categoryfilm";
    }
  
    return "redirect:/categoryfilmadd";
  }

}
