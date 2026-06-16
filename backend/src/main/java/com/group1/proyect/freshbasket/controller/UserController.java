package com.group1.proyect.freshbasket.controller;

import com.group1.proyect.freshbasket.dto.request.UserRequestDTO;
import com.group1.proyect.freshbasket.dto.response.UserResponseDTO;
import com.group1.proyect.freshbasket.entity.User;
import com.group1.proyect.freshbasket.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API optimizada para la gestión de usuarios de FreshBasket")
public class UserController extends GenericController<User, UserRequestDTO, UserResponseDTO, Long> {

    private final UserService userService;

    public UserController(UserService userService) {
        super(userService, "usuario");
        this.userService = userService;
    }


    @Operation(
            summary = "Buscar usuarios por nombre",
            description = "Retorna usuarios que coincidan con el nombre especificado (búsqueda parcial)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados con éxito"),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios con ese nombre")
    })
    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsersByName(
            @Parameter(description = "Nombre o parte del nombre a buscar", example = "Juan Manuel", required = true)
            @RequestParam String name) {
        return ResponseEntity.ok(userService.searchUsersByName(name));
    }

    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Utiliza el token de sesión para extraer el email y retornar los datos del usuario actual"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(Principal principal) {
        String email = principal.getName();
        UserResponseDTO userProfile = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(userProfile);
    }

    @Operation(
            summary = "Actualizar perfil del usuario autenticado",
            description = "Permite al usuario conectado modificar sus propios datos básicos desde el formulario de perfil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos provistos inválidos")
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMyProfile(Principal principal, @Valid @RequestBody UserRequestDTO requestDTO) {
        String email = principal.getName();
        UserResponseDTO updatedUser = userService.updateUserProfileByEmail(email, requestDTO);
        return ResponseEntity.ok(updatedUser);
    }
}