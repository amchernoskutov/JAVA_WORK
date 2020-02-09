package com.smartsoft.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.smartsoft.repository.CustomerRepository;
import com.smartsoft.repository.PurchaseRepository;
import com.smartsoft.repository.ShoppinglistRepository;
import com.smartsoft.service.XMLFileToDataBase;
import com.smartsoft.service.XsdValidator;

@RestController
@RequestMapping("upload")
public class PurchaseController {

  private ArrayList<HashSet<String>> errorsValidator = new ArrayList<HashSet<String>>();

  private ShoppinglistRepository shoppinglistRepo;
  private PurchaseRepository purchaseRepo;
  private CustomerRepository customerRepo;

  public PurchaseController(ShoppinglistRepository shoppinglistRepo,
      PurchaseRepository purchaseRepo, CustomerRepository customerRepo) {
    this.shoppinglistRepo = shoppinglistRepo;
    this.purchaseRepo = purchaseRepo;
    this.customerRepo = customerRepo;
  }

  @GetMapping
  @ResponseBody
  public ArrayList<HashSet<String>> list() {

    return errorsValidator;
  }

  @PostMapping
  public @ResponseBody ArrayList<HashSet<String>> create(
      @RequestParam("file") MultipartFile multipartFile) {
    var errors = new HashSet<String>();
    errors.add("Принимаю файл: " + multipartFile.getOriginalFilename());
    try {
      File file = XsdValidator.convertMultiPartToFile(multipartFile);

      var getErrors = new ArrayList<String>();
      getErrors = XsdValidator.xsdValidatorExec(file);
      if (getErrors.isEmpty()) {
        errors.add("Ошибок валидации нет");

        var xMLFileToDataBase = new XMLFileToDataBase(shoppinglistRepo, purchaseRepo, customerRepo);
        if (xMLFileToDataBase.exec(file)) {
          errors.add("Данные записаны в базу данных");
        } else {
          errors.add("Есть ошибки. Данные не записаны в базу данных");
        }

      } else {
        errors.addAll(getErrors);
      }
    } catch (IOException e) {
      errors.add("Ошибка преобразования файла");
    }

    errorsValidator.add(errors);
    return errorsValidator;
  }

}
