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
import com.rgd.form.ProductForm;
import com.rgd.repository.ProductRepository;
import com.rgd.service.ProductService;
import com.rgd.service.XsdValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/product")
public class ProductController {
  private ProductRepository productRepo;
  private ProductService productService;

  @Value("classpath:xsd/checkproduct.xsd")
  private Resource resourceXSDFile;

  public ProductController(ProductRepository productRepo) {
    this.productRepo = productRepo;
    this.productService = new ProductService(this.productRepo);
  }

  @GetMapping
  public String senDnone(ProductForm productForm, Map<String, Object> model) {
    model.put("products", productRepo.findAll());
    model.put("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString());
    return "product";
  }

  @PostMapping
  public String processCollapseShowDesign(@RequestParam(value = "myButtion[]") String value,
      @RequestParam("myfileinput") MultipartFile multipartFile, Map<String, Object> model) {

    if (value.contains("productinit")) {
      var errors = new HashSet<String>();
      try {
        File file = XsdValidator.convertMultiPartToFile(multipartFile);

        errors.addAll(XsdValidator.xsdValidatorExec(file, resourceXSDFile));
        if (errors.isEmpty()) {
          if (!productService.exec(file))
            errors.add("Ошибка записи в базу данных.");
        }
      } catch (IOException e) {
        errors.add("Ошибка преобразования файла");
      }

      model.put("products", productRepo.findAll());
      model.put("errormessags", errors);
      model.put("admindnone",
          SecurityContextHolder.getContext().getAuthentication().getName().toString());
    }

    return "product";
  }

}
