package com.notification.controller;

import com.event.dto.OrderNotificationEvent;
import com.notification.dto.request.Recipient;
import com.notification.dto.request.SendEmailRequest;
import com.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderNotificationController {

    EmailService emailService;

    @KafkaListener(topics = "order-delivery")
    public void listenOrderNotificationDelivery(OrderNotificationEvent message){
        log.info("Message received: {}", message);
        emailService.sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder()
                        .email(message.getRecipient())
                        .build())
                .subject(message.getSubject())
                .htmlContent(message.getBody())
                .build());
    }
}
