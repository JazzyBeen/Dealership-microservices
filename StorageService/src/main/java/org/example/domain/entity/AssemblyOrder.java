package org.example.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AssemblyOrder {
    private UUID id;
    private UUID sourceOrderId;
    private UUID carId;
    public enum Status { CREATED, ASSEMBLED, FAIL }
    private Status status;
}