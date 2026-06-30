package com.group1.proyect.freshbasket.repository;


import com.group1.proyect.freshbasket.entity.InventoryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryReportRepository extends JpaRepository<InventoryReport, Long> {

    List<InventoryReport> findByProductNameContainingIgnoreCase(String productName);

    List<InventoryReport> findByStockAvailableBetween(Integer min, Integer max);
}
