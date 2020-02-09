package com.rgd.repository;

import org.springframework.data.repository.CrudRepository;
import com.rgd.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
  User findByUsername(String name);
}
