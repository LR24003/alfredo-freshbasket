package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.ProductSoldReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductsSoldRepository extends JpaRepository<ProductSoldReport, Long> {

    List<ProductSoldReport> findByProductNameContainingIgnoreCase(String productName);

    List<ProductSoldReport> findByUnitsSoldBetween(Integer min, Integer max);
}
