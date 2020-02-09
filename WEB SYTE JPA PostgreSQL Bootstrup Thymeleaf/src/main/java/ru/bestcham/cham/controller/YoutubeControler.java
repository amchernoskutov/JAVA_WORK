package ru.bestcham.cham.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.form.YoutubeForm;
import ru.bestcham.cham.repository.YoutubeRepository;
import ru.bestcham.cham.service.YoutubeService;

@Slf4j
@Controller
@Data
@RequestMapping("/youtube")
public class YoutubeControler {
  @Value("${upload.pathyoutube}")
  private String uploadPathYoutube;

  private YoutubeRepository youtubeRepo;
  private YoutubeService youtubeService;

  public YoutubeControler(YoutubeRepository youtubeRepo) {
    this.youtubeRepo = youtubeRepo;
    this.youtubeService = new YoutubeService(this.youtubeRepo);
  }

  @GetMapping
  public String registerForm(YoutubeForm youtubeForm, Model model) {
    model.addAttribute("youtubes", youtubeService.getYoutubes());
    
    return "youtube";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, Map<String, Object> model) {
    
    if (value.contains("youtube#add*")) { 
      return "redirect:/youtubeadd";
    }
    if (value.contains("youtube#edit*")) {
      return "redirect:/youtubeedit"+youtubeService.getParamId(value);
    }
    if (value.contains("youtube#delete*")) youtubeService.buttionDelete(value, uploadPathYoutube);
    
    return "redirect:/youtube";
  }

}
