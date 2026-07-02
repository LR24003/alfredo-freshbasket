package com.group1.proyect.freshbasket.service.impl;

import com.group1.proyect.freshbasket.dto.response.UserReportResponseDTO;
import com.group1.proyect.freshbasket.entity.UserReport;
import com.group1.proyect.freshbasket.repository.UserReportRepository;
import com.group1.proyect.freshbasket.service.UserReportService;
import com.group1.proyect.freshbasket.utils.ExportDocUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReportServiceImpl implements UserReportService {

    private final UserReportRepository userReportRepository;

    @Override
    public List<UserReportResponseDTO> getAll(){
        return userReportRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserReportResponseDTO getById(Long id){
        return userReportRepository.findById(id)
                .map(this::convertToResponseDto)
                .orElseThrow(() -> new RuntimeException("Registro de usuario no encontrado con ese Id: " + id));
    }

    @Override
    public List<UserReportResponseDTO> getByFullName(String fullName) {
        return userReportRepository.findByFullNameContainingIgnoreCase(fullName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserReportResponseDTO> getByCountryName(String countryName) {
        return userReportRepository.findByCountryNameContainingIgnoreCase(countryName)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserReportResponseDTO> getByRole(String role) {
        return userReportRepository.findByRoleContainingIgnoreCase(role)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserReportResponseDTO> getByEstado(String estado) {
        return userReportRepository.findByEstadoContainingIgnoreCase(estado)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private UserReportResponseDTO convertToResponseDto(UserReport Entity){
        UserReportResponseDTO dto = new UserReportResponseDTO();
        dto.setId(Entity.getId());
        dto.setFullName(Entity.getFullName());
        dto.setEmail(Entity.getEmail());
        dto.setRole(Entity.getRole());
        dto.setEstado(Entity.getEstado());
        dto.setCountryName(Entity.getCountryName());

        return dto;
    }

    @Override
    public byte[] exportExcel(){
        List<UserReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre completo", "Correo", "Rol", "Estado", "País origen"};

        List<Map<String, Object>> rows = new ArrayList<>();
        for (UserReportResponseDTO dto : data) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", dto.getId());
            row.put("name", dto.getFullName());
            row.put("email", dto.getEmail());
            row.put("role", dto.getRole());
            row.put("status", dto.getEstado());
            row.put("country", dto.getCountryName());
            rows.add(row);
        }

        return ExportDocUtil.toExcel("Reporte de usuarios", headers, rows);
    }

    @Override
    public byte[] exportPdf() {
        List<UserReportResponseDTO> data = this.getAll();
        String[] headers = {"Id", "Nombre completo", "Correo", "Rol", "Estado", "País origen"};

        List<List<String>> rows = new ArrayList<>();
        for (UserReportResponseDTO dto : data) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(dto.getId()));
            row.add(String.valueOf(dto.getFullName()));
            row.add(String.valueOf(dto.getEmail()));
            row.add(String.valueOf(dto.getRole()));
            row.add(String.valueOf(dto.getEstado()));
            row.add(String.valueOf(dto.getCountryName()));
            rows.add(row);
        }

        return ExportDocUtil.toPdf("Reporte de usuarios", headers, null, rows);
    }
}
