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
import ru.bestcham.cham.form.CategorySyteForm;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.service.CategorySyteService;

@Slf4j
@Controller
@Data
@RequestMapping("/categorysyteadd")
public class CategorySyteAddControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();
  private String categorySyteNameAdd; 
  
  private CategorySyteRepository categorySyteRepo;
  private CategorySyteService categorySyteService;

  public CategorySyteAddControler(CategorySyteRepository categorySyteRepo) {
    this.categorySyteRepo = categorySyteRepo;
    this.categorySyteService = new CategorySyteService(this.categorySyteRepo);
  }
  
  @GetMapping
  public String registerForm(CategorySyteForm categorySyteForm, Model model) {
    
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    }
    
    if ("".equals(categorySyteNameAdd) || (categorySyteNameAdd == null)) categorySyteNameAdd = categorySyteForm.getCategorysytenameadd();
    model.addAttribute("categorysytenameadd", categorySyteNameAdd); 
    
    return "categorysyteadd";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, CategorySyteForm categorySyteForm, Map<String, Object> model) {
    
    errorMessages.clear();

    if ("categorysytebut#buttion#add".equals(value)) {
      errorMessages.addAll(categorySyteService.buttionAdd(categorySyteForm.getCategorysytenameadd()));
      categorySyteNameAdd = categorySyteForm.getCategorysytenameadd();
    }
    if ("categorysytebut#buttion#add#cancel".equals(value)) return "redirect:/categorysyte"; 
    if (errorMessages.isEmpty()) {
      categorySyteNameAdd = "";
      return "redirect:/categorysyte";
    }
  
    return "redirect:/categorysyteadd";
  }

}
