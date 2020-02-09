package com.rgd.controller;

import java.time.LocalDate;
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
import com.rgd.form.OrderInitForm;
import com.rgd.repository.CustomerRepository;
import com.rgd.repository.OrderRepository;
import com.rgd.repository.ProductRepository;
import com.rgd.service.OrderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SessionAttributes(types = OrderInitForm.class)
@Controller
public class OrderAddController {
  private CustomerRepository customerRepo;
  private ProductRepository productRepo;
  private OrderRepository orderRepo;
  private OrderService orderService;
  private HashSet<String> errors;

  public OrderAddController(CustomerRepository customerRepo, ProductRepository productRepo,
      OrderRepository orderRepo) {
    this.customerRepo = customerRepo;
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.orderService = new OrderService(this.customerRepo, this.productRepo, this.orderRepo);
  }

  @GetMapping("/orderadd")
  public String getOrderForm(OrderInitForm orderInitForm, Model model) {
    model.addAttribute(orderInitForm);

    model.addAttribute("errormessages", errors);
    model.addAttribute("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString());
    model.addAttribute("orderinitform", orderInitForm);
    model.addAttribute("products", productRepo.findAll());
    model.addAttribute("customers", customerRepo.findAll());

    return "orderadd";
  }

  @PostMapping("/orderadd")
  public String processOrderForm(@RequestParam(value = "myButtion[]") String value,
      OrderInitForm orderInitForm, Model model, SessionStatus status) {
    errors = new HashSet<String>();

    if (value.contains("orderadd")) {
      if (orderInitForm.getPurchasedate() == null) {
        errors.add("Дата покупки не может быть пустой");
      }
      if ((orderInitForm.getCount() == null) || (orderInitForm.getCount() == 0)
          || (orderInitForm.getCount() < 0)) {
        errors.add("Количество должно быть больше ноля");
      }
      if (orderInitForm.getIdcustomer() == 0) {
        errors.add("Не задан покупатель");
      }
      if (orderInitForm.getIdproduct() == 0) {
        errors.add("Не задана покупка");
      }
    }

    if (!errors.isEmpty()) {
      Assert.notNull(orderInitForm);
      return "redirect:/orderadd";
    } else {
      orderService.addOrder(orderInitForm);
      status.setComplete();
      return "redirect:/order";
    }
  }

}
