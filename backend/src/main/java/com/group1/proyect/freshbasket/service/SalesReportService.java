package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.SalesReportResponseDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface SalesReportService {

    List<SalesReportResponseDTO> getAll();

    SalesReportResponseDTO getById(Long id);

    List<SalesReportResponseDTO> getByEmployeeName(String employeeName);

    List<SalesReportResponseDTO> getFilteredSales(Integer day, Integer month, String paymentMethod);

    List<SalesReportResponseDTO> getFilteredSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate, String paymentMethod);

    byte[] exportExcel();
    byte[] exportPdf();
}
