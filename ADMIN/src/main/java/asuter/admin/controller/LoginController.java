package asuter.admin.controller;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import asuter.admin.domain.Users;
import asuter.admin.form.LoginForm;
import asuter.admin.jdbc.repository.JdbcUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
@Controller
public class LoginController {

  @Autowired
  JdbcUserRepository jdbcUserRepo;
  
  @Autowired
  PasswordEncoder passwordEncoder;
  
  @GetMapping("/login")
  public String getLogin(Map<String, Object> model) {
    LoginForm loginForm = new LoginForm("user_admin", true, true, "", true);
    model.put("loginForm", loginForm);

    return "login";
  }

  @PostMapping( "/login/failed")
  public String getLoginFailed(LoginForm loginForm,  Map<String, Object> model) {
    loginForm.setUsernameIsActive(true);
    loginForm.setUsernameIsFound(true); 
    loginForm.setPasswordIsFound(true);
    try {
      Users user = jdbcUserRepo.findOne(loginForm.getUsername());
      if (user == null) {
        loginForm.setUsernameIsFound(false);
      } else {
        loginForm.setUsernameIsActive(user.isActive());
        if (!user.getPassword().equals(passwordEncoder.encode(loginForm.getPassword()))) loginForm.setPasswordIsFound(false); 
      }
    } catch(Exception e) {
      loginForm.setUsernameIsFound(false);
    }

    model.put("loginForm", loginForm);

    return "loginform";
  }

}
