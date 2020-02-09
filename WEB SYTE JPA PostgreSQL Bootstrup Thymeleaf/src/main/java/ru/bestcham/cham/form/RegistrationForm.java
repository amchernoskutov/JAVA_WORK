package ru.bestcham.cham.form;

import java.util.Collections;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;
import ru.bestcham.cham.domain.Role;
import ru.bestcham.cham.domain.User;

@Data
public class RegistrationForm {
  private String username;
  private String email;
  private String password;
  private String confirm;
  private Set<Role> roles;

  public RegistrationForm() {
    username = "Введите имя пользователя";
    email = "Введите email";
  }

  public User toUser(PasswordEncoder passwordEncoder) {
    User user = new User();

    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setRoles(Collections.singleton(Role.USER));

    return user;
  }

}
