package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.CustomerLoyal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerLoyalRepository extends JpaRepository<CustomerLoyal, Long> {

    List<CustomerLoyal> findByCustomerNameContainingIgnoreCase(String customerName);

    List<CustomerLoyal> findByTotalPurchasesBetween(Integer min, Integer max);

}
