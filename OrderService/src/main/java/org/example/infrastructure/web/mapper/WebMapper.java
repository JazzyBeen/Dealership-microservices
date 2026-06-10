package org.example.infrastructure.web.mapper;

import org.example.domain.entity.CustomOrder;
import org.example.domain.entity.InStockOrder;
import org.example.domain.entity.Order;
import org.example.infrastructure.web.dto.OrderResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebMapper {

    @Mapping(target = "status", expression = "java(getStatus(order))")
    OrderResponseDto toDto(Order order);

    default String getStatus(Order order) {
        if (order == null) return null;
        if (order instanceof InStockOrder) return ((InStockOrder) order).getStatus().name();
        if (order instanceof CustomOrder) return ((CustomOrder) order).getStatus().name();
        return "UNKNOWN";
    }
}