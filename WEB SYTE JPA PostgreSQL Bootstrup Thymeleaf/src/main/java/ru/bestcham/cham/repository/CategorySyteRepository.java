package ru.bestcham.cham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.CategorySyte;

public interface CategorySyteRepository extends JpaRepository<CategorySyte, Long> {
  List<CategorySyte> findByNameIgnoreCase(String name);
  CategorySyte findById(Integer id);

}
