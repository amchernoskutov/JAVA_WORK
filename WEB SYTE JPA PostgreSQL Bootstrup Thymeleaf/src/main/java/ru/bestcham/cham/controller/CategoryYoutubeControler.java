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
import ru.bestcham.cham.form.CategoryYoutubeForm;
import ru.bestcham.cham.repository.CategoryYoutubeRepository;
import ru.bestcham.cham.service.CategoryYoutubeService;

@Slf4j
@Controller
@Data
@RequestMapping("/categoryyoutube")
public class CategoryYoutubeControler {
  private CategoryYoutubeRepository categoryYoutubeRepo;
  private CategoryYoutubeService categoryYoutubeService;

  public CategoryYoutubeControler(CategoryYoutubeRepository categoryYoutubeRepo) {
    this.categoryYoutubeRepo = categoryYoutubeRepo;
    this.categoryYoutubeService = new CategoryYoutubeService(this.categoryYoutubeRepo);
  }

  @GetMapping
  public String registerForm(CategoryYoutubeForm categoryYoutubeForm, Model model) {
    model.addAttribute("categoryyoutubes", categoryYoutubeService.getCategoryYoutubes());
    
    return "categoryyoutube";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, Map<String, Object> model) {
    
    if (value.contains("categoryyoutube#add*")) { 
      return "redirect:/categoryyoutubeadd";
    }
    if (value.contains("categoryyoutube#edit*")) {
      return "redirect:/categoryyoutubeedit"+categoryYoutubeService.getParamId(value);
    }
    if (value.contains("categoryyoutube#delete*")) categoryYoutubeService.buttionDelete(value);
    if (value.contains("categoryyoutube#init")) categoryYoutubeService.InitCategoryYoutube();
    
    return "redirect:/categoryyoutube";
  }

}
