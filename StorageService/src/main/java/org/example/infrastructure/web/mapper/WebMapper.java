package org.example.infrastructure.web.mapper;

import org.example.domain.entity.Car;
import org.example.domain.entity.CarPart;
import org.example.infrastructure.web.dto.CarDto;
import org.example.infrastructure.web.dto.CarPartDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WebMapper {
    CarDto toDto(Car car);
    CarPartDto toDto(CarPart carPart);
    Car toDomain(CarDto dto);
    CarPart toDomain(CarPartDto dto);
}