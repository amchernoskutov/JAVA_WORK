package com.smartsoft.form;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.smartsoft.domain.User;
import lombok.Data;

@Data
public class RegistrationForm {
  private String username;
  private String password;

  public RegistrationForm() {
    username = "Введите имя пользователя";
    password = "Введите пароль";
  }

  public User toUser(PasswordEncoder passwordEncoder) {
    return new User(username, passwordEncoder.encode(password));
  }

}

