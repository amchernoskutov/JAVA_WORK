package com.rgd.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import com.rgd.form.ReportForm;
import com.rgd.form.ReportParamForm;
import com.rgd.repository.CustomerRepository;
import com.rgd.repository.OrderRepository;
import com.rgd.service.ReportService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SessionAttributes(types = ReportParamForm.class)
@Controller
public class ReportController {
  private CustomerRepository customerRepo;
  private OrderRepository orderRepo;

  public ReportController(CustomerRepository customerRepo, OrderRepository orderRepo) {
    this.customerRepo = customerRepo;
    this.orderRepo = orderRepo;
  }

  @GetMapping("/report")
  public String senDnone(@ModelAttribute ReportParamForm reportParamForm,
      Map<String, Object> model) {
    var customer = customerRepo.findById((long) reportParamForm.getIdcustomer()).get();
    model.put("customer", customer.getLastname() + " " + customer.getCustomername());
    model.put("reportparamform", reportParamForm);
    var reportService = new ReportService(customerRepo, orderRepo);
    var reports = reportService.getReport(reportParamForm);
    reportParamForm.setSumamount(
        reports.stream().map(ReportForm::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    reportParamForm.setSumcount(reports.stream().mapToInt(f -> f.getCount()).sum());
    model.put("shoplists", reports);


    model.put("admindnone",
        SecurityContextHolder.getContext().getAuthentication().getName().toString());
    return "report";
  }
}
