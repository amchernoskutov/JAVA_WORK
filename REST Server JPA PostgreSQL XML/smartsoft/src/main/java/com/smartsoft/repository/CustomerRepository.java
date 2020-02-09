package com.smartsoft.repository;

import java.util.Set;
import org.springframework.data.repository.CrudRepository;
import com.smartsoft.domain.Customer;

public interface CustomerRepository extends  CrudRepository<Customer, Long> {
  Customer findByCustomernameAndLastnameAndAgeAllIgnoreCase(String customername, String lastname, Integer age);
  Set<Customer> findByAge(Integer age);

}
