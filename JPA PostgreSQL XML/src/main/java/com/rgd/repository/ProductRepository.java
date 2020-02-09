package com.rgd.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.rgd.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
  List<Product> findAll();

  Product findByProductnameAllIgnoreCase(String productname);

  Optional<Product> findById(Long id);
}
