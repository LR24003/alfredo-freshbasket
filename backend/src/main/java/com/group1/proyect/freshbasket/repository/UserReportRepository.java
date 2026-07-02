package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    List<UserReport> findByFullNameContainingIgnoreCase(String fullName);

    List<UserReport> findByCountryNameContainingIgnoreCase(String countryName);

    List<UserReport> findByRoleContainingIgnoreCase(String role);

    List<UserReport> findByEstadoContainingIgnoreCase(String estado);
}
