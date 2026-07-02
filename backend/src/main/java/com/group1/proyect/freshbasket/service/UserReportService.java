package com.group1.proyect.freshbasket.service;

import com.group1.proyect.freshbasket.dto.response.UserReportResponseDTO;
import java.util.List;

public interface UserReportService {

    List<UserReportResponseDTO> getAll();
    UserReportResponseDTO getById(Long id);
    List<UserReportResponseDTO> getByFullName(String FullName);
    List<UserReportResponseDTO> getByCountryName(String countryName);
    List<UserReportResponseDTO> getByRole(String role);
    List<UserReportResponseDTO> getByEstado(String estado);

    byte[] exportExcel();
    byte[] exportPdf();

}
