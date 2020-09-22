package loader.elbrus.controller;

import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import loader.elbrus.config.data.GeneralData;
import loader.elbrus.form.MainForm;

/**
 * LoaderelbrusController
 * 
 * Контроллер для главной формы
 *
 */

@Controller
public class LoaderelbrusController {

  @GetMapping("/loaderelbrus")
  public String senDnone(Map<String, Object> model) {
    
    GeneralData.mainForms.sort(Comparator.comparing(MainForm::getDateCreate));
    model.put("mainForms", GeneralData.mainForms);

    return "loaderelbrus";
  }
}


  