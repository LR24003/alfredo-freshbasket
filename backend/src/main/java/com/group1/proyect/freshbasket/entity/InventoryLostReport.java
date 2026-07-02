package com.group1.proyect.freshbasket.entity;

import jakarta.persistence.*;
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
@Table(name = "view_inventory_losses_report", schema = "public")
public class InventoryLostReport implements Identifiable<Long> {

    @Id
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "exit_reason")
    private String exitReason;

    @Column(name = "total_units_lost")
    private Integer unitsLost;

    @Column(name = "estimated_financial_loss")
    private BigDecimal totalLost;
}
