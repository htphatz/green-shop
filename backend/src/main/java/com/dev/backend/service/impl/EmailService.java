package com.dev.backend.service.impl;

import com.dev.backend.dto.brevo.EmailReq;
import com.dev.backend.dto.brevo.ParamReq;
import com.dev.backend.dto.brevo.ToReq;
import com.dev.backend.dto.event.OrderConfirmEvent;
import com.dev.backend.repository.httpclient.BrevoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final BrevoClient brevoClient;
    private final StringRedisTemplate redisTemplate;

    @Value(value = "${brevo.api-key}")
    private String apiKey;

    @Value(value = "${brevo.template-id}")
    private Integer templateId;

    @KafkaListener(topics = "order-confirmed", groupId = "email-group")
    public void sendEmail(OrderConfirmEvent event, Acknowledgment ack) {
        String redisKey = "processed_event:email:" + event.getEventId();

        // 1. Idempotency Check using Redis SETNX (7-day TTL)
        Boolean isFirstTime = redisTemplate.opsForValue().setIfAbsent(redisKey, "PROCESSED", Duration.ofDays(7));

        if (Boolean.FALSE.equals(isFirstTime)) {
            log.warn("Duplicate event detected! Skipping email for eventId: {}, orderId: {}", event.getEventId(), event.getOrderId());
            ack.acknowledge(); // Commit offset and skip duplicate message
            return;
        }

        try {
            log.info("Processing send email for orderId: {}, email: {}", event.getOrderId(), event.getEmail());

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

            // 2. Manually commit offset upon 100% success
            ack.acknowledge();
            log.info("Email sent successfully and offset committed for orderId: {}", event.getOrderId());

        } catch (Exception e) {
            // Delete Redis key on failure to allow retries
            redisTemplate.delete(redisKey);
            log.error("Failed to send email for orderId: {}, will trigger Kafka Retry / DLQ", event.getOrderId(), e);
            throw e; // Call ErrorHandler -> Retry 3 times -> Forward to .DLT
        }
    }

    // 3. Listener for Dead Letter Topic (.DLT) after retries are exhausted
    @KafkaListener(topics = "order-confirmed.DLT", groupId = "email-dlt-group")
    public void handleDlt(OrderConfirmEvent event, Acknowledgment ack) {
        log.error("CRITICAL ALARM: Message landed in DLT! EventId: {}, OrderId: {}, Email: {}",
                event.getEventId(), event.getOrderId(), event.getEmail());
        // Alert to Slack / Telegram or persist in DB for manual admin intervention
        ack.acknowledge();
    }
}
