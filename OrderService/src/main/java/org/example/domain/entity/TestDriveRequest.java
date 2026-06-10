package org.example.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TestDriveRequest {
    private UUID id;
    private User client;
    private UUID carId;
    private LocalDateTime dateTime;
}