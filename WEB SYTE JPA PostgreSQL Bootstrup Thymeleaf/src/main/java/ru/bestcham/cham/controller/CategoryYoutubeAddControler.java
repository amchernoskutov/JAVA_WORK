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
@RequestMapping("/categoryyoutubeadd")
public class CategoryYoutubeAddControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();
  private String categoryYoutubeNameAdd; 
  
  private CategoryYoutubeRepository categoryYoutubeRepo;
  private CategoryYoutubeService categoryYoutubeService;

  public CategoryYoutubeAddControler(CategoryYoutubeRepository categoryYoutubeRepo) {
    this.categoryYoutubeRepo = categoryYoutubeRepo;
    this.categoryYoutubeService = new CategoryYoutubeService(this.categoryYoutubeRepo);
  }
  
  @GetMapping
  public String registerForm(CategoryYoutubeForm categoryYoutubeForm, Model model) {
    
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    }
    
    if ("".equals(categoryYoutubeNameAdd) || (categoryYoutubeNameAdd == null)) categoryYoutubeNameAdd = categoryYoutubeForm.getCategoryyoutubenameadd();
    model.addAttribute("categoryyoutubenameadd", categoryYoutubeNameAdd); 
    
    return "categoryyoutubeadd";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, CategoryYoutubeForm categoryYoutubeForm, Map<String, Object> model) {
    
    errorMessages.clear();

    if ("categoryyoutubebut#buttion#add".equals(value)) {
      errorMessages.addAll(categoryYoutubeService.buttionAdd(categoryYoutubeForm.getCategoryyoutubenameadd()));
      categoryYoutubeNameAdd = categoryYoutubeForm.getCategoryyoutubenameadd();
    }
    if ("categoryyoutubebut#buttion#add#cancel".equals(value)) return "redirect:/categoryyoutube"; 
    if (errorMessages.isEmpty()) {
      categoryYoutubeNameAdd = "";
      return "redirect:/categoryyoutube";
    }
  
    return "redirect:/categoryyoutubeadd";
  }

}
