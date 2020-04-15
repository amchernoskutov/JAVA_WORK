package asuter.admin.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/asuteradmin")
public class AsuterAdminController {

  @Autowired
  AuthenticationManagerBuilder auth;
  
  @GetMapping
  public String getAsuterAdmin(Map<String, Object> model, Principal principal) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
    model.put("navbarUser", username);

    
   //    System.out.println("username=" + username + " principal.getName()=" + principal.getName());
//    auth.jdbcAuthentication().getUserDetailsService().getUsersByUsernameQuery()
    
//    SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(i -> System.out.println(i.getAuthority()));
    
    //https://boraji.com/spring-security-5-jdbc-based-authentication-example

    
    return "asuteradmin";
  }
}
