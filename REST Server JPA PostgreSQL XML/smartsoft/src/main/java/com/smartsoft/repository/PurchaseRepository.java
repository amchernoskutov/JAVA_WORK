package com.smartsoft.repository;

import org.springframework.data.repository.CrudRepository;
import com.smartsoft.domain.Purchase;

public interface PurchaseRepository extends  CrudRepository<Purchase, Long> {
  Purchase findByPurchasenameIgnoreCase(String purchasename);
}
