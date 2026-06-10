package org.example.infrastructure.persistence.mapper;

import org.example.domain.entity.TestDriveRequest;
import org.example.infrastructure.persistence.entity.TestDriveRequestJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserJpaMapper.class})
public interface TestDriveJpaMapper {

    TestDriveRequest toDomain(TestDriveRequestJpaEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "removed", ignore = true)
    TestDriveRequestJpaEntity toJpa(TestDriveRequest domain);
}