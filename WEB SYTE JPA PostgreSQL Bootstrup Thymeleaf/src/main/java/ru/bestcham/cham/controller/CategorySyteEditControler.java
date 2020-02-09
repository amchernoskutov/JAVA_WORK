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
import ru.bestcham.cham.form.CategorySyteForm;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.service.CategorySyteService;

@Controller
@Data
@RequestMapping("/categorysyteedit")
public class CategorySyteEditControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();
  private String categorySyteIdEdit;
  private String categorySyteNameEdit;

  private CategorySyteRepository categorySyteRepo;
  private CategorySyteService categorySyteService;

  public CategorySyteEditControler(CategorySyteRepository categorySyteRepo) {
    this.categorySyteRepo = categorySyteRepo;
    this.categorySyteService = new CategorySyteService(this.categorySyteRepo);
  }

  @GetMapping
  public String registerForm(
      @RequestParam(name = "id", required = false, defaultValue = "") String id,
      CategorySyteForm categorySyteForm, Model model) {
    if ("".equals(id)) id = categorySyteIdEdit;
    model.addAttribute("categorysyteidedit", id);  

    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    } else {
      categorySyteNameEdit = categorySyteService.getCategorySyte(id).getName();
    }
    model.addAttribute("categorysytenameedit", categorySyteNameEdit);

    return "categorysyteedit";
  }

  @PostMapping
  public String processCollapseShowDesign(
      @RequestParam(value = "myButtion[]") String value,
      CategorySyteForm categorySyteForm, Map<String, Object> model) {

    errorMessages.clear();

    if ("categorysytebut#buttion#edit".equals(value)) {
      errorMessages.addAll(categorySyteService.buttionEdit(categorySyteForm.getCategorysyteidedit(), categorySyteForm.getCategorysytenameedit()));
      categorySyteIdEdit = categorySyteForm.getCategorysyteidedit();
      categorySyteNameEdit = categorySyteForm.getCategorysytenameedit();
    }
    if ("categorysytebut#buttion#edit#cancel".equals(value))
      return "redirect:/categorysyte";
    
    if (errorMessages.isEmpty()) {
      categorySyteNameEdit = "";
      categorySyteIdEdit = "";
      return "redirect:/categorysyte";
    }


    return "redirect:/categorysyteedit";
  }

}
