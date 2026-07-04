package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.SuppliersReportResponseDTO;
import java.util.List;

public interface SuppliersReportService {

    List<SuppliersReportResponseDTO> getAll();
    SuppliersReportResponseDTO getById(Long id);
    List<SuppliersReportResponseDTO> getBySupplierName(String supplierName);
    List<SuppliersReportResponseDTO> getByCountry(String country);
    List<SuppliersReportResponseDTO> getByTotalProducts(String totalProductsRange);

    byte[] exportExcel();
    byte[] exportPdf();
}
