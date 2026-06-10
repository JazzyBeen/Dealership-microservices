package org.example.infrastructure.messaging.kafka;

import org.example.application.usecase.AssemblyOrderUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaOrderConsumer {
    private final AssemblyOrderUseCase assemblyUseCase;

    public KafkaOrderConsumer(AssemblyOrderUseCase assemblyUseCase) {
        this.assemblyUseCase = assemblyUseCase;
    }

    @KafkaListener(topics = "order-events", groupId = "storage-service-group")
    public void consumeOrderEvents(String payload) {
        if (payload.contains("orderId") && payload.contains("carId")) {
            String orderIdStr = extractValue(payload, "orderId");
            String carIdStr = extractValue(payload, "carId");
            assemblyUseCase.processNewOrder(UUID.fromString(orderIdStr), UUID.fromString(carIdStr));
        }
    }

    private String extractValue(String payload, String key) {
        String search = "\"" + key + "\":\"";
        int start = payload.indexOf(search) + search.length();
        int end = payload.indexOf("\"", start);
        return payload.substring(start, end);
    }
}