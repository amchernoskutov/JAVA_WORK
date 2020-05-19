package asuter.admin.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import asuter.admin.config.session.SessionConfig;
import asuter.admin.domain.SystemName;
import asuter.admin.service.SystemNameService;

@Controller
@RequestMapping("/asuteradmin")
public class AsuterAdminController {
  @Autowired
  private SessionConfig sessionConfig;
  
  @Autowired
  SystemNameService systemNameService;
  
  @GetMapping
  public String getAsuterAdmin(Map<String, Object> model, Principal principal) {

    String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
    model.put("navbarUser", username);
    
    try {
      sessionConfig.InitModuleConfig();
    } catch (Exception e) {
      return "redirect:/logout";
    }

    model.put("addsystemNameItem", new SystemName());
    model.put("updateSystemNameItem", new SystemName());
    model.put("deleteSystemNameItem", new SystemName());
    
    Map<String, String> systemNameItemError = new HashMap<String, String>();
    model.put("systemNameItemError", systemNameItemError);

    
    try {
      model.put("systemNames", systemNameService.findByAll());
    } catch (Exception e) {
      //
    }

    return "asuteradmin";
  }
}
