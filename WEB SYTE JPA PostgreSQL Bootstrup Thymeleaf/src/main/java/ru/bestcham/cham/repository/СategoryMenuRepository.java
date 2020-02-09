package ru.bestcham.cham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.СategoryMenu;

public interface СategoryMenuRepository extends JpaRepository<СategoryMenu, Long> {
  СategoryMenu findByNameIgnoreCase(String name);
  List<СategoryMenu> findAll();

}
