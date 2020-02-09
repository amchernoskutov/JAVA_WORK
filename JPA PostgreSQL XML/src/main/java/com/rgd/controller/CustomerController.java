package com.rgd.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.rgd.form.CustomerForm;
import com.rgd.repository.CustomerRepository;
import com.rgd.service.CustomerService;
import com.rgd.service.XsdValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerController {
  private CustomerRepository customerRepo;
  private CustomerService customerService;
  
  @Value("classpath:xsd/checkcustomer.xsd")
  private Resource resourceXSDFile;

  public CustomerController(CustomerRepository customerRepo) {
    this.customerRepo = customerRepo;
    this.customerService = new CustomerService(this.customerRepo);
  }

  @GetMapping
  public String senDnone(CustomerForm customerForm, Map<String, Object> model) {
    model.put("customers", customerRepo.findAll());
    model.put("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString());
    return "customer";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value,
      @RequestParam("myfileinput") MultipartFile multipartFile, Map<String, Object> model) {

    if (value.contains("customerinit")) {
      var errors = new HashSet<String>();
      try {
        File file = XsdValidator.convertMultiPartToFile(multipartFile);

        errors.addAll(XsdValidator.xsdValidatorExec(file,  resourceXSDFile));
        if (errors.isEmpty()) {
          if (!customerService.exec(file))
            errors.add("Ошибка записи в базу данных.");
        }
      } catch (IOException e) {
        errors.add("Ошибка преобразования файла");
      }

      model.put("customers", customerRepo.findAll());
      model.put("errormessags", errors);
      model.put("admindnone",
          SecurityContextHolder.getContext().getAuthentication().getName().toString());
    }

    return "customer";
  }

}
