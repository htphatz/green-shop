package com.dev.backend.service.impl;

import com.dev.backend.dto.brevo.EmailReq;
import com.dev.backend.dto.brevo.ParamReq;
import com.dev.backend.dto.brevo.ToReq;
import com.dev.backend.dto.event.OrderConfirmEvent;
import com.dev.backend.repository.httpclient.BrevoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final BrevoClient brevoClient;

    @Value(value = "${brevo.api-key}")
    private String apiKey;

    @Value(value = "${brevo.template-id}")
    private Integer templateId;

    @KafkaListener(topics = "order-confirmed", groupId = "my-group")
    public void sendEmail(OrderConfirmEvent event) {
        ToReq to = ToReq.builder()
                .email(event.getEmail())
                .build();

        ParamReq params = ParamReq.builder()
                .orderId(event.getOrderId())
                .build();

        EmailReq request = EmailReq.builder()
                .to(List.of(to))
                .templateId(templateId)
                .params(params)
                .build();

        brevoClient.sendEmail(apiKey, request);
    }
}
