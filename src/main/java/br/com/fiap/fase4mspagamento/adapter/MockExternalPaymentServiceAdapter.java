package br.com.fiap.fase4mspagamento.adapter;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentNotification;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import br.com.fiap.fase4mspagamento.port.ExternalPaymentService;
import br.com.fiap.fase4mspagamento.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Versão simplificada do mock de serviço externo de pagamento
 */
@Component
public class MockExternalPaymentServiceAdapter implements ExternalPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MockExternalPaymentServiceAdapter.class);
    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MockExternalPaymentServiceAdapter(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String processPayment(Payment payment) {
        logger.info("Iniciando processamento de pagamento via serviço externo mock");
        // Agendar envio de callback após 10 segundos (simulando processamento assíncrono)
        scheduler.schedule(() -> processPaymentAsync(payment), 10, TimeUnit.SECONDS);
        // Retornar um ID qualquer, já que não precisamos rastreá-lo no mock
        return "MOCK-TRANSACTION";
    }

    @Override
    public String checkStatus(String transactionId) {
        // Para simplificar, sempre retornar APPROVED
        return PaymentStatus.APPROVED.name();
    }

    /**
     * Processa o pagamento de forma assíncrona e envia o callback
     */
    private void processPaymentAsync(Payment payment) {
        try {
            logger.info("Processando pagamento assincronamente: external_id={}, order_id={}",
                    payment.getExternalId(), payment.getOrderId());

            // No mock, sempre aprovar o pagamento
            PaymentStatus newStatus = PaymentStatus.APPROVED;
            String message = "Pagamento aprovado pelo serviço externo";

            // Criar a notificação para o callback
            PaymentNotification notification = new PaymentNotification(
                    null,  // ID interno, não relevante para o callback
                    payment.getExternalId(),
                    newStatus,
                    message,
                    payment.getOrderId()
            );

            // Enviar a notificação usando o NotificationService
            logger.info("Enviando notificação para o callback URL: {}", payment.getCallbackUrl());
            notificationService.sendNotification(payment.getCallbackUrl(), notification);

        } catch (Exception e) {
            logger.error("Erro ao processar pagamento assincronamente", e);
        }
    }
}