package com.rgd.service;

import java.math.BigDecimal;
import java.util.HashSet;
import org.springframework.stereotype.Service;
import com.rgd.form.ReportForm;
import com.rgd.form.ReportParamForm;
import com.rgd.repository.CustomerRepository;
import com.rgd.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportService {
  private CustomerRepository customerRepo;
  private OrderRepository orderRepo;

  public ReportService(CustomerRepository customerRepo, OrderRepository orderRepo) {
    this.customerRepo = customerRepo;
    this.orderRepo = orderRepo;
  }

  public HashSet<ReportForm> getReport(ReportParamForm reportParamForm) {
    var shoppinglist = new HashSet<ReportForm>();
    var customer = customerRepo.findById((long) reportParamForm.getIdcustomer()).get();

    try {
      orderRepo.findByCustomerAndPurchasedateBetween(customer, reportParamForm.getFirstdate(),
          reportParamForm.getSeconddate()).forEach(f -> {
            var reportForm = new ReportForm();
            reportForm.setId(f.getId());
            reportForm.setPurchasedate(f.getPurchasedate());
            reportForm.setCount(f.getCount());
            reportForm.setAmount(f.getProduct().getAmount().multiply(new BigDecimal(f.getCount())));
            reportForm.setProductname(f.getProduct().getProductname());
            shoppinglist.add(reportForm);
          });
    } catch (Exception e) {
      //
    }

    return shoppinglist;
  }

}
