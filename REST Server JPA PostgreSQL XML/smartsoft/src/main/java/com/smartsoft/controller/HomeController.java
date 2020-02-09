package com.smartsoft.controller;

import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.smartsoft.repository.CustomerRepository;
import com.smartsoft.repository.ShoppinglistRepository;
import com.smartsoft.service.ReportService;

@Controller
public class HomeController {
  private ShoppinglistRepository shoppinglistRepo;
  private CustomerRepository customerRepo;
  private ReportService reportService;

  public HomeController(ShoppinglistRepository shoppinglistRepo, CustomerRepository customerRepo, ReportService reportService) {
    this.shoppinglistRepo = shoppinglistRepo;
    this.reportService = new ReportService(this.shoppinglistRepo, this.customerRepo);
  }

  @GetMapping("/home")
  public String senDnone(Map<String, Object> model) {
    model.put("admindnone", SecurityContextHolder.getContext().getAuthentication().getName().toString());
    model.put("shoplists", reportService.getReport());
    model.put("sumamount", String.format("%4.2f",reportService.getReport().stream().mapToDouble(f -> f.getAmount().doubleValue()).sum()));
    
    return "home";
  }
}