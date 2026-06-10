package org.example.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public abstract class Order {
    private UUID id;
    private UUID carId;
    private User client;
    private User manager;
    private BigDecimal price;
}