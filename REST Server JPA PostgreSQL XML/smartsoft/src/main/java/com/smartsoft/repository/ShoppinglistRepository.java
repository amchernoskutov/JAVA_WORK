package com.smartsoft.repository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import com.smartsoft.domain.Customer;
import com.smartsoft.domain.Shoppinglist;

public interface ShoppinglistRepository extends CrudRepository<Shoppinglist, Long> {
  HashSet<Shoppinglist> findAll();
  HashSet<Shoppinglist> findByPurchasedateBetween(LocalDate dateStart, LocalDate dateEnd);
  HashSet<Shoppinglist> findByCustomersIn(Set<Customer> customers);
}
