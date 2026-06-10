package org.example.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarPartDto {
    private String name;
    private BigDecimal priceAdjustment;
}