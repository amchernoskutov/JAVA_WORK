package ru.bestcham.cham.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.domain.CategoryFilm;
import ru.bestcham.cham.domain.CategoryYoutube;
import ru.bestcham.cham.form.FilmForm;
import ru.bestcham.cham.form.YoutubeForm;
import ru.bestcham.cham.repository.CategoryFilmRepository;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.repository.CategoryYoutubeRepository;
import ru.bestcham.cham.repository.FilmRepository;
import ru.bestcham.cham.repository.YoutubeRepository;
import ru.bestcham.cham.repository.СategoryMenuRepository;
import ru.bestcham.cham.service.CategoryFilmService;
import ru.bestcham.cham.service.CategorySyteService;
import ru.bestcham.cham.service.CategoryYoutubeService;
import ru.bestcham.cham.service.FilmService;
import ru.bestcham.cham.service.YoutubeService;

@Slf4j
@Controller
@Data
@RequestMapping("/youtubeadd")
public class YoutubeAddControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();

  private String youtubeNameAdd; 
  private String youtubeUrlAdd;
  private String youtubeDescriptionAdd;
  private String picture; 

  private YoutubeRepository youtubeRepo;
  private YoutubeService youtubeService;
  private CategoryYoutubeRepository categoryYoutubeRepo;
  private CategoryYoutubeService categoryYoutubeService;
  private СategoryMenuRepository categoryMenuRepo;

  @Value("${upload.pathyoutube}")
  private String uploadPathYoutube;

  public YoutubeAddControler(YoutubeRepository youtubeRepo, CategoryYoutubeRepository categoryYoutubeRepo, СategoryMenuRepository categoryMenuRepo) {
    this.youtubeRepo = youtubeRepo;
    this.youtubeService = new YoutubeService(this.youtubeRepo);
    this.categoryYoutubeRepo = categoryYoutubeRepo;
    this.categoryYoutubeService = new CategoryYoutubeService(this.categoryYoutubeRepo);
    this.categoryMenuRepo = categoryMenuRepo;
  }
  
  @GetMapping
  public String registerForm(YoutubeForm youtubeForm, Model model) {
    
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    }
    
    if ("".equals(youtubeNameAdd) || (youtubeNameAdd == null)) youtubeNameAdd = youtubeForm.getYoutubenameadd();
    model.addAttribute("youtubenameadd", youtubeNameAdd); 
    
    if ("".equals(youtubeUrlAdd) || (youtubeUrlAdd == null)) youtubeUrlAdd = youtubeForm.getYoutubeurladd();
    model.addAttribute("youtubeurladd", youtubeUrlAdd); 

    if ("".equals(youtubeDescriptionAdd) || (youtubeDescriptionAdd == null)) youtubeDescriptionAdd = youtubeForm.getYoutubedescriptionadd();
    model.addAttribute("youtubedescriptionadd", youtubeDescriptionAdd); 

    model.addAttribute("categoryyoutubes", categoryYoutubeService.getCategoryYoutubes());
    
    
    return "youtubeadd";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, 
      @RequestParam("file") MultipartFile file,  
      YoutubeForm youtubeForm, Map<String, Object> model) throws IOException {
    
    errorMessages.clear();

    if ("youtubebut#buttion#add".equals(value)) {
      youtubeForm.setCategoryMenu(categoryMenuRepo.findByNameIgnoreCase("Youtube каналы"));

      List<CategoryYoutube> categoryyoutubes = new ArrayList<CategoryYoutube>(); 
      youtubeForm.getCategoryyoutubes().stream().forEach(f -> categoryyoutubes.add(categoryYoutubeRepo.findById(f)));
      youtubeForm.setGanres(categoryyoutubes);
      
      if (file != null && !file.getOriginalFilename().isEmpty()) {
        File uploadDir = new File(uploadPathYoutube);

        if (!uploadDir.exists()) {
          uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo((new File(uploadPathYoutube + "/" + resultFilename)));

        youtubeForm.setPicture(resultFilename);
      }
      
      errorMessages.addAll(youtubeService.buttionAdd(youtubeForm));
      youtubeNameAdd = youtubeForm.getYoutubenameadd();
    }
    if ("youtubebut#buttion#add#cancel".equals(value)) return "redirect:/youtube"; 
    if (errorMessages.isEmpty()) {
      youtubeNameAdd = "";
      return "redirect:/youtube";
    }
  
    return "redirect:/youtubeadd";
  }

}
