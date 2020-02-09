package ru.bestcham.cham.controller;

import java.util.ArrayList;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import ru.bestcham.cham.form.RegistrationForm;
import ru.bestcham.cham.repository.UserRepository;

@Slf4j
@Controller
@RequestMapping("/registration")
public class RegistrationController {

  private UserRepository userRepo;
  private PasswordEncoder passwordEncoder;
  private int lengthPassword = 8;

  public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public String registerForm(RegistrationForm registrationForm, Model model) {

    model.addAttribute("username", registrationForm.getUsername());
    model.addAttribute("email", registrationForm.getEmail());

    return "registration";
  }

  @PostMapping
  public String processRegistration(RegistrationForm registrationForm, Map<String, Object> model) {
    ArrayList<String> errorMessages = new ArrayList<String>();

    if (userRepo.findByEmail(registrationForm.getEmail()) != null) {
      errorMessages.add(
          "Ошибка: Пользователь с email: " + registrationForm.getEmail() + " уже зарегистрирован.");
    } else {
      if (registrationForm.getUsername().length() < 1) {
        errorMessages.add("Ошибка: Имя пользователя не задано.");
      }

      if (registrationForm.getPassword().length() < lengthPassword) {
        errorMessages.add("Ошибка: длинна пароля должна быть не менее 8 символов.");
      }

      if (!registrationForm.getPassword().equals(registrationForm.getConfirm())) {
        errorMessages.add("Ошибка: пароли не совпадают.");
      }
    }

    if (errorMessages.isEmpty()) {
      userRepo.save(registrationForm.toUser(passwordEncoder));
    } else {
      model.put("errormessags", errorMessages);
      model.put("username", registrationForm.getUsername());
      model.put("email", registrationForm.getEmail());
      return "registration";
    }

    return "redirect:/login";
  }

}
