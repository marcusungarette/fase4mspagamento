package br.com.fiap.fase4mspagamento.gateway.database.jpa.entity;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentEntityTest {

    @Test
    void constructor_ShouldCreateEmptyEntity_WhenNoArgsProvided() {
        PaymentEntity entity = new PaymentEntity();

        assertNull(entity.getId());
        assertNull(entity.getExternalId());
        assertNull(entity.getAmount());
        assertNull(entity.getCreditCardNumber());
        assertNull(entity.getOrderId());
        assertNull(entity.getCallbackUrl());
        assertNull(entity.getStatus());
        assertNull(entity.getMessage());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    void constructor_ShouldMapAllProperties_WhenPaymentProvided() {
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
                id, externalId, amount, creditCardNumber, orderId, callbackUrl,
                status, message, createdAt, updatedAt
        );

        PaymentEntity entity = new PaymentEntity(payment);

        assertEquals(id, entity.getId());
        assertEquals(externalId, entity.getExternalId());
        assertEquals(amount, entity.getAmount());
        assertEquals(creditCardNumber, entity.getCreditCardNumber());
        assertEquals(orderId, entity.getOrderId());
        assertEquals(callbackUrl, entity.getCallbackUrl());
        assertEquals(status, entity.getStatus());
        assertEquals(message, entity.getMessage());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    void toDomain_ShouldConvertEntityToPayment() {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(1L);
        entity.setExternalId("PAY-123");
        entity.setAmount(new BigDecimal("100.50"));
        entity.setCreditCardNumber("4111111111111111");
        entity.setOrderId("ORDER-456");
        entity.setCallbackUrl("http://example.com/callback");
        entity.setStatus(PaymentStatus.APPROVED);
        entity.setMessage("Payment approved");

        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        Payment payment = entity.toDomain();

        assertEquals(entity.getId(), payment.getId());
        assertEquals(entity.getExternalId(), payment.getExternalId());
        assertEquals(entity.getAmount(), payment.getAmount());
        assertEquals(entity.getCreditCardNumber(), payment.getCreditCardNumber());
        assertEquals(entity.getOrderId(), payment.getOrderId());
        assertEquals(entity.getCallbackUrl(), payment.getCallbackUrl());
        assertEquals(entity.getStatus(), payment.getStatus());
        assertEquals(entity.getMessage(), payment.getMessage());
        assertEquals(entity.getCreatedAt(), payment.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), payment.getUpdatedAt());
    }

    @Test
    void onCreate_ShouldSetCreatedAtAndUpdatedAt() throws Exception {
        PaymentEntity entity = new PaymentEntity();
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());

        Method onCreate = PaymentEntity.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(entity);

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());

        LocalDateTime now = LocalDateTime.now();
        assertTrue(entity.getCreatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(entity.getCreatedAt().isBefore(now.plusSeconds(1)));
        assertTrue(entity.getUpdatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(entity.getUpdatedAt().isBefore(now.plusSeconds(1)));
    }

    @Test
    void onUpdate_ShouldUpdateOnlyUpdatedAt() throws Exception {
        PaymentEntity entity = new PaymentEntity();
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusHours(1);
        entity.setCreatedAt(originalCreatedAt);
        entity.setUpdatedAt(originalCreatedAt); // Same as createdAt initially

        Method onUpdate = PaymentEntity.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(entity);

        assertEquals(originalCreatedAt, entity.getCreatedAt(), "CreatedAt should not change");
        assertNotEquals(originalCreatedAt, entity.getUpdatedAt(), "UpdatedAt should be different from original");

        LocalDateTime now = LocalDateTime.now();
        assertTrue(entity.getUpdatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(entity.getUpdatedAt().isBefore(now.plusSeconds(1)));
    }

    @Test
    void gettersAndSetters_ShouldWorkForAllFields() {
        PaymentEntity entity = new PaymentEntity();

        Long id = 2L;
        String externalId = "PAY-456";
        BigDecimal amount = new BigDecimal("200.75");
        String creditCardNumber = "5555555555554444";
        String orderId = "ORDER-789";
        String callbackUrl = "http://example.org/callback";
        PaymentStatus status = PaymentStatus.PENDING;
        String message = "Processing payment";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(12);

        entity.setId(id);
        entity.setExternalId(externalId);
        entity.setAmount(amount);
        entity.setCreditCardNumber(creditCardNumber);
        entity.setOrderId(orderId);
        entity.setCallbackUrl(callbackUrl);
        entity.setStatus(status);
        entity.setMessage(message);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(updatedAt);

        assertEquals(id, entity.getId());
        assertEquals(externalId, entity.getExternalId());
        assertEquals(amount, entity.getAmount());
        assertEquals(creditCardNumber, entity.getCreditCardNumber());
        assertEquals(orderId, entity.getOrderId());
        assertEquals(callbackUrl, entity.getCallbackUrl());
        assertEquals(status, entity.getStatus());
        assertEquals(message, entity.getMessage());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    void bidirectionalMapping_ShouldPreserveDomainValues() {
        Payment originalPayment = new Payment(
                3L,
                "PAY-789",
                new BigDecimal("300.25"),
                "3784123456789010",
                "ORDER-101112",
                "http://example.net/callback",
                PaymentStatus.REFUNDED,
                "Payment refunded",
                LocalDateTime.now().minusWeeks(1),
                LocalDateTime.now().minusDays(2)
        );

        PaymentEntity entity = new PaymentEntity(originalPayment);
        Payment convertedPayment = entity.toDomain();

        assertEquals(originalPayment.getId(), convertedPayment.getId());
        assertEquals(originalPayment.getExternalId(), convertedPayment.getExternalId());
        assertEquals(originalPayment.getAmount(), convertedPayment.getAmount());
        assertEquals(originalPayment.getCreditCardNumber(), convertedPayment.getCreditCardNumber());
        assertEquals(originalPayment.getOrderId(), convertedPayment.getOrderId());
        assertEquals(originalPayment.getCallbackUrl(), convertedPayment.getCallbackUrl());
        assertEquals(originalPayment.getStatus(), convertedPayment.getStatus());
        assertEquals(originalPayment.getMessage(), convertedPayment.getMessage());
        assertEquals(originalPayment.getCreatedAt(), convertedPayment.getCreatedAt());
        assertEquals(originalPayment.getUpdatedAt(), convertedPayment.getUpdatedAt());
    }
}