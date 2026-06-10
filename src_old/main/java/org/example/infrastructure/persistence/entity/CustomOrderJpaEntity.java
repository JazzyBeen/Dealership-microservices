package org.example.infrastructure.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.domain.entity.CustomOrder;

@Entity
@DiscriminatorValue("CUSTOM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomOrderJpaEntity extends OrderJpaEntity {

    @Enumerated(EnumType.STRING)
    private CustomOrder.Status status;
}