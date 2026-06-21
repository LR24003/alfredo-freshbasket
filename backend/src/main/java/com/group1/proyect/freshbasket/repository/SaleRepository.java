package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Sale;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends GenericRepository<Sale, Long>{

    List<Sale> findByActiveTrue();

    List<Sale> findByStatusAndActiveTrue(String status);

    List<Sale> findByCustomerIdAndActiveTrue(Long customerId);
}
