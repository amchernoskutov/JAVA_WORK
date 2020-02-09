package com.smartsoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.smartsoft.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
  @Autowired
  private UserRepository userRepo;

  @Override
  public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
    var user = userRepo.findByUsername(name);
    if (user != null) {
      return user;
    }
    throw new UsernameNotFoundException("Пользователь с именем: '" + name + "' не найден.");
  }
}