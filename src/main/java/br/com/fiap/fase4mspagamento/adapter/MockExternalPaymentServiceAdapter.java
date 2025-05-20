package br.com.fiap.fase4mspagamento.adapter;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentNotification;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import br.com.fiap.fase4mspagamento.port.ExternalPaymentService;
import br.com.fiap.fase4mspagamento.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Versão simplificada do mock de serviço externo de pagamento
 */
@Component
public class MockExternalPaymentServiceAdapter implements ExternalPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(MockExternalPaymentServiceAdapter.class);
    private static final BigDecimal LIMIT_VALUE = new BigDecimal("10000.00");

    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Armazenar pagamentos processados e seus status
    private final Map<String, Payment> processedPayments = new HashMap<>();
    private final Map<String, String> transactionStatuses = new HashMap<>();

    public MockExternalPaymentServiceAdapter(NotificationService notificationService) {
        this.notificationService = notificationService;
        logger.info("MockExternalPaymentServiceAdapter inicializado com limite de R$ {}", LIMIT_VALUE);
    }

    @Override
    public String processPayment(Payment payment) {
        logger.info("Iniciando processamento de pagamento via serviço externo mock");

        // Gerar um ID de transação único
        String transactionId = "MOCK-TRANS-" + java.util.UUID.randomUUID().toString();

        // Armazenar o pagamento para uso posterior
        processedPayments.put(transactionId, payment);

        // Determinar o status com base no valor do pagamento
        String status = determineStatus(payment);
        transactionStatuses.put(transactionId, status);

        // Agendar envio de callback após 10 segundos (simulando processamento assíncrono)
        scheduler.schedule(() -> processPaymentAsync(payment, transactionId), 10, TimeUnit.SECONDS);

        return transactionId;
    }

    @Override
    public String checkStatus(String transactionId) {
        // Retornar o status armazenado ou PENDING se não existir
        String status = transactionStatuses.getOrDefault(transactionId, PaymentStatus.PENDING.name());
        logger.info("Verificando status do transactionId {}: {}", transactionId, status);
        return status;
    }

    // Determinar o status com base no valor do pagamento
    private String determineStatus(Payment payment) {
        boolean isApproved = payment.getAmount().compareTo(LIMIT_VALUE) <= 0;

        if (isApproved) {
            logger.info("Valor {} está abaixo do limite {}: será APPROVED",
                    payment.getAmount(), LIMIT_VALUE);
            return PaymentStatus.APPROVED.name();
        } else {
            logger.warn("Valor {} excede o limite {}: será REJECTED",
                    payment.getAmount(), LIMIT_VALUE);
            return PaymentStatus.REJECTED.name();
        }
    }

    /**
     * Processa o pagamento de forma assíncrona e envia o callback
     */
    private void processPaymentAsync(Payment payment, String transactionId) {
        try {
            logger.info("Processando pagamento assincronamente: external_id={}, order_id={}, amount={}",
                    payment.getExternalId(), payment.getOrderId(), payment.getAmount());

            // Verificar se o valor do pagamento excede o limite
            boolean isApproved = payment.getAmount().compareTo(LIMIT_VALUE) <= 0;

            PaymentStatus newStatus;
            String message;

            if (isApproved) {
                newStatus = PaymentStatus.APPROVED;
                message = "Pagamento aprovado pelo serviço externo";
                logger.info("Pagamento aprovado: valor dentro do limite permitido");
            } else {
                newStatus = PaymentStatus.REJECTED;
                message = "Pagamento rejeitado pelo serviço externo: valor excede o limite de R$ " + LIMIT_VALUE;
                logger.warn("Pagamento rejeitado: valor {} excede o limite de {}", payment.getAmount(), LIMIT_VALUE);
            }

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