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
import ru.bestcham.cham.domain.CategorySyte;
import ru.bestcham.cham.domain.Syte;
import ru.bestcham.cham.form.SyteForm;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.repository.SyteRepository;
import ru.bestcham.cham.service.CategorySyteService;
import ru.bestcham.cham.service.SyteService;

@Controller
@Data
@RequestMapping("/syteedit")
public class SyteEditControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();

  private String syteIdEdit;
  private String syteNameEdit;
  private String syteUrlEdit;
  private String syteDescriptionEdit;
  private String picture; 

  private SyteRepository syteRepo;
  private SyteService syteService;
  private CategorySyteRepository categorySyteRepo;
  private CategorySyteService categorySyteService;

  private class CategorySyteItem extends CategorySyte {
    boolean checked;

    public void setChecked(boolean checked) {
      this.checked = checked;
    }

    public boolean getChecked() {
      return this.checked;
    }
  }
  
  @Value("${upload.pathsyte}")
  private String uploadPathinSyte;

  public SyteEditControler(SyteRepository syteRepo, CategorySyteRepository categorySyteRepo) {
    this.syteRepo = syteRepo;
    this.syteService = new SyteService(this.syteRepo);
    this.categorySyteRepo = categorySyteRepo;
    this.categorySyteService = new CategorySyteService(this.categorySyteRepo);
  }

  @GetMapping
  public String registerForm(
      @RequestParam(name = "id", required = false, defaultValue = "") String id,
      SyteForm syteForm, Model model) {

    if ("".equals(id))
      id = syteIdEdit;
    model.addAttribute("syteidedit", id);

    Syte syte = new Syte();
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    } else {
      syte = syteService.getSyte(id);
      syteNameEdit = syte.getName();
      syteUrlEdit = syte.getUrl();
      syteDescriptionEdit = syte.getDescription();
      if (!"".equals(syte.getPicture()))
        picture = syte.getPicture();
    }
    model.addAttribute("sytenameedit", syteNameEdit);
    model.addAttribute("syteurledit", syteUrlEdit);
    model.addAttribute("sytedescriptionedit", syteDescriptionEdit);

    List<CategorySyteItem> categorySyteItems = new ArrayList<CategorySyteItem>();
    for (CategorySyte categorySyteInit : categorySyteService.getCategorySytes()) {
      CategorySyteItem categorySyteItem = new CategorySyteItem();
      categorySyteItem.setId(categorySyteInit.getId());
      categorySyteItem.setName(categorySyteInit.getName());
      if (syte.getCategorysytes().stream().filter(f -> f.getId() == categorySyteInit.getId())
          .count() == 1) {
        categorySyteItem.setChecked(true);
      } else {
        categorySyteItem.setChecked(false);
      }
      categorySyteItems.add(categorySyteItem);
    }
    model.addAttribute("categorysytes", categorySyteItems);

    if (picture != null) {
      // model.addAttribute("file", uploadPathinSyte + "/" + picture);
      model.addAttribute("file", picture);
    }

    return "syteedit";
  }

  @PostMapping
  public String processCollapseShowDesign(
      @RequestParam(value = "myButtion[]") String value,
      SyteForm syteForm, @RequestParam("file") MultipartFile file, Map<String, Object> model) throws IOException {

    errorMessages.clear();

    if ("sytebut#buttion#edit".equals(value)) {

      List<CategorySyte> categorysytes = new ArrayList<CategorySyte>();
      syteForm.getCategorysytes().stream()
          .forEach(f -> categorysytes.add(categorySyteRepo.findById(f)));
      syteForm.setGanres(categorysytes);

      if (file != null && !file.getOriginalFilename().isEmpty()) {
        File uploadDir = new File(uploadPathinSyte);

        if (!uploadDir.exists()) {
          uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo(new File(uploadPathinSyte + "/" + resultFilename));
        syteForm.setPicture(resultFilename);
      }

      errorMessages.addAll(syteService.buttionEdit(syteForm, uploadPathinSyte));
      syteIdEdit = syteForm.getSyteidedit();
      syteNameEdit = syteForm.getSytenameedit();
      syteUrlEdit = syteForm.getSyteurledit();
      syteDescriptionEdit = syteForm.getSytedescriptionedit();
      // picture = syteForm.getPicture();

      if (syteForm.getPicture() != null)
        picture = syteForm.getPicture();

    }
    if ("sytebut#buttion#edit#cancel".equals(value))
      return "redirect:/syte";

    if (errorMessages.isEmpty()) {
      syteNameEdit = "";
      syteIdEdit = "";
      return "redirect:/syte";
    }

    return "redirect:/syteedit";
  }

}
