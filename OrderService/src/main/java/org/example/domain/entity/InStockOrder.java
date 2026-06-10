package org.example.domain.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class InStockOrder extends Order {
    public enum Status { DECORATED, APPROVEDBYTHEWAREHOUSE, AWAITINGPAYMENT, PAIDFOR, AWAITINGCARDELIVERY, READYFORDELIVERY, COMPLETED, CANCELLED }
    private Status status;

    public InStockOrder(UUID id, UUID carId, User client, User manager, BigDecimal price, Status status) {
        super(id, carId, client, manager, price);
        this.status = status;
    }
}