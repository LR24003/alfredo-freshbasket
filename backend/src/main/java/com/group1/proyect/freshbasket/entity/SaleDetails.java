package com.group1.proyect.freshbasket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sale_details")
public class SaleDetails implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "discount", nullable = false, precision = 5, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "tipo_item_exento", nullable = false)
    private Integer tipoItemExento = 1;

    @Column(name = "unidad_medida_codigo", nullable = false, length = 3)
    private String unidadMedidaCodigo = "99";

    @Column(name = "iva_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal ivaItem = BigDecimal.ZERO;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
