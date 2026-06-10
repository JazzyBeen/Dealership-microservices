package org.example.application.usecase;

import org.example.domain.entity.AssemblyOrder;
import org.example.infrastructure.messaging.outbox.OutboxEventJpaEntity;
import org.example.infrastructure.messaging.outbox.StorageOutboxRepository;
import org.example.infrastructure.persistence.entity.AssemblyOrderJpaEntity;
import org.example.infrastructure.persistence.repository.SpringDataAssemblyOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AssemblyOrderUseCase {
    private final SpringDataAssemblyOrderRepository assemblyRepository;
    private final StorageOutboxRepository outboxRepository;

    public AssemblyOrderUseCase(SpringDataAssemblyOrderRepository assemblyRepository, StorageOutboxRepository outboxRepository) {
        this.assemblyRepository = assemblyRepository;
        this.outboxRepository = outboxRepository;
    }

    public void processNewOrder(UUID sourceOrderId, UUID carId) {
        AssemblyOrderJpaEntity assembly = new AssemblyOrderJpaEntity();
        assembly.setSourceOrderId(sourceOrderId);
        assembly.setCarId(carId);
        assembly.setStatus(AssemblyOrder.Status.CREATED);
        assemblyRepository.save(assembly);

        assembly.setStatus(AssemblyOrder.Status.ASSEMBLED);
        assemblyRepository.save(assembly);

        String payload = String.format("{\"eventType\":\"OrderApproved\", \"orderId\":\"%s\"}", sourceOrderId);
        outboxRepository.save(new OutboxEventJpaEntity("OrderApproved", payload));
    }
}