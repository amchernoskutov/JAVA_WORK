package ru.bestcham.cham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {
  List<Film> findByNameIgnoreCase(String name);
  Film findById(Integer id);

}
