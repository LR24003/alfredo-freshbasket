package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.InventoryLostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLostRepository extends JpaRepository<InventoryLostReport, Long> {

    List<InventoryLostReport> findByProductNameContainingIgnoreCase(String productName);

    List<InventoryLostReport> findByExitReasonContainingIgnoreCase(String exitReason);

    List<InventoryLostReport> findByUnitsLostBetween(Integer min, Integer max);
}
