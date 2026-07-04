package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SaleDetailsRepository extends GenericRepository<SaleDetails, Long> {

    List<SaleDetails> findBySaleIdAndActiveTrue(Long saleId);

    List<SaleDetails> findBySaleId(Long saleId);

}