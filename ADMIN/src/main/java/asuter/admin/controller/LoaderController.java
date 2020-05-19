package asuter.admin.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import asuter.admin.config.session.SessionConfig;
import asuter.admin.domain.LoaderDatas;
import asuter.admin.mqsender.MQSender;
import asuter.admin.service.LoaderService;
import lombok.Data;

@Controller
@Data
public class LoaderController {
  @Autowired
  LoaderService loaderService;

  @Autowired
  private MQSender mqSender;
  
  @Autowired
  SessionConfig sessionConfig;
  
  @PostMapping("/asuteradmin/loaderfile")
  public String postLoaderFile(@RequestParam(name = "validatedCustomFile") MultipartFile multipart, Map<String, Object> model) {
    String error = "";     
    
    try {
      sessionConfig.getUserConfig().setBooks(loaderService.readCSVFile(multipart));
      sessionConfig.getUserConfig().setProgressBar(0);
    } catch (Exception e) {
      error = "Ошибка: " + e.getMessage(); 
    }

    
    model.put("error", error);
    try {
      model.put("loaderDatas", sessionConfig.getUserConfig().getBooks());
      model.put("progressBarValue", sessionConfig.getUserConfig().getProgressBar());
      model.put("progressBarSize", sessionConfig.getUserConfig().getBooks().size()); 
    } catch (Exception e) {
      return "redirect:/logout";
    }

    return "navmenu8";
  }

  @PostMapping("/asuteradmin/sendfile")
  public String postSendFile(Map<String, Object> model) {
    try {
      if (sessionConfig.getUserConfig().getBooks().isEmpty()) {
        model.put("loaderDatas", null);
        model.put("progressBarValue", null); 
        model.put("progressBarSize", null); 
        return "navmenu8";  
      }

      List<LoaderDatas> books = (List<LoaderDatas>) sessionConfig.getUserConfig().getBooks();
      int progressBarSize = books.size();

      if (progressBarSize == 0) {
        model.put("loaderDatas", null);
        model.put("progressBarValue", null); 
        model.put("progressBarSize", null); 
        return "navmenu8";  
      }

      if (progressBarSize != sessionConfig.getUserConfig().getProgressBar()) {
        LoaderDatas loaderDatas = books.get(sessionConfig.getUserConfig().getProgressBar());
    
        loaderDatas.setCheck(true);
        sessionConfig.getUserConfig().setProgressBar(sessionConfig.getUserConfig().getProgressBar() + 1);

        model.put("loaderDatas", sessionConfig.getUserConfig().getBooks());
        model.put("progressBarValue", (int) loaderDatas.getId()*100/progressBarSize);
        model.put("progressBarSize", progressBarSize); 
      
        try {
          createMQSender(loaderDatas);
          loaderDatas.setRespond("Успешно направлено");
        } catch (Exception e) {
          loaderDatas.setRespond("Ошибка: " + e.getMessage());
        }
      
      } else {
        model.put("loaderDatas", sessionConfig.getUserConfig().getBooks());
        model.put("progressBarValue", 120);
        model.put("progressBarSize", null); 
      }
    } catch (Exception e) {
      return "redirect:/logout";
    }
    
    return "navmenu8";
  }

  // Формирование MQ сообщения
  private void createMQSender(LoaderDatas loaderDatas) throws Exception { 
    mqSender.setId(loaderDatas.getIdRequest());
    mqSender.setDate1(new Timestamp(loaderDatas.getDateStart().getTime()));
    mqSender.setDate2(new Timestamp(loaderDatas.getDateFinish().getTime()));
    mqSender.setData_name(loaderDatas.getShotNameRequest().toUpperCase());
    mqSender.setSystem_name(loaderDatas.getShotNameData().toUpperCase());
    mqSender.setXML_name(loaderDatas.getPathAndFileName());
    mqSender.send("", loaderDatas.getShotNameSystem()); 
  }

}
