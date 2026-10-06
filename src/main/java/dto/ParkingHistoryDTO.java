package dto;

import java.time.LocalDateTime;

public record ParkingHistoryDTO(
        String licensePlate,
        String zone,
        LocalDateTime startTime,
        LocalDateTime endTime,
        double amount) {
}