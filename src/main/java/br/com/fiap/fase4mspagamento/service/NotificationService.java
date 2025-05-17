package br.com.fiap.fase4mspagamento.service;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final RestTemplate restTemplate;

    public NotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendNotification(String callbackUrl, PaymentNotification notification) {
        logger.info("Enviando notificação para: {} com status: {}", callbackUrl, notification.getStatus());

        try {
            HttpEntity<PaymentNotification> request = new HttpEntity<>(notification);
            ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Notificação enviada com sucesso");
            } else {
                logger.error("Erro ao enviar notificação: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Falha ao enviar notificação", e);
        }
    }
}