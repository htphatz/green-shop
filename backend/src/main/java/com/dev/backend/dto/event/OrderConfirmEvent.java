package com.dev.backend.dto.event;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmEvent {
    private String eventId;
    private String orderId;
    private String email;
}
