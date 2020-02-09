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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.rgd.form.OrderForm;
import com.rgd.repository.CustomerRepository;
import com.rgd.repository.OrderRepository;
import com.rgd.repository.ProductRepository;
import com.rgd.service.OrderService;
import com.rgd.service.XsdValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
// @RequestMapping("/order")
public class OrderController {
  private CustomerRepository customerRepo;
  private ProductRepository productRepo;
  private OrderRepository orderRepo;
  private OrderService orderService;

  @Value("classpath:xsd/checkorder.xsd")
  private Resource resourceXSDFile;
  
  public OrderController(CustomerRepository customerRepo, ProductRepository productRepo,
      OrderRepository orderRepo) {
    this.customerRepo = customerRepo;
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.orderService = new OrderService(this.customerRepo, this.productRepo, this.orderRepo);
  }

  @GetMapping("/order")
  public String senDnone(OrderForm orderForm, Map<String, Object> model) {
    model.put("orders", orderRepo.findAll());
    model.put("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString());
    return "order";
  }

  @PostMapping("/order")
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value,
      @RequestParam("myfileinput") MultipartFile multipartFile, Map<String, Object> model) {

    if (value.contains("orderinit")) {
      var errors = new HashSet<String>();
      try {
        File file = XsdValidator.convertMultiPartToFile(multipartFile);

        errors.addAll(XsdValidator.xsdValidatorExec(file, resourceXSDFile));
        if (errors.isEmpty()) {
          if (!orderService.exec(file))
            errors.add("Ошибка записи в базу данных.");
        }
      } catch (IOException e) {
        errors.add("Ошибка преобразования файла");
      }

      model.put("orders", orderRepo.findAll());
      model.put("errormessags", errors);
      model.put("admindnone",
          SecurityContextHolder.getContext().getAuthentication().getName().toString());
    }
    return "order";
  }

  @GetMapping("/order/delete/{id}")
  public String processDelete(@PathVariable Integer id, Map<String, Object> model) {
    orderService.delete((long) id);
    return "redirect:/order";
  }


}
