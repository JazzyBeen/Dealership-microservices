package org.example.infrastructure.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.domain.entity.InStockOrder;

@Entity
@DiscriminatorValue("IN_STOCK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InStockOrderJpaEntity extends OrderJpaEntity {

    @Enumerated(EnumType.STRING)
    private InStockOrder.Status status;
}