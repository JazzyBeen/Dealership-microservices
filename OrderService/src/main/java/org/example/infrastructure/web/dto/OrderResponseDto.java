package org.example.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderResponseDto {
    private UUID id;
    private BigDecimal price;
    private String status;
    private UUID carId;
}