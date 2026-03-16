package com.investtracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * BaseEntity — Abstract superclass for all tenant-owned entities.
 *
 * WHY: Avoids duplicating createdAt/updatedAt on every table.
 * @EntityListeners(AuditingEntityListener.class) hooks into Spring Data JPA
 * to auto-populate @CreatedDate and @LastModifiedDate fields.
 *
 * @MappedSuperclass tells JPA to inherit these columns into child entity
 * tables, NOT create a separate base_entity table.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
