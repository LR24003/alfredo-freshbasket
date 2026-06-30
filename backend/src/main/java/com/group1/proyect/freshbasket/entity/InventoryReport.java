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
import java.math.BigDecimal;

@Entity
@Immutable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "view_inventory_report", schema = "public")
public class InventoryReport implements Identifiable<Long> {

    @Id
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "total_entries")
    private Integer totalEntries;

    @Column(name = "total_exits")
    private Integer totalExits;

    @Column(name = "stock_available")
    private Integer stockAvailable;
}
