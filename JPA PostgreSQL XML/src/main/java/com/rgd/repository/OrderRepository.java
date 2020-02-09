package com.rgd.repository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.rgd.domain.Customer;
import com.rgd.domain.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {
  List<Order> findAll();

  HashSet<Order> findByCustomerAndPurchasedateBetween(Customer customer, LocalDate firstDate,
      LocalDate secondDate);
}
