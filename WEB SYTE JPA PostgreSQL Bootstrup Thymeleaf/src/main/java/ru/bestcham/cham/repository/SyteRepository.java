package ru.bestcham.cham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.Syte;

public interface SyteRepository extends JpaRepository<Syte, Long> {
  List<Syte> findByNameIgnoreCase(String name);
  Syte findById(Integer id);

}
