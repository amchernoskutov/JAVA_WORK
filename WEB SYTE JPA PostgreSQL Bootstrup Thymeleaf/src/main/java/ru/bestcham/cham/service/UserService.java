package ru.bestcham.cham.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.bestcham.cham.domain.User;
import ru.bestcham.cham.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
  @Autowired
  private UserRepository userRepo;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var user = userRepo.findByEmail(email);
    if (user != null) {
      return user;
    }
    throw new UsernameNotFoundException("Пользователь с таким email: '" + email + "' не найдет");
  }
}
