package org.example.infrastructure.web.mapper;

import org.example.domain.entity.*;
import org.example.infrastructure.web.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebMapper {

    CarDto toDto(Car car);
    CarPartDto toDto(CarPart carPart);

    Car toDomain(CarDto dto);
    CarPart toDomain(CarPartDto dto);

    @Mapping(target = "status", expression = "java(getStatus(order))")
    OrderResponseDto toDto(Order order);

    default String getStatus(Order order) {
        if (order == null) return null;
        if (order instanceof InStockOrder) return ((InStockOrder) order).getStatus().name();
        if (order instanceof CustomOrder) return ((CustomOrder) order).getStatus().name();
        return "UNKNOWN";
    }
}