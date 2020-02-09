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
import ru.bestcham.cham.form.SyteForm;
import ru.bestcham.cham.repository.SyteRepository;
import ru.bestcham.cham.service.SyteService;

@Slf4j
@Controller
@Data
@RequestMapping("/syte")
public class SyteControler {
  @Value("${upload.pathsyte}")
  private String uploadPathSyte;

  private SyteRepository syteRepo;
  private SyteService syteService;

  public SyteControler(SyteRepository syteRepo) {
    this.syteRepo = syteRepo;
    this.syteService = new SyteService(this.syteRepo);
  }

  @GetMapping
  public String registerForm(SyteForm syteForm, Model model) {
    model.addAttribute("sytes", syteService.getSytes());
    
    return "syte";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value, Map<String, Object> model) {
    
    if (value.contains("syte#add*")) { 
      return "redirect:/syteadd";
    }
    if (value.contains("syte#edit*")) {
      return "redirect:/syteedit"+syteService.getParamId(value);
    }
    if (value.contains("syte#delete*")) syteService.buttionDelete(value, uploadPathSyte);
    
    return "redirect:/syte";
  }

}
