package com.group1.proyect.freshbasket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "view_users_report", schema = "public")
public class UserReport implements Identifiable<Long>{

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "estado")
    private String estado;

    @Column(name = "country_name")
    private String countryName;
}
