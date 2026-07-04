package com.group1.proyect.freshbasket.repository;

import com.group1.proyect.freshbasket.entity.DteControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DteControlRepository extends GenericRepository<DteControl, Long> {

    Optional<DteControl> findByCodigoGeneracion(UUID codigoGeneracion);

    Optional<DteControl> findByNumeroControl(String numeroControl);

    Optional<DteControl> findBySaleId(Long saleId);

    List<DteControl> findByStatusFiscalAndActiveTrue(String statusFiscal);


    @Query(value = "SELECT dc.numero_control FROM dte_control dc " +
            "WHERE dc.tipo_documento = :tipoDoc " +
            "ORDER BY dc.dte_id DESC LIMIT 1", nativeQuery = true)
    Optional<String> findLastNumeroControlByTipoDocumento(@Param("tipoDoc") String tipoDocumento);
}
