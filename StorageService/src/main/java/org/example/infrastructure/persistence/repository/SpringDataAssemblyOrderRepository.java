package org.example.infrastructure.persistence.repository;

import org.example.infrastructure.persistence.entity.AssemblyOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataAssemblyOrderRepository extends JpaRepository<AssemblyOrderJpaEntity, UUID> {
}