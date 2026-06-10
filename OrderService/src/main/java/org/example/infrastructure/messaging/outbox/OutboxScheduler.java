package org.example.infrastructure.messaging.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class OutboxScheduler {

    private final SpringDataOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxScheduler(SpringDataOutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        List<OutboxEventJpaEntity> events = outboxRepository.findByProcessedFalse();
        for (OutboxEventJpaEntity event : events) {
            kafkaTemplate.send("order-events", event.getPayload());
            event.setProcessed(true);
            outboxRepository.save(event);
        }
    }
}