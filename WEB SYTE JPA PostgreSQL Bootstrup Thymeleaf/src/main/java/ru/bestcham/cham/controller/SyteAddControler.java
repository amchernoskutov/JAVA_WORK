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
import ru.bestcham.cham.domain.CategorySyte;
import ru.bestcham.cham.form.SyteForm;
import ru.bestcham.cham.repository.CategorySyteRepository;
import ru.bestcham.cham.repository.SyteRepository;
import ru.bestcham.cham.repository.СategoryMenuRepository;
import ru.bestcham.cham.service.CategorySyteService;
import ru.bestcham.cham.service.SyteService;

@Slf4j
@Controller
@Data
@RequestMapping("/syteadd")
public class SyteAddControler {
  private ArrayList<String> errorMessages = new ArrayList<String>();

  private String syteNameAdd; 
  private String syteUrlAdd;
  private String syteDescriptionAdd;
  private String picture; 
  
  private SyteRepository syteRepo;
  private SyteService syteService;
  private CategorySyteRepository categorySyteRepo;
  private CategorySyteService categorySyteService;
  private СategoryMenuRepository categoryMenuRepo;
  
  @Value("${upload.pathsyte}")
  private String uploadPathSyte;

  public SyteAddControler(SyteRepository syteRepo, CategorySyteRepository categorySyteRepo, СategoryMenuRepository categoryMenuRepo) {
    this.syteRepo = syteRepo;
    this.syteService = new SyteService(this.syteRepo);
    this.categorySyteRepo = categorySyteRepo;
    this.categorySyteService = new CategorySyteService(this.categorySyteRepo);
    this.categoryMenuRepo = categoryMenuRepo;
  }

  @GetMapping
  public String registerForm(SyteForm syteForm, Model model) {
    
    if (!errorMessages.isEmpty()) {
      model.addAttribute("errormessages", errorMessages);
    }
    
    if ("".equals(syteNameAdd) || (syteNameAdd == null)) syteNameAdd = syteForm.getSytenameadd();
    model.addAttribute("sytenameadd", syteNameAdd); 
    
    if ("".equals(syteUrlAdd) || (syteUrlAdd == null)) syteUrlAdd = syteForm.getSyteurladd();
    model.addAttribute("syteurladd", syteUrlAdd); 

    if ("".equals(syteDescriptionAdd) || (syteDescriptionAdd == null)) syteDescriptionAdd = syteForm.getSytedescriptionadd();
    model.addAttribute("sytedescriptionadd", syteDescriptionAdd); 

    model.addAttribute("categorysytes", categorySyteService.getCategorySytes());

    return "syteadd";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, 
      @RequestParam("file") MultipartFile file,  
      SyteForm syteForm, Map<String, Object> model) throws IOException {
    
    errorMessages.clear();

    if ("sytebut#buttion#add".equals(value)) {
      syteForm.setCategoryMenu(categoryMenuRepo.findByNameIgnoreCase("Сайты"));

      List<CategorySyte> categorysytes = new ArrayList<CategorySyte>(); 
      syteForm.getCategorysytes().stream().forEach(f -> categorysytes.add(categorySyteRepo.findById(f)));
      syteForm.setGanres(categorysytes);
      
      if (file != null && !file.getOriginalFilename().isEmpty()) {
        File uploadDir = new File(uploadPathSyte);

        if (!uploadDir.exists()) {
          uploadDir.mkdir();
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + file.getOriginalFilename();

        file.transferTo((new File(uploadPathSyte + "/" + resultFilename)));

        syteForm.setPicture(resultFilename);
      }
      
      errorMessages.addAll(syteService.buttionAdd(syteForm));
      syteNameAdd = syteForm.getSytenameadd();
    }
    if ("sytebut#buttion#add#cancel".equals(value)) return "redirect:/syte"; 
    if (errorMessages.isEmpty()) {
      syteNameAdd = "";
      return "redirect:/syte";
    }
  
    return "redirect:/syteadd";
  }

}
