package com.smartsoft.controller;

import java.util.Map;
import java.util.TreeSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.smartsoft.form.ReportForm;
import com.smartsoft.repository.CustomerRepository;
import com.smartsoft.repository.ShoppinglistRepository;
import com.smartsoft.service.ReportService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ReportControler {
  private ShoppinglistRepository shoppinglistRepo;
  private CustomerRepository customerRepo;
  private ReportService reportService;

  public ReportControler(ShoppinglistRepository shoppinglistRepo, CustomerRepository customerRepo,
      ReportService reportService) {
    this.shoppinglistRepo = shoppinglistRepo;
    this.customerRepo = customerRepo;
    this.reportService = new ReportService(this.shoppinglistRepo, this.customerRepo);
  }

  @GetMapping("/report/{id}")
  public String senDnone(@PathVariable Integer id, Map<String, Object> model) {
    var reports = new TreeSet<ReportForm>();
    switch (id) {

      case 1:
        reports = reportService.getReport1();
        model.put("namereport", "Отчет список покупок за последнюю неделю");
        model.put("shoplists", reports);
        model.put("sumamount", String.format("%4.2f",
            reports.stream().mapToDouble(f -> f.getAmount().doubleValue()).sum()));
        model.put("finishinfo", "");

        break;
      case 2:
        reports = reportService.getReport2();
        model.put("namereport", "Отчет самый покупаемый товар за последний месяц");
        model.put("shoplists", reports);
        model.put("sumamount", String.format("%4.2f",
            reports.stream().mapToDouble(f -> f.getAmount().doubleValue()).sum()));
        if (!reports.isEmpty()) {
          model.put("finishinfo", "Самый покупаемый товар за последний месяц: "
              + reportService.getMaxCountArticle(reports));
        } else {
          model.put("finishinfo", "Нет данных");
        }

        break;
      case 3:
        reports = reportService.getReport3();
        model.put("namereport",
            "Отчет имя и фамилию человека, совершившего больше всего покупок за полгода");
        model.put("shoplists", reports);
        model.put("sumamount", String.format("%4.2f",
            reports.stream().mapToDouble(f -> f.getAmount().doubleValue()).sum()));
        if (!reports.isEmpty()) {
          model.put("finishinfo",
              "Больше всего покупок совершил: " + reports.first().getLastname() + " "
                  + reports.first().getCustomername() + " (возрост " + reports.first().getAge()
                  + " лет)");
        } else {
          model.put("finishinfo", "Нет данных");
        }

        break;
      case 4:
        reports = reportService.getReport4();
        model.put("namereport", "Отчет чаще всего покупают люди в возрасте 18 лет");
        model.put("shoplists", reports);

        if (!reports.isEmpty()) {
          model.put("finishinfo", "В возрасте 18 лет люди чаще всего покупают: "
              + reportService.getMaxCountArticle(reports));
        } else {
          model.put("finishinfo", "Нет данных");
        }

        break;
    }

    return "report";
  }

}
