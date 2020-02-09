package com.rgd.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import com.rgd.domain.Customer;
import com.rgd.domain.Customer.Gender;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
  List<Customer> findAll();

  Customer findByCustomernameAndLastnameAndAgeAndGenderAllIgnoreCase(String customername,
      String lastname, Integer age, Gender gender);

  Optional<Customer> findById(Long id);
}
