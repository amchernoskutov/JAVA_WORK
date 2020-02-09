package com.rgd.controller;

import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/index")
  public String senDnone(Map<String, Object> model) {
    model.put("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString() + " " + 
        SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
    return "index";
  }


}
