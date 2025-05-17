package br.com.fiap.fase4mspagamento.usecase;

import br.com.fiap.fase4mspagamento.controller.mapper.PaymentMapper;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import br.com.fiap.fase4mspagamento.gateway.PaymentGateway;
import br.com.fiap.fase4mspagamento.port.ExternalPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentUsecaseTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private ExternalPaymentService externalPaymentService;

    @InjectMocks
    private ProcessPaymentUsecase usecase;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    private Payment initialPayment;
    private Payment savedPayment;
    private Payment updatedPayment;
    private final LocalDateTime now = LocalDateTime.now();
    private final String mockTransactionId = "MOCK-TRANS-123";

    @BeforeEach
    void setUp() {
        initialPayment = new Payment(
                null, // No ID yet
                null, // No externalId yet
                new BigDecimal("100.50"),
                "4111111111111111",
                "ORDER-123",
                "http://example.com/callback",
                PaymentStatus.PENDING,
                "Processando pagamento",
                now,
                now
        );

        // Setup payment after initial save (with ID and externalId)
        savedPayment = new Payment(
                1L,
                "PAYER-" + UUID.randomUUID().toString(),
                new BigDecimal("100.50"),
                "4111111111111111",
                "ORDER-123",
                "http://example.com/callback",
                PaymentStatus.PENDING,
                "Pagamento enviado para processamento",
                now,
                now
        );

        // Setup payment after status update (APPROVED)
        updatedPayment = new Payment(
                1L,
                savedPayment.getExternalId(),
                new BigDecimal("100.50"),
                "4111111111111111",
                "ORDER-123",
                "http://example.com/callback",
                PaymentStatus.APPROVED,
                "Pagamento aprovado pelo serviço externo",
                now,
                now.plusMinutes(5)
        );
    }

    @Test
    void execute_ShouldProcessPaymentSuccessfully_WhenExternalServiceApproves() {
        when(paymentGateway.save(any(Payment.class)))
                .thenReturn(savedPayment)  // First save
                .thenReturn(updatedPayment); // Second save after status update

        when(externalPaymentService.processPayment(any(Payment.class))).thenReturn(mockTransactionId);
        when(externalPaymentService.checkStatus(mockTransactionId)).thenReturn(PaymentStatus.APPROVED.name());

        Payment result = usecase.execute(initialPayment);

        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals("Pagamento aprovado pelo serviço externo", result.getMessage());
        assertEquals(updatedPayment, result);

        verify(paymentGateway, times(2)).save(paymentCaptor.capture());

        Payment firstSavedPayment = paymentCaptor.getAllValues().get(0);
        assertNotNull(firstSavedPayment.getExternalId());
        assertTrue(firstSavedPayment.getExternalId().startsWith("PAYER-"));
        assertEquals(PaymentStatus.PENDING, firstSavedPayment.getStatus());
        assertEquals("Pagamento enviado para processamento", firstSavedPayment.getMessage());

        Payment secondSavedPayment = paymentCaptor.getAllValues().get(1);
        assertEquals(PaymentStatus.APPROVED, secondSavedPayment.getStatus());
        assertEquals("Pagamento aprovado pelo serviço externo", secondSavedPayment.getMessage());

        verify(externalPaymentService).processPayment(any(Payment.class));
        verify(externalPaymentService).checkStatus(mockTransactionId);
    }


    @Test
    void execute_ShouldUpdatePaymentToRejected_WhenExternalServiceThrowsException() {
        Payment errorPayment = new Payment(
                1L,
                savedPayment.getExternalId(),
                new BigDecimal("100.50"),
                "4111111111111111",
                "ORDER-123",
                "http://example.com/callback",
                PaymentStatus.REJECTED,
                "Erro ao processar pagamento: Connection error",
                now,
                now.plusMinutes(5)
        );

        when(paymentGateway.save(any(Payment.class)))
                .thenReturn(savedPayment)   // First save
                .thenReturn(errorPayment);  // Save after error

        when(externalPaymentService.processPayment(any(Payment.class))).thenThrow(new RuntimeException("Connection error"));

        Payment result = usecase.execute(initialPayment);

        assertEquals(PaymentStatus.REJECTED, result.getStatus());
        assertTrue(result.getMessage().contains("Connection error"));

        verify(paymentGateway, times(2)).save(any(Payment.class));
        verify(externalPaymentService).processPayment(any(Payment.class));
        verify(externalPaymentService, never()).checkStatus(anyString());
    }

}