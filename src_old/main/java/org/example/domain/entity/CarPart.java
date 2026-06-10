package org.example.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class CarPart {
    private String name;
    private BigDecimal priceAdjustment;
    private List<String> compatibleModels;
}
