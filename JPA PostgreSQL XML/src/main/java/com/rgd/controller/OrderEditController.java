package com.rgd.controller;

import java.util.HashSet;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class OrderEditController {
  private CustomerRepository customerRepo;
  private ProductRepository productRepo;
  private OrderRepository orderRepo;
  private OrderService orderService;
  private HashSet<String> errors = new HashSet<String>();

  public OrderEditController(CustomerRepository customerRepo, ProductRepository productRepo,
      OrderRepository orderRepo) {
    this.customerRepo = customerRepo;
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.orderService = new OrderService(this.customerRepo, this.productRepo, this.orderRepo);
  }

  @GetMapping("/orderedit/{id}")
  public String getOrderForm(@PathVariable Integer id, OrderInitForm orderInitForm, Model model) {
    if (errors.isEmpty()) {
      orderInitForm = orderService.formInit((long) id, orderInitForm);
    }

    model.addAttribute(orderInitForm);

    model.addAttribute("errormessages", errors);
    model.addAttribute("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString());
    model.addAttribute("orderinitform", orderInitForm);

    model.addAttribute("products", productRepo.findAll());
    model.addAttribute("customers", customerRepo.findAll());

    return "orderedit";
  }

  @PostMapping("/orderedit/{id}")
  public String processOrderForm(@PathVariable Integer id,
      @RequestParam(value = "myButtion[]") String value, OrderInitForm orderInitForm, Model model,
      SessionStatus status) {
    errors = new HashSet<String>();

    if (value.contains("orderedit")) {
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

      return "redirect:/orderedit/" + id;
    } else {
      orderService.editOrder(orderInitForm);
      status.setComplete();
      return "redirect:/order";
    }
  }

}
