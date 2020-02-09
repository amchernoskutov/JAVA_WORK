package ru.bestcham.cham.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.Data;

@Controller
@Data
@RequestMapping("/design")
public class DesignController {
  
  @GetMapping
  public String registerForm(Model model) {
    return "design";
  }

  @PostMapping
  public String processCollapseShowDesign(Map<String, Object> model) {
    return "design";
  }
  
}
