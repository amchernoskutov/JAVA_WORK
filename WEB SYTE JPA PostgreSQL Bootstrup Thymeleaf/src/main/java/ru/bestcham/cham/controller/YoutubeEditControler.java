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
import ru.bestcham.cham.domain.CategoryYoutube;
import ru.bestcham.cham.domain.Youtube;
import ru.bestcham.cham.form.YoutubeForm;
import ru.bestcham.cham.repository.CategoryYoutubeRepository;
import ru.bestcham.cham.repository.YoutubeRepository;
import ru.bestcham.cham.service.CategoryYoutubeService;
import ru.bestcham.cham.service.YoutubeService;

@Controller
@Data
@RequestMapping("/youtubeedit")
public class YoutubeEditControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();

  private String youtubeIdEdit;
  private String youtubeNameEdit;
  private String youtubeUrlEdit;
  private String youtubeDescriptionEdit;
  private String picture; 

  private YoutubeRepository youtubeRepo;
  private YoutubeService youtubeService;
  private CategoryYoutubeRepository categoryYoutubeRepo;
  private CategoryYoutubeService categoryYoutubeService;

  private class CategoryYoutubeItem extends CategoryYoutube {
    boolean checked;

    public void setChecked(boolean checked) {
      this.checked = checked;
    }

    public boolean getChecked() {
      return this.checked;
    }
  }
  
  @Value("${upload.pathyoutube}")
  private String uploadPathinYoutube;

  public YoutubeEditControler(YoutubeRepository youtubeRepo, CategoryYoutubeRepository categoryYoutubeRepo) {
    this.youtubeRepo = youtubeRepo;
    this.youtubeService = new YoutubeService(this.youtubeRepo);
    this.categoryYoutubeRepo = categoryYoutubeRepo;
    this.categoryYoutubeService = new CategoryYoutubeService(this.categoryYoutubeRepo);
  }
  
  @GetMapping
  public String registerForm(
      @RequestParam(name = "id", required = false, defaultValue = "") String id,
      YoutubeForm youtubeForm, Model model) {

    if ("".equals(id))
      id = youtubeIdEdit;
    model.addAttribute("youtubeidedit", id);

    Youtube youtube = new Youtube();
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    } else {
      youtube = youtubeService.getYoutube(id);
      youtubeNameEdit = youtube.getName();
      youtubeUrlEdit = youtube.getUrl();
      youtubeDescriptionEdit = youtube.getDescription();
      if (!"".equals(youtube.getPicture()))
        picture = youtube.getPicture();
    }
    model.addAttribute("youtubenameedit", youtubeNameEdit);
    model.addAttribute("youtubeurledit", youtubeUrlEdit);
    model.addAttribute("youtubedescriptionedit", youtubeDescriptionEdit);

    List<CategoryYoutubeItem> categoryYoutubeItems = new ArrayList<CategoryYoutubeItem>();
    for (CategoryYoutube categoryYoutubeInit : categoryYoutubeService.getCategoryYoutubes()) {
      CategoryYoutubeItem categoryYoutubeItem = new CategoryYoutubeItem();
      categoryYoutubeItem.setId(categoryYoutubeInit.getId());
      categoryYoutubeItem.setName(categoryYoutubeInit.getName());
      if (youtube.getCategoryyoutubes().stream().filter(f -> f.getId() == categoryYoutubeInit.getId())
          .count() == 1) {
        categoryYoutubeItem.setChecked(true);
      } else {
        categoryYoutubeItem.setChecked(false);
      }
      categoryYoutubeItems.add(categoryYoutubeItem);
    }
    model.addAttribute("categoryyoutubes", categoryYoutubeItems);

    if (picture != null) {
      // model.addAttribute("file", uploadPathinYoutube + "/" + picture);
      model.addAttribute("file", picture);
    }

    return "youtubeedit";
  }

  @PostMapping
  public String processCollapseShowDesign(
      @RequestParam(value = "myButtion[]") String value,
      YoutubeForm youtubeForm, @RequestParam("file") MultipartFile file, Map<String, Object> model) throws IOException {

    errorMessages.clear();

    if ("youtubebut#buttion#edit".equals(value)) {

      List<CategoryYoutube> categoryyoutubes = new ArrayList<CategoryYoutube>();
      youtubeForm.getCategoryyoutubes().stream()
          .forEach(f -> categoryyoutubes.add(categoryYoutubeRepo.findById(f)));
      youtubeForm.setGanres(categoryyoutubes);

      if (file != null && !file.getOriginalFilename().isEmpty()) {
        File uploadDir = new File(uploadPathinYoutube);

        if (!uploadDir.exists()) {
          uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo(new File(uploadPathinYoutube + "/" + resultFilename));
        youtubeForm.setPicture(resultFilename);
      }

      errorMessages.addAll(youtubeService.buttionEdit(youtubeForm, uploadPathinYoutube));
      youtubeIdEdit = youtubeForm.getYoutubeidedit();
      youtubeNameEdit = youtubeForm.getYoutubenameedit();
      youtubeUrlEdit = youtubeForm.getYoutubeurledit();
      youtubeDescriptionEdit = youtubeForm.getYoutubedescriptionedit();
      // picture = youtubeForm.getPicture();

      if (youtubeForm.getPicture() != null)
        picture = youtubeForm.getPicture();

    }
    if ("youtubebut#buttion#edit#cancel".equals(value))
      return "redirect:/youtube";

    if (errorMessages.isEmpty()) {
      youtubeNameEdit = "";
      youtubeIdEdit = "";
      return "redirect:/youtube";
    }

    return "redirect:/youtubeedit";
  }

}
