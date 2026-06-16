package com.group1.proyect.freshbasket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T, ID> extends JpaRepository<T, ID> {
    // Esta interfaz le permite a Spring saber qué operaciones heredarán todos los módulos
}