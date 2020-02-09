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
import ru.bestcham.cham.form.CategoryYoutubeForm;
import ru.bestcham.cham.repository.CategoryYoutubeRepository;
import ru.bestcham.cham.service.CategoryYoutubeService;

@Controller
@Data
@RequestMapping("/categoryyoutubeedit")
public class CategoryYoutubeEditControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();
  private String categoryYoutubeIdEdit;
  private String categoryYoutubeNameEdit;

  private CategoryYoutubeRepository categoryYoutubeRepo;
  private CategoryYoutubeService categoryYoutubeService;

  public CategoryYoutubeEditControler(CategoryYoutubeRepository categoryYoutubeRepo) {
    this.categoryYoutubeRepo = categoryYoutubeRepo;
    this.categoryYoutubeService = new CategoryYoutubeService(this.categoryYoutubeRepo);
  }
  
  @GetMapping
  public String registerForm(
      @RequestParam(name = "id", required = false, defaultValue = "") String id,
      CategoryYoutubeForm categoryYoutubeForm, Model model) {
    if ("".equals(id)) id = categoryYoutubeIdEdit;
    model.addAttribute("categoryyoutubeidedit", id);  

    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    } else {
      categoryYoutubeNameEdit = categoryYoutubeService.getCategoryYoutube(id).getName();
    }
    model.addAttribute("categoryyoutubenameedit", categoryYoutubeNameEdit);

    return "categoryyoutubeedit";
  }

  @PostMapping
  public String processCollapseShowDesign(
      @RequestParam(value = "myButtion[]") String value,
      CategoryYoutubeForm categoryYoutubeForm, Map<String, Object> model) {

    errorMessages.clear();

    if ("categoryyoutubebut#buttion#edit".equals(value)) {
      errorMessages.addAll(categoryYoutubeService.buttionEdit(categoryYoutubeForm.getCategoryyoutubeidedit(), categoryYoutubeForm.getCategoryyoutubenameedit()));
      categoryYoutubeIdEdit = categoryYoutubeForm.getCategoryyoutubeidedit();
      categoryYoutubeNameEdit = categoryYoutubeForm.getCategoryyoutubenameedit();
    }
    if ("categoryyoutubebut#buttion#edit#cancel".equals(value))
      return "redirect:/categoryyoutube";
    
    if (errorMessages.isEmpty()) {
      categoryYoutubeNameEdit = "";
      categoryYoutubeIdEdit = "";
      return "redirect:/categoryyoutube";
    }


    return "redirect:/categoryyoutubeedit";
  }

}
