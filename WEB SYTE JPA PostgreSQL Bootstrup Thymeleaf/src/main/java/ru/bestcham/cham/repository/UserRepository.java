package ru.bestcham.cham.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);
}


