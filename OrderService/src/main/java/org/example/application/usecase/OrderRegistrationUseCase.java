package org.example.application.usecase;

import org.example.domain.entity.CustomOrder;
import org.example.domain.entity.InStockOrder;
import org.example.domain.entity.Order;
import org.example.domain.entity.User;
import org.example.domain.repositories.OrderRepository;
import org.example.infrastructure.messaging.outbox.OutboxEventJpaEntity;
import org.example.infrastructure.messaging.outbox.SpringDataOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderRegistrationUseCase {
    private final OrderRepository orderRepository;
    private final SpringDataOutboxRepository outboxRepository;

    public OrderRegistrationUseCase(OrderRepository orderRepository, SpringDataOutboxRepository outboxRepository) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
    }

    public InStockOrder createInStockOrder(User client, User manager, UUID carId, BigDecimal price) {
        InStockOrder order = new InStockOrder(null, carId, client, manager, price, InStockOrder.Status.DECORATED);
        return (InStockOrder) orderRepository.save(order);
    }

    public CustomOrder createCustomOrder(User client, User manager, UUID carId, BigDecimal price) {
        CustomOrder order = new CustomOrder(null, carId, client, manager, price, CustomOrder.Status.DECORATED);
        return (CustomOrder) orderRepository.save(order);
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId);
        if (order != null) {
            if (order instanceof InStockOrder) {
                ((InStockOrder) order).setStatus(InStockOrder.Status.CANCELLED);
            } else if (order instanceof CustomOrder) {
                ((CustomOrder) order).setStatus(CustomOrder.Status.CANCELLED);
            }
            orderRepository.save(order);
        }
    }

    public void markAsPaidAndSendToStorage(UUID orderId) {
        Order order = orderRepository.findById(orderId);
        if (order instanceof InStockOrder) {
            ((InStockOrder) order).setStatus(InStockOrder.Status.PAIDFOR);
        } else if (order instanceof CustomOrder) {
            ((CustomOrder) order).setStatus(CustomOrder.Status.PAIDFOR);
        }
        orderRepository.save(order);

        String payload = String.format("{\"orderId\":\"%s\", \"carId\":\"%s\"}", order.getId(), order.getCarId());
        outboxRepository.save(new OutboxEventJpaEntity("OrderSentForApproval", payload));
    }

    public void processStorageApproval(UUID orderId) {
        Order order = orderRepository.findById(orderId);
        if (order instanceof InStockOrder) {
            ((InStockOrder) order).setStatus(InStockOrder.Status.READYFORDELIVERY);
        } else if (order instanceof CustomOrder) {
            ((CustomOrder) order).setStatus(CustomOrder.Status.READYFORDELIVERY);
        }
        orderRepository.save(order);
    }

    public List<Order> getFilteredOrders(String username, List<String> roles) {
        if (roles != null && (roles.contains("MANAGER") || roles.contains("ADMIN"))) {
            return orderRepository.findAll();
        }
        return orderRepository.findByClientUsername(username);
    }
}