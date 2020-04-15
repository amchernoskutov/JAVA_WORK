package loader.elbrus.controller;

import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import loader.elbrus.form.MainForm;
import loader.elbrus.log.LALog;

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
    
    LALog.mainForms.sort(Comparator.comparing(MainForm::getDateCreate));
    model.put("mainForms", LALog.mainForms);

    return "loaderelbrus";
  }
}


  