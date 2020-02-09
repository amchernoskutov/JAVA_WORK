package ru.bestcham.cham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.CategoryYoutube;

public interface CategoryYoutubeRepository extends JpaRepository<CategoryYoutube, Long> {
  List<CategoryYoutube> findByNameIgnoreCase(String name);
  CategoryYoutube findById(Integer id);

}
