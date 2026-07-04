package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.SuppliersReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SuppliersReportRepository extends JpaRepository<SuppliersReport, Long> {

    List<SuppliersReport> findBySupplierNameContainingIgnoreCase(String supplierName);

    List<SuppliersReport> findByCountryContainingIgnoreCase(String country);

    List<SuppliersReport> findByTotalProductsBetween(Integer min, Integer max);

}
