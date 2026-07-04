package com.group1.proyect.freshbasket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "view_suppliers_report", schema = "public")
public class SuppliersReport implements Identifiable<Long> {

    @Id
    @Column(name = "supplier_id")
    private Long id;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "country")
    private String country;

    @Column(name = "main_product")
    private String mainProduct;

    @Column(name = "supplied_volume")
    private String suppliedVolume;

    @Column(name = "total_products")
    private Integer totalProducts;

    @Column(name = "total_stock")
    private Integer totalStock;

    @Column(name = "total_purchased")
    private BigDecimal totalPurchased;
}
