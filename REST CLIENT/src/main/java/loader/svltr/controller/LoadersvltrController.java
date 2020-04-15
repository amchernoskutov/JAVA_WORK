package loader.svltr.controller;

import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import loader.svltr.form.MainForm;
import loader.svltr.log.LSLog;

/**
 * LoadersvltrController
 * 
 * Контроллер для главной формы
 *
 */

@Controller
public class LoadersvltrController {

  @GetMapping("/loadersvltr")
  public String senDnone(Map<String, Object> model) {

    LSLog.mainForms.sort(Comparator.comparing(MainForm::getDateCreate));
    model.put("mainForms", LSLog.mainForms);

    return "loadersvltr";
  }
}


  