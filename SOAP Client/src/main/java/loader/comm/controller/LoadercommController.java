package loader.comm.controller;

import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import loader.comm.form.MainForm;
import loader.comm.log.LSLog;

/**
 * LoadercommController
 * 
 * Контроллер для главной формы
 *
 */

@Controller
public class LoadercommController {

  @GetMapping("/loadercomm")
  public String senDnone(Map<String, Object> model) {

    LSLog.mainForms.sort(Comparator.comparing(MainForm::getDateCreate));
    model.put("mainForms", LSLog.mainForms);

    return "loadercomm";
  }
}


  