package org.example.infrastructure.persistence.mapper;

import org.example.domain.entity.CustomOrder;
import org.example.domain.entity.InStockOrder;
import org.example.domain.entity.Order;
import org.example.infrastructure.persistence.entity.CustomOrderJpaEntity;
import org.example.infrastructure.persistence.entity.InStockOrderJpaEntity;
import org.example.infrastructure.persistence.entity.OrderJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserJpaMapper.class, CarJpaMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderJpaMapper {

    default Order toDomain(OrderJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof InStockOrderJpaEntity) {
            return toDomainInStock((InStockOrderJpaEntity) entity);
        } else if (entity instanceof CustomOrderJpaEntity) {
            return toDomainCustom((CustomOrderJpaEntity) entity);
        }
        throw new IllegalArgumentException("Неизвестный тип: " + entity.getClass().getName());
    }

    default OrderJpaEntity toJpa(Order domain) {
        if (domain == null) {
            return null;
        }
        if (domain instanceof InStockOrder) {
            return toJpaInStock((InStockOrder) domain);
        } else if (domain instanceof CustomOrder) {
            return toJpaCustom((CustomOrder) domain);
        }
        throw new IllegalArgumentException("Неизвестный тип: " + domain.getClass().getName());
    }

    InStockOrder toDomainInStock(InStockOrderJpaEntity entity);
    CustomOrder toDomainCustom(CustomOrderJpaEntity entity);

    InStockOrderJpaEntity toJpaInStock(InStockOrder domain);
    CustomOrderJpaEntity toJpaCustom(CustomOrder domain);
}