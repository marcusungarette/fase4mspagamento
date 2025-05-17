package br.com.fiap.fase4mspagamento.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void constructor_ShouldInitializeAllFields_WhenAllParametersProvided() {
        Long id = 1L;
        String externalId = "PAY-123";
        BigDecimal amount = new BigDecimal("100.50");
        String creditCardNumber = "4111111111111111";
        String orderId = "ORDER-456";
        String callbackUrl = "http://example.com/callback";
        PaymentStatus status = PaymentStatus.APPROVED;
        String message = "Payment approved";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        Payment payment = new Payment(
                id,
                externalId,
                amount,
                creditCardNumber,
                orderId,
                callbackUrl,
                status,
                message,
                createdAt,
                updatedAt
        );

        assertEquals(id, payment.getId());
        assertEquals(externalId, payment.getExternalId());
        assertEquals(amount, payment.getAmount());
        assertEquals(creditCardNumber, payment.getCreditCardNumber());
        assertEquals(orderId, payment.getOrderId());
        assertEquals(callbackUrl, payment.getCallbackUrl());
        assertEquals(status, payment.getStatus());
        assertEquals(message, payment.getMessage());
        assertEquals(createdAt, payment.getCreatedAt());
        assertEquals(updatedAt, payment.getUpdatedAt());
    }

    @Test
    void constructor_ShouldInitializeWithDefaultValues_WhenCreatedWithMinimalParameters() {
        BigDecimal amount = new BigDecimal("150.75");
        String creditCardNumber = "5555555555554444";
        String orderId = "ORDER-789";
        String callbackUrl = "http://example.com/callback2";

        Payment payment = new Payment(amount, creditCardNumber, orderId, callbackUrl);

        assertNull(payment.getId());
        assertNull(payment.getExternalId());
        assertEquals(amount, payment.getAmount());
        assertEquals(creditCardNumber, payment.getCreditCardNumber());
        assertEquals(orderId, payment.getOrderId());
        assertEquals(callbackUrl, payment.getCallbackUrl());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals("Processando pagamento", payment.getMessage());
        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());

        LocalDateTime now = LocalDateTime.now();
        assertTrue(payment.getCreatedAt().isAfter(now.minusSeconds(2)));
        assertTrue(payment.getCreatedAt().isBefore(now.plusSeconds(2)));
        assertTrue(payment.getUpdatedAt().isAfter(now.minusSeconds(2)));
        assertTrue(payment.getUpdatedAt().isBefore(now.plusSeconds(2)));
    }

    @Test
    void constructor_ShouldCreateEmptyObject_WhenNoArgsConstructorUsed() {
        Payment payment = new Payment();

        assertNull(payment.getId());
        assertNull(payment.getExternalId());
        assertNull(payment.getAmount());
        assertNull(payment.getCreditCardNumber());
        assertNull(payment.getOrderId());
        assertNull(payment.getCallbackUrl());
        assertNull(payment.getStatus());
        assertNull(payment.getMessage());
        assertNull(payment.getCreatedAt());
        assertNull(payment.getUpdatedAt());
    }

    @Test
    void withStatus_ShouldCreateNewPaymentWithUpdatedStatusAndMessage() {
        Payment originalPayment = new Payment(
                1L,
                "PAY-123",
                new BigDecimal("100.50"),
                "4111111111111111",
                "ORDER-456",
                "http://example.com/callback",
                PaymentStatus.PENDING,
                "Processing payment",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusMinutes(30)
        );

        PaymentStatus newStatus = PaymentStatus.APPROVED;
        String newMessage = "Payment approved";

        Payment updatedPayment = originalPayment.withStatus(newStatus, newMessage);

        assertEquals(newStatus, updatedPayment.getStatus());
        assertEquals(newMessage, updatedPayment.getMessage());
        assertTrue(updatedPayment.getUpdatedAt().isAfter(originalPayment.getUpdatedAt()));

        assertEquals(originalPayment.getId(), updatedPayment.getId());
        assertEquals(originalPayment.getExternalId(), updatedPayment.getExternalId());
        assertEquals(originalPayment.getAmount(), updatedPayment.getAmount());
        assertEquals(originalPayment.getCreditCardNumber(), updatedPayment.getCreditCardNumber());
        assertEquals(originalPayment.getOrderId(), updatedPayment.getOrderId());
        assertEquals(originalPayment.getCallbackUrl(), updatedPayment.getCallbackUrl());
        assertEquals(originalPayment.getCreatedAt(), updatedPayment.getCreatedAt());
    }

}