package ru.bestcham.cham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.CategoryFilm;

public interface CategoryFilmRepository extends JpaRepository<CategoryFilm, Long> {
  List<CategoryFilm> findByNameIgnoreCase(String name);
  CategoryFilm findById(Integer id);
  List<CategoryFilm> findAll();

}

