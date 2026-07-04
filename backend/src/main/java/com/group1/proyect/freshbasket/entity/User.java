package com.group1.proyect.freshbasket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "El email es obligatorio")
    @Email
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "El password es obligatorio")
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Column(nullable = false)
    private String role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Size(max = 2)
    @Column(name = "tipo_documento", length = 2)
    private String tipoDocumento = "13";

    @NotBlank(message = "El número de documento legal es obligatorio")
    @Size(max = 20)
    @Column(name = "numero_documento", length = 20, nullable = false, unique = true)
    private String numeroDocumento;

    @Size(max = 10)
    @Column(name = "nrc_customer", length = 10)
    private String nrcCustomer;

    @Size(max = 8)
    @Column(name = "actividad_economica_codigo", length = 8)
    private String actividadEconomicaCodigo;

    @Size(max = 2)
    @Column(name = "departamento_codigo", length = 2)
    private String departamentoCodigo = "01";

    @Size(max = 250)
    @Column(name = "direccion_detallada", length = 250)
    private String direccionDetallada;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Entry> entries = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Exit> exits = new ArrayList<>();
}