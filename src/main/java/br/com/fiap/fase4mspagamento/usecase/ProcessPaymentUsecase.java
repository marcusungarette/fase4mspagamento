package br.com.fiap.fase4mspagamento.usecase;

import br.com.fiap.fase4mspagamento.controller.mapper.PaymentMapper;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import br.com.fiap.fase4mspagamento.gateway.PaymentGateway;
import br.com.fiap.fase4mspagamento.port.ExternalPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProcessPaymentUsecase {
    private static final Logger logger = LoggerFactory.getLogger(ProcessPaymentUsecase.class);

    private final PaymentGateway paymentGateway;
    private final PaymentMapper paymentMapper;
    private final ExternalPaymentService externalPaymentService;

    public ProcessPaymentUsecase(
            PaymentGateway paymentGateway,
            PaymentMapper paymentMapper,
            ExternalPaymentService externalPaymentService) {
        this.paymentGateway = paymentGateway;
        this.paymentMapper = paymentMapper;
        this.externalPaymentService = externalPaymentService;

        logger.info("ProcessPaymentUsecase construído com externalPaymentService: {}",
                externalPaymentService.getClass().getName());
    }

    @Transactional
    public Payment execute(Payment payment) {
        logger.info("Iniciando processamento de pagamento");

        // Gerar ID externo único
        String externalId = "PAYER-" + UUID.randomUUID().toString();
        logger.info("Pagamento recebeu ID externo: {}", externalId);

        // Criar novo payment com externalId - iniciamos com PENDING
        Payment newPayment = new Payment(
                payment.getId(),
                externalId,
                payment.getAmount(),
                payment.getCreditCardNumber(),
                payment.getOrderId(),
                payment.getCallbackUrl(),
                PaymentStatus.PENDING,  // Status inicial é PENDING
                "Pagamento enviado para processamento",
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );

        // Salvar o pagamento com status inicial PENDING
        Payment savedPayment = paymentGateway.save(newPayment);
        logger.info("Pagamento salvo com ID: {} e externalId: {}", savedPayment.getId(), savedPayment.getExternalId());

        // Enviar para o serviço externo (mock) via adapter
        try {
            logger.info("Enviando pagamento para processamento externo");
            String transactionId = externalPaymentService.processPayment(savedPayment);
            logger.info("Pagamento enviado para processamento externo, transactionId: {}", transactionId);

            // Como nosso mock sempre aprova o pagamento imediatamente,
            // podemos verificar o status e atualizar no banco de dados
            // Em um cenário real, isso seria feito pelo callback
            String status = externalPaymentService.checkStatus(transactionId);

            if (status.equals(PaymentStatus.APPROVED.name())) {
                logger.info("Pagamento aprovado pelo serviço externo, atualizando no banco");

                // Criar versão atualizada do pagamento
                Payment updatedPayment = new Payment(
                        savedPayment.getId(),
                        savedPayment.getExternalId(),
                        savedPayment.getAmount(),
                        savedPayment.getCreditCardNumber(),
                        savedPayment.getOrderId(),
                        savedPayment.getCallbackUrl(),
                        PaymentStatus.APPROVED,  // Status atualizado
                        "Pagamento aprovado pelo serviço externo",  // Mensagem atualizada
                        savedPayment.getCreatedAt(),
                        LocalDateTime.now()  // Timestamp atualizado
                );

                // Salvar a versão atualizada
                savedPayment = paymentGateway.save(updatedPayment);
                logger.info("Pagamento atualizado com status: {}", savedPayment.getStatus());
            }

        } catch (Exception e) {
            logger.error("Erro ao enviar pagamento para processamento externo", e);

            // Atualizar o pagamento com status de erro
            Payment errorPayment = new Payment(
                    savedPayment.getId(),
                    savedPayment.getExternalId(),
                    savedPayment.getAmount(),
                    savedPayment.getCreditCardNumber(),
                    savedPayment.getOrderId(),
                    savedPayment.getCallbackUrl(),
                    PaymentStatus.REJECTED,  // Status de erro
                    "Erro ao processar pagamento: " + e.getMessage(),
                    savedPayment.getCreatedAt(),
                    LocalDateTime.now()
            );

            savedPayment = paymentGateway.save(errorPayment);
            logger.info("Pagamento atualizado com status de erro: {}", savedPayment.getStatus());
        }

        return savedPayment;
    }
}