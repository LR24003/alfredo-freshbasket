package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.Sale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends GenericRepository<Sale, Long> {

    List<Sale> findByActiveTrue();

    List<Sale> findByStatusAndActiveTrue(String status);

    List<Sale> findByCustomerIdAndActiveTrue(Long customerId);

    List<Sale> findByEmployeeIdAndActiveTrue(Long employeeId);

    List<Sale> findBySaleDateBetweenAndActiveTrue(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.saleDate >= :inicioDia AND s.active = true")
    Optional<BigDecimal> calculateDailyTotal(@Param("inicioDia") LocalDateTime inicioDia);

    Long id(Long id);
}