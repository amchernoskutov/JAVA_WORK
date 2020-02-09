package com.smartsoft.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import com.smartsoft.controller.ReportControler;
import com.smartsoft.domain.Customer;
import com.smartsoft.domain.Shoppinglist;
import com.smartsoft.form.ReportForm;
import com.smartsoft.form.ReportFormComparator;
import com.smartsoft.repository.CustomerRepository;
import com.smartsoft.repository.ShoppinglistRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportService {
  private ShoppinglistRepository shoppinglistRepo;
  private CustomerRepository customerRepo;

  public ReportService(ShoppinglistRepository shoppinglistRepo, CustomerRepository customerRepo) {
    this.shoppinglistRepo = shoppinglistRepo;
    this.customerRepo = customerRepo;
  }

  private TreeSet<ReportForm> setReportForm(ArrayList<Shoppinglist> shoppinglist) {
    var reportFormComparator = new ReportFormComparator();
    TreeSet<ReportForm> report = new TreeSet<ReportForm>(reportFormComparator);

    shoppinglist.forEach(f -> {
      var reportForm = new ReportForm();
      reportForm.setPurchasename(f.getPurchases().getPurchasename());
      reportForm.setCustomername(f.getCustomers().getCustomername());
      reportForm.setLastname(f.getCustomers().getLastname());
      reportForm.setAge(f.getCustomers().getAge());
      reportForm.setCount(f.getCount());
      reportForm.setAmount(f.getAmount());
      reportForm.setPurchasedate(f.getPurchasedate());
      reportForm.setArticles(f.getArticles());

      var articles = new ArrayList<String>();
      f.getArticles().forEach(r -> {
        articles.add(r.getCode().toString());
      });
      reportForm.setArticle(StringUtils.join(articles, ", "));
      report.add(reportForm);
    });

    return report;
  }

  public TreeSet<ReportForm> getReport() {
    var shoppinglist = new ArrayList<Shoppinglist>();
    shoppinglistRepo.findAll().forEach(f -> shoppinglist.add(f));

    return setReportForm(shoppinglist);
  }

  public TreeSet<ReportForm> getReport1() {
    var shoppinglist = new ArrayList<Shoppinglist>();
    LocalDate now = LocalDate.now();
    LocalDate lastWeekStart = now.minusWeeks(1).with(DayOfWeek.MONDAY);
    LocalDate lastWeekEnd = lastWeekStart.plusDays(6);

    shoppinglistRepo.findByPurchasedateBetween(lastWeekStart, lastWeekEnd)
        .forEach(f -> shoppinglist.add(f));
    return setReportForm(shoppinglist);
  }

  public TreeSet<ReportForm> getReport2() {
    var shoppinglist = new ArrayList<Shoppinglist>();
    LocalDate now = LocalDate.now();
    LocalDate firstOfThisMonth = now.withDayOfMonth(1);
    LocalDate firstOfLastMonth = firstOfThisMonth.minusMonths(1);
    LocalDate endOfLastMonth = firstOfThisMonth.minusDays(1);

    shoppinglistRepo.findByPurchasedateBetween(firstOfLastMonth, endOfLastMonth)
        .forEach(f -> shoppinglist.add(f));
    return setReportForm(shoppinglist);
  }

  public TreeSet<ReportForm> getReport3() {
    var shoppinglist = new ArrayList<Shoppinglist>();
    var resultShoppinglist = new ArrayList<Shoppinglist>();
    LocalDate now = LocalDate.now();
    LocalDate firstOfThisMonth = now.withDayOfMonth(1);
    LocalDate firstOfLastMonth = firstOfThisMonth.minusMonths(5);

    shoppinglistRepo.findByPurchasedateBetween(firstOfLastMonth, now)
        .forEach(f -> shoppinglist.add(f));

    Map<Customer, Integer> countByCustomers = shoppinglist.stream().collect(Collectors
        .groupingBy(Shoppinglist::getCustomers, Collectors.summingInt(Shoppinglist::getCount)));

    var maxCount = Collections
        .max(countByCustomers.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

    shoppinglist.stream().filter(f -> f.getCustomers() == maxCount)
        .forEach(f -> resultShoppinglist.add(f));

    return setReportForm(resultShoppinglist);
  }

  public TreeSet<ReportForm> getReport4() {
    var shoppinglist = new ArrayList<Shoppinglist>();

    var findAge = customerRepo.findByAge((Integer)18);
    if (!findAge.isEmpty()) {
    shoppinglistRepo.findByCustomersIn(findAge)
        .forEach(f -> shoppinglist.add(f));
    }
    return setReportForm(shoppinglist);
  }

  public String getMaxCountArticle(TreeSet<ReportForm> reports) {
    var articles = new ArrayList<String>();
    var articlesRes = new ArrayList<String>();
    reports.forEach(f -> {
      f.getArticles().forEach(r -> {
        articles.add(r.getCode().toString());
      });
    });

    var articleCount = new HashMap<String, Integer>();

    articles.stream().collect(Collectors.groupingBy(elem -> elem)).forEach((key, value) -> {
      articleCount.put(key, value.size());
    });

     Integer maxCount = Collections.max(articleCount.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
     for (HashMap.Entry<String, Integer> f : articleCount.entrySet()) {
       if (f.getValue() ==  maxCount) articlesRes.add(f.getKey()); 
     }
     
     return StringUtils.join(articlesRes, ", ");
      
  }

}
