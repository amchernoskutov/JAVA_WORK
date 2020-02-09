package com.rgd.controller;

import java.util.HashSet;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import com.rgd.form.ReportParamForm;
import com.rgd.repository.CustomerRepository;

@SessionAttributes(types = ReportParamForm.class)
@Controller
public class ReportParamController {
  private CustomerRepository customerRepo;
  private HashSet<String> errors;

  public ReportParamController(CustomerRepository customerRepo) {
    this.customerRepo = customerRepo;
  }

  @GetMapping("/reportparam")
  public String getOrderForm(ReportParamForm reportForm, Model model) {
    model.addAttribute(reportForm);

    model.addAttribute("errormessages", errors);
    model.addAttribute("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString());
    model.addAttribute("reportform", reportForm);
    model.addAttribute("customers", customerRepo.findAll());

    return "reportparam";
  }

  @PostMapping("/reportparam")
  public String processOrderForm(@RequestParam(value = "myButtion[]") String value,
      ReportParamForm reportForm, Model model, SessionStatus status) {
    errors = new HashSet<String>();

    if (value.contains("reportparam")) {
      if (reportForm.getFirstdate() == null) {
        errors.add("Начальная дата покупки не может быть пустой");
      }
      if (reportForm.getSeconddate() == null) {
        errors.add("Конечная дата покупки не может быть пустой");
      }
      if (reportForm.getFirstdate().isAfter(reportForm.getSeconddate())) {
        errors.add("Начальная дата не может быть больше конечной");
      }
      if (reportForm.getIdcustomer() == 0) {
        errors.add("Не задан покупатель");
      }
    }

    if (!errors.isEmpty()) {
      Assert.notNull(reportForm);
      return "redirect:/reportparam";
    } else {
      return "redirect:/report";
    }
  }

}
