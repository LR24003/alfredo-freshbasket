package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalesReportRepository extends JpaRepository<SalesReport, Long> {

    List<SalesReport> findByEmployeeNameContainingIgnoreCase(String employeeName);

}

