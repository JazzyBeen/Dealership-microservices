package org.example.infrastructure.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GrpcCarDto {
    private String id;
    private String brand;
    private String model;
    private String basePrice;
    private Integer enginePower;
    private String transmissionName;
}