package asuter.admin.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import asuter.admin.domain.LoaderDatas;
import asuter.admin.service.LoaderService;
import lombok.Data;

@Controller
@Data
public class LoaderController {

  @Autowired
  LoaderService loaderService;

  @Autowired 
  private HttpSession httpSession;

  @PostMapping("/asuteradmin/loaderfile")
  public String postLoaderFile(@RequestParam(name = "validatedCustomFile") MultipartFile multipart, Map<String, Object> model) {
    String error = "";     
    
    try {
      httpSession.removeAttribute("books");
      httpSession.removeAttribute("progressBar");
      httpSession.setAttribute("books", loaderService.readCSVFile(multipart));
      httpSession.setAttribute("progressBar", 0);
    } catch (Exception e) {
      error = "Ошибка: " + e.getMessage(); 
    }
    
    model.put("error", error);
    model.put("loaderDatas", httpSession.getAttribute("books"));
    model.put("progressBarValue", httpSession.getAttribute("progressBar"));

    return "navmenu3";
  }

  @PostMapping("/asuteradmin/sendfile")
  public String postSendFile(Map<String, Object> model) {
    String error = "";      
    
    if (httpSession.getAttribute("books") == null) {
      model.put("loaderDatas", null);
      model.put("progressBarValue", null); 
      return "navmenu3";  
    }

    List<LoaderDatas> books = (List<LoaderDatas>) httpSession.getAttribute("books");
    int progressBarSize = books.size();

    if (progressBarSize == 0) {
      model.put("loaderDatas", null);
      model.put("progressBarValue", null); 
      return "navmenu3";  
    }

    if (progressBarSize != (int) httpSession.getAttribute("progressBar")) {
    
      LoaderDatas loaderDatas = books.get((int) httpSession.getAttribute("progressBar"));
    
      loaderDatas.setCheck(true);
      httpSession.setAttribute("progressBar", (int) httpSession.getAttribute("progressBar") + 1);

      model.put("loaderDatas", httpSession.getAttribute("books"));
      model.put("progressBarValue", (int) loaderDatas.getId()*100/progressBarSize);

      System.out.println(loaderDatas.getId() + " " + progressBarSize + " " + Integer.toString((int) loaderDatas.getId()*100/progressBarSize) + " " + httpSession.getId());
    
    } else {
      model.put("loaderDatas", httpSession.getAttribute("books"));
      model.put("progressBarValue", 120);
      System.out.println(httpSession.getAttribute("progressBar") + " " + httpSession.getId());
    }
    
    return "navmenu3";
  }

}
