package org.example.infrastructure.persistence.repository;

import jakarta.persistence.criteria.Predicate;
import org.example.infrastructure.persistence.entity.CarJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CarSpecification {
    public static Specification<CarJpaEntity> filterCars(String brand, String partName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (brand != null && !brand.isBlank()) {
                predicates.add(cb.equal(root.get("brand"), brand));
            }
            if (partName != null && !partName.isBlank()) {
                Predicate wheelsMatch = cb.equal(root.join("wheels").get("name"), partName);
                Predicate transMatch = cb.equal(root.join("transmission").get("name"), partName);
                Predicate interiorMatch = cb.equal(root.join("interior").get("name"), partName);
                Predicate steeringMatch = cb.equal(root.join("steeringWheel").get("name"), partName);
                predicates.add(cb.or(wheelsMatch, transMatch, interiorMatch, steeringMatch));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}