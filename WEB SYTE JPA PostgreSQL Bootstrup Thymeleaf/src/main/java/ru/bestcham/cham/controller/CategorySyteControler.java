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
import ru.bestcham.cham.form.CategorySyteForm;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.service.CategorySyteService;

@Slf4j
@Controller
@Data
@RequestMapping("/categorysyte")
public class CategorySyteControler {
  private CategorySyteRepository categorySyteRepo;
  private CategorySyteService categorySyteService;

  public CategorySyteControler(CategorySyteRepository categorySyteRepo) {
    this.categorySyteRepo = categorySyteRepo;
    this.categorySyteService = new CategorySyteService(this.categorySyteRepo);
  }

  @GetMapping
  public String registerForm(CategorySyteForm categorySyteForm, Model model) {
    model.addAttribute("categorysytes", categorySyteService.getCategorySytes());
    
    return "categorysyte";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, Map<String, Object> model) {
    
    if (value.contains("categorysyte#add*")) { 
      return "redirect:/categorysyteadd";
    }
    if (value.contains("categorysyte#edit*")) {
      return "redirect:/categorysyteedit"+categorySyteService.getParamId(value);
    }
    if (value.contains("categorysyte#delete*")) categorySyteService.buttionDelete(value);
    if (value.contains("categorysyte#init")) categorySyteService.InitCategorySyte();
    
    return "redirect:/categorysyte";
  }

}
