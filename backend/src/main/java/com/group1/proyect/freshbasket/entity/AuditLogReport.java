package com.group1.proyect.freshbasket.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Immutable 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "view_audit_logs_report", schema = "public")
public class AuditLogReport implements Identifiable<Long> {

    @Id
    @Column(name = "audit_id")
    private Long id;

    @Column(name = "entity")
    private String entity;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "action")
    private String action;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
