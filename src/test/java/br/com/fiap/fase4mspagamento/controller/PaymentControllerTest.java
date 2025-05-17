package br.com.fiap.fase4mspagamento.controller;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentRequest;
import br.com.fiap.fase4mspagamento.controller.dto.PaymentResponse;
import br.com.fiap.fase4mspagamento.controller.mapper.PaymentMapper;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import br.com.fiap.fase4mspagamento.usecase.GetPaymentUsecase;
import br.com.fiap.fase4mspagamento.usecase.ProcessPaymentUsecase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private ProcessPaymentUsecase processPaymentUsecase;

    @Mock
    private GetPaymentUsecase getPaymentUsecase;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentRequest paymentRequest;
    private Payment payment;
    private Payment processedPayment;
    private PaymentResponse paymentResponse;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Criar PaymentRequest usando o construtor com todos os argumentos
        BigDecimal amount = new BigDecimal("100.50");
        String creditCardNumber = "4111111111111111";
        String orderId = "ORDER-123";
        String callbackUrl = "http://example.com/callback";
        paymentRequest = new PaymentRequest(amount, creditCardNumber, orderId, callbackUrl);

        // Setup Payment entity (initial)
        payment = new Payment(
                null, // No ID yet
                null, // No externalId yet
                amount,
                creditCardNumber,
                orderId,
                callbackUrl,
                PaymentStatus.PENDING,
                "Processando pagamento",
                now,
                now
        );

        // Setup processed Payment (after going through external service)
        processedPayment = new Payment(
                1L,
                "PAY-XYZ",
                amount,
                creditCardNumber,
                orderId,
                callbackUrl,
                PaymentStatus.PENDING, // Status still pending as actual processing is async
                "Pagamento enviado para processamento",
                now,
                now
        );

        // Setup PaymentResponse
        paymentResponse = new PaymentResponse(
                1L,
                "PAY-XYZ",
                amount,
                PaymentStatus.PENDING,
                "Pagamento enviado para processamento",
                orderId,
                now,
                now
        );
    }

    @Test
    void createPayment_ShouldReturnAcceptedStatus_WhenPaymentIsProcessed() {
        // Arrange
        when(paymentMapper.toDomain(any(PaymentRequest.class))).thenReturn(payment);
        when(processPaymentUsecase.execute(any(Payment.class))).thenReturn(processedPayment);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.createPayment(paymentRequest);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(paymentResponse, response.getBody());

        // Verify interactions with mocks
        verify(paymentMapper).toDomain(paymentRequest);
        verify(processPaymentUsecase).execute(payment);
        verify(paymentMapper).toResponse(processedPayment);
    }

    @Test
    void getPayment_ShouldReturnOkStatus_WhenPaymentExists() {
        // Arrange
        Long paymentId = 1L;
        when(getPaymentUsecase.execute(anyLong())).thenReturn(processedPayment);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.getPayment(paymentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentResponse, response.getBody());

        // Verify interactions with mocks
        verify(getPaymentUsecase).execute(paymentId);
        verify(paymentMapper).toResponse(processedPayment);
    }

    @Test
    void getPayment_ShouldThrowException_WhenPaymentDoesNotExist() {
        // Arrange
        Long paymentId = 999L;
        when(getPaymentUsecase.execute(anyLong())).thenThrow(new RuntimeException("Payment not found with id: " + paymentId));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentController.getPayment(paymentId));

        // Verify interactions with mocks
        verify(getPaymentUsecase).execute(paymentId);
        verify(paymentMapper, never()).toResponse(any(Payment.class));
    }

    @Test
    void getPaymentByExternalId_ShouldReturnOkStatus_WhenPaymentExists() {
        // Arrange
        String externalId = "PAY-XYZ";
        when(getPaymentUsecase.executeByExternalId(anyString())).thenReturn(processedPayment);
        when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.getPaymentByExternalId(externalId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentResponse, response.getBody());

        // Verify interactions with mocks
        verify(getPaymentUsecase).executeByExternalId(externalId);
        verify(paymentMapper).toResponse(processedPayment);
    }

    @Test
    void getPaymentByExternalId_ShouldThrowException_WhenPaymentDoesNotExist() {
        // Arrange
        String externalId = "NON-EXISTENT";
        when(getPaymentUsecase.executeByExternalId(anyString())).thenThrow(new RuntimeException("Payment not found with externalId: " + externalId));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentController.getPaymentByExternalId(externalId));

        // Verify interactions with mocks
        verify(getPaymentUsecase).executeByExternalId(externalId);
        verify(paymentMapper, never()).toResponse(any(Payment.class));
    }
}