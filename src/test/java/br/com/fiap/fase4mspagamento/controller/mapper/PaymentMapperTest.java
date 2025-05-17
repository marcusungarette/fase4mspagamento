package br.com.fiap.fase4mspagamento.controller.mapper;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentNotification;
import br.com.fiap.fase4mspagamento.controller.dto.PaymentRequest;
import br.com.fiap.fase4mspagamento.controller.dto.PaymentResponse;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentMapperTest {

    private PaymentMapper mapper;
    private PaymentRequest paymentRequest;
    private Payment payment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        mapper = new PaymentMapper();

        // Setup test data
        BigDecimal amount = new BigDecimal("100.50");
        String creditCardNumber = "4111111111111111";
        String orderId = "ORDER-123";
        String callbackUrl = "http://example.com/callback";

        paymentRequest = new PaymentRequest(amount, creditCardNumber, orderId, callbackUrl);

        createdAt = LocalDateTime.now().minusHours(1);
        updatedAt = LocalDateTime.now();
        payment = new Payment(
                1L,
                "PAY-XYZ",
                amount,
                creditCardNumber,
                orderId,
                callbackUrl,
                PaymentStatus.APPROVED,
                "Payment approved",
                createdAt,
                updatedAt
        );
    }

    @Test
    void toDomain_ShouldMapRequestToPaymentEntity() {
        // Act
        Payment result = mapper.toDomain(paymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals(paymentRequest.getAmount(), result.getAmount());
        assertEquals(paymentRequest.getCreditCardNumber(), result.getCreditCardNumber());
        assertEquals(paymentRequest.getOrderId(), result.getOrderId());
        assertEquals(paymentRequest.getCallbackUrl(), result.getCallbackUrl());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertEquals("Processando pagamento", result.getMessage());

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void toResponse_ShouldMapPaymentEntityToResponse() {
        PaymentResponse result = mapper.toResponse(payment);

        assertNotNull(result);
        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getExternalId(), result.getExternalId());
        assertEquals(payment.getAmount(), result.getAmount());
        assertEquals(payment.getStatus(), result.getStatus());
        assertEquals(payment.getMessage(), result.getMessage());
        assertEquals(payment.getOrderId(), result.getOrderId());
        assertEquals(payment.getCreatedAt(), result.getCreatedAt());
        assertEquals(payment.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void toNotification_ShouldMapPaymentEntityToNotification() {
        PaymentNotification result = mapper.toNotification(payment);

        assertNotNull(result);
        assertEquals(payment.getId(), result.getPaymentId());
        assertEquals(payment.getExternalId(), result.getExternalId());
        assertEquals(payment.getStatus(), result.getStatus());
        assertEquals(payment.getMessage(), result.getMessage());
        assertEquals(payment.getOrderId(), result.getOrderId());
    }
}