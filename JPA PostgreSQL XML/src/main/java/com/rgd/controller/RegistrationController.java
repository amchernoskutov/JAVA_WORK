package com.rgd.controller;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.rgd.domain.User;
import com.rgd.form.RegistrationForm;
import com.rgd.repository.UserRepository;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

  private UserRepository userRepo;
  private PasswordEncoder passwordEncoder;

  public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @ModelAttribute(name = "user")
  public User user() {
    return new User("", "");
  }

  @GetMapping
  public String registerForm(RegistrationForm registrationForm, Map<String, Object> model) {
    model.put("username", registrationForm.getUsername());
    model.put("password", registrationForm.getPassword());

    return "registration";
  }

  @PostMapping
  public String processRegistration(RegistrationForm registrationForm, @Valid User user,
      Errors errors, Map<String, Object> model) {
    if (userRepo.findByUsername(registrationForm.getUsername()) != null) {
      errors.rejectValue("username", "error.user",
          "Пользователь с таким именем уже зарегистрирован");
    }

    if (errors.hasErrors()) {
      model.put("username", registrationForm.getUsername());
      model.put("password", registrationForm.getPassword());
      return "registration";
    }

    userRepo.save(registrationForm.toUser(passwordEncoder));
    return "redirect:/login";
  }
}
