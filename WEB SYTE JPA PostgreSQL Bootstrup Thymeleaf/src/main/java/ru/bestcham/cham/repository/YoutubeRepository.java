package ru.bestcham.cham.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bestcham.cham.domain.Youtube;

public interface YoutubeRepository extends JpaRepository<Youtube, Long> {
  List<Youtube> findByNameIgnoreCase(String name);
  Youtube findById(Integer id);

}
