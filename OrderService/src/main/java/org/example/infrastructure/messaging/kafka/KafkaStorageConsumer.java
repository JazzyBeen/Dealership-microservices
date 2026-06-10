package org.example.infrastructure.messaging.kafka;

import org.example.application.usecase.OrderRegistrationUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaStorageConsumer {

    private final OrderRegistrationUseCase orderUseCase;

    public KafkaStorageConsumer(OrderRegistrationUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @KafkaListener(topics = "storage-events", groupId = "order-service-group")
    public void consumeStorageEvents(String payload) {
        if (payload.contains("OrderApproved")) {
            String orderIdStr = extractValue(payload, "orderId");
            orderUseCase.processStorageApproval(UUID.fromString(orderIdStr));
        }
    }

    private String extractValue(String payload, String key) {
        String search = "\"" + key + "\":\"";
        int start = payload.indexOf(search) + search.length();
        int end = payload.indexOf("\"", start);
        return payload.substring(start, end);
    }
}