package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.AuditLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogReportRepository extends JpaRepository<AuditLogReport, Long> {

    List<AuditLogReport> findByUserNameContainingIgnoreCase(String userName);

    List<AuditLogReport> findByActionIgnoreCase(String action);
}