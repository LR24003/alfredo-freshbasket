package com.group1.proyect.freshbasket.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String role;
    private String email;
    private String name;
    private String lastName;
}
