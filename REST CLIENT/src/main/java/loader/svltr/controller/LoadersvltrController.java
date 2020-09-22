package loader.svltr.controller;

import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import loader.svltr.config.data.GeneralData;
import loader.svltr.form.MainForm;

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

    GeneralData.mainForms.sort(Comparator.comparing(MainForm::getDateCreate));
    model.put("mainForms", GeneralData.mainForms);

    return "loadersvltr";
  }
}


  