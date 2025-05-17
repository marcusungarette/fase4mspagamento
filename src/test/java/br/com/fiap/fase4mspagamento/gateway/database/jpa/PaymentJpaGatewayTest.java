package br.com.fiap.fase4mspagamento.gateway.database.jpa;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import br.com.fiap.fase4mspagamento.gateway.database.jpa.entity.PaymentEntity;
import br.com.fiap.fase4mspagamento.gateway.database.jpa.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentJpaGatewayTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentJpaGateway paymentJpaGateway;

    @Captor
    private ArgumentCaptor<PaymentEntity> paymentEntityCaptor;

    private Payment payment;
    private PaymentEntity paymentEntity;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        payment = new Payment(
                1L,
                "PAY-123",
                new BigDecimal("100.50"),
                "4111111111111111",
                "ORDER-456",
                "http://example.com/callback",
                PaymentStatus.APPROVED,
                "Payment approved",
                now.minusHours(1),
                now
        );

        paymentEntity = new PaymentEntity(payment);
    }

    @Test
    void save_ShouldConvertAndSaveEntity() {
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(paymentEntity);

        Payment savedPayment = paymentJpaGateway.save(payment);

        verify(paymentRepository).save(paymentEntityCaptor.capture());
        PaymentEntity capturedEntity = paymentEntityCaptor.getValue();

        assertEquals(payment.getId(), capturedEntity.getId());
        assertEquals(payment.getExternalId(), capturedEntity.getExternalId());
        assertEquals(payment.getAmount(), capturedEntity.getAmount());
        assertEquals(payment.getCreditCardNumber(), capturedEntity.getCreditCardNumber());
        assertEquals(payment.getOrderId(), capturedEntity.getOrderId());
        assertEquals(payment.getCallbackUrl(), capturedEntity.getCallbackUrl());
        assertEquals(payment.getStatus(), capturedEntity.getStatus());
        assertEquals(payment.getMessage(), capturedEntity.getMessage());
        assertEquals(payment.getCreatedAt(), capturedEntity.getCreatedAt());
        assertEquals(payment.getUpdatedAt(), capturedEntity.getUpdatedAt());

        assertEquals(payment.getId(), savedPayment.getId());
        assertEquals(payment.getExternalId(), savedPayment.getExternalId());
        assertEquals(payment.getAmount(), savedPayment.getAmount());
        assertEquals(payment.getCreditCardNumber(), savedPayment.getCreditCardNumber());
        assertEquals(payment.getOrderId(), savedPayment.getOrderId());
        assertEquals(payment.getCallbackUrl(), savedPayment.getCallbackUrl());
        assertEquals(payment.getStatus(), savedPayment.getStatus());
        assertEquals(payment.getMessage(), savedPayment.getMessage());
        assertEquals(payment.getCreatedAt(), savedPayment.getCreatedAt());
        assertEquals(payment.getUpdatedAt(), savedPayment.getUpdatedAt());
    }

    @Test
    void findById_ShouldReturnPayment_WhenPaymentExists() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentEntity));

        Optional<Payment> result = paymentJpaGateway.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(payment.getId(), result.get().getId());
        assertEquals(payment.getExternalId(), result.get().getExternalId());
        verify(paymentRepository).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenPaymentDoesNotExist() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Payment> result = paymentJpaGateway.findById(999L);

        assertFalse(result.isPresent());
        verify(paymentRepository).findById(999L);
    }

    @Test
    void findByExternalId_ShouldReturnPayment_WhenPaymentExists() {
        String externalId = "PAY-123";
        when(paymentRepository.findByExternalId(externalId)).thenReturn(Optional.of(paymentEntity));

        Optional<Payment> result = paymentJpaGateway.findByExternalId(externalId);

        assertTrue(result.isPresent());
        assertEquals(payment.getId(), result.get().getId());
        assertEquals(payment.getExternalId(), result.get().getExternalId());
        verify(paymentRepository).findByExternalId(externalId);
    }

    @Test
    void findByExternalId_ShouldReturnEmptyOptional_WhenPaymentDoesNotExist() {
        String externalId = "NON-EXISTENT";
        when(paymentRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Optional<Payment> result = paymentJpaGateway.findByExternalId(externalId);

        assertFalse(result.isPresent());
        verify(paymentRepository).findByExternalId(externalId);
    }

    @Test
    void findAll_ShouldReturnAllPayments() {
        Payment payment2 = new Payment(
                2L,
                "PAY-456",
                new BigDecimal("200.75"),
                "5555555555554444",
                "ORDER-789",
                "http://example.org/callback",
                PaymentStatus.PENDING,
                "Processing payment",
                now.minusDays(1),
                now.minusHours(12)
        );

        PaymentEntity paymentEntity2 = new PaymentEntity(payment2);

        List<PaymentEntity> entities = Arrays.asList(paymentEntity, paymentEntity2);
        when(paymentRepository.findAll()).thenReturn(entities);

        List<Payment> results = paymentJpaGateway.findAll();

        assertEquals(2, results.size());
        assertEquals(payment.getId(), results.get(0).getId());
        assertEquals(payment.getExternalId(), results.get(0).getExternalId());
        assertEquals(payment2.getId(), results.get(1).getId());
        assertEquals(payment2.getExternalId(), results.get(1).getExternalId());
        verify(paymentRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoPaymentsExist() {
        when(paymentRepository.findAll()).thenReturn(List.of());

        List<Payment> results = paymentJpaGateway.findAll();

        assertTrue(results.isEmpty());
        verify(paymentRepository).findAll();
    }
}