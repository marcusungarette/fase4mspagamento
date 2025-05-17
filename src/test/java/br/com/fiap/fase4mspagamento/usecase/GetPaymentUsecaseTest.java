package br.com.fiap.fase4mspagamento.usecase;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import br.com.fiap.fase4mspagamento.exception.PaymentNotFoundException;
import br.com.fiap.fase4mspagamento.gateway.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPaymentUsecaseTest {

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private GetPaymentUsecase usecase;

    private Payment payment;
    private final Long paymentId = 1L;
    private final String externalId = "PAY-XYZ";
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        payment = new Payment(
                paymentId,
                externalId,
                new BigDecimal("100.50"),
                "4111111111111111",
                "ORDER-123",
                "http://example.com/callback",
                PaymentStatus.APPROVED,
                "Payment approved",
                now.minusHours(1),
                now
        );
    }

    @Test
    void execute_ShouldReturnPayment_WhenPaymentExists() {
        when(paymentGateway.findById(paymentId)).thenReturn(Optional.of(payment));

        Payment result = usecase.execute(paymentId);

        assertNotNull(result);
        assertEquals(payment, result);
    }

    @Test
    void execute_ShouldThrowException_WhenPaymentDoesNotExist() {
        when(paymentGateway.findById(anyLong())).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () -> {
            usecase.execute(999L);
        });

        assertTrue(exception.getMessage().contains("Pagamento não encontrado com ID: 999"));
    }

    @Test
    void executeByExternalId_ShouldReturnPayment_WhenPaymentExists() {
        when(paymentGateway.findByExternalId(externalId)).thenReturn(Optional.of(payment));

        Payment result = usecase.executeByExternalId(externalId);

        assertNotNull(result);
        assertEquals(payment, result);
    }

    @Test
    void executeByExternalId_ShouldThrowException_WhenPaymentDoesNotExist() {
        when(paymentGateway.findByExternalId(anyString())).thenReturn(Optional.empty());

        String nonExistentId = "NON-EXISTENT";

        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () -> {
            usecase.executeByExternalId(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("Pagamento não encontrado com ID externo: " + nonExistentId));
    }
}