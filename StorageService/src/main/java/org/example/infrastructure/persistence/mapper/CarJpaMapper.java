package org.example.infrastructure.persistence.mapper;

import org.example.domain.entity.Car;
import org.example.domain.entity.CarPart;
import org.example.infrastructure.persistence.entity.CarJpaEntity;
import org.example.infrastructure.persistence.entity.CarPartJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarJpaMapper {

    Car toDomain(CarJpaEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "removed", ignore = true)
    CarJpaEntity toJpa(Car domain);

    CarPart toDomain(CarPartJpaEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "removed", ignore = true)
    @Mapping(target = "id", ignore = true)
    CarPartJpaEntity toJpa(CarPart domain);
}