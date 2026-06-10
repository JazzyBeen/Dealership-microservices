package org.example.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "car_parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE car_parts SET removed = true WHERE id=?")
@SQLRestriction("removed = false")
public class CarPartJpaEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "price_adjustment", nullable = false)
    private BigDecimal priceAdjustment;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "car_part_compatible_models",
            joinColumns = @JoinColumn(name = "part_id")
    )
    @Column(name = "model_name")
    private List<String> compatibleModels;
}