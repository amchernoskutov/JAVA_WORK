package com.smartsoft.repository;

import org.springframework.data.repository.CrudRepository;
import com.smartsoft.domain.User;

public interface UserRepository extends  CrudRepository<User, Long> {
  User findByUsername(String name);
}
