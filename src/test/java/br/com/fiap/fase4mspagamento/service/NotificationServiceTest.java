package br.com.fiap.fase4mspagamento.service;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentNotification;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<HttpEntity<PaymentNotification>> requestCaptor;

    private PaymentNotification notification;
    private String callbackUrl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "restTemplate", restTemplate);

        callbackUrl = "http://example.com/callback";
        notification = new PaymentNotification(
                1L,
                "PAY-123",
                PaymentStatus.APPROVED,
                "Payment approved",
                "ORDER-456"
        );
    }

    @Test
    void sendNotification_ShouldSendCorrectRequest_WhenSuccessful() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        notificationService.sendNotification(callbackUrl, notification);

        verify(restTemplate).postForEntity(eq(callbackUrl), requestCaptor.capture(), eq(String.class));

        // Verify request body
        HttpEntity<PaymentNotification> capturedRequest = requestCaptor.getValue();
        PaymentNotification capturedNotification = capturedRequest.getBody();

        assert capturedNotification != null;
        assert capturedNotification.getPaymentId().equals(notification.getPaymentId());
        assert capturedNotification.getExternalId().equals(notification.getExternalId());
        assert capturedNotification.getStatus().equals(notification.getStatus());
        assert capturedNotification.getMessage().equals(notification.getMessage());
        assert capturedNotification.getOrderId().equals(notification.getOrderId());
    }

    @Test
    void sendNotification_ShouldHandleNonOkResponse() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        assertDoesNotThrow(() -> notificationService.sendNotification(callbackUrl, notification));

        verify(restTemplate).postForEntity(eq(callbackUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void sendNotification_ShouldHandleClientError() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertDoesNotThrow(() -> notificationService.sendNotification(callbackUrl, notification));

        verify(restTemplate).postForEntity(eq(callbackUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void sendNotification_ShouldHandleServerError() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertDoesNotThrow(() -> notificationService.sendNotification(callbackUrl, notification));

        verify(restTemplate).postForEntity(eq(callbackUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void sendNotification_ShouldHandleConnectionError() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        assertDoesNotThrow(() -> notificationService.sendNotification(callbackUrl, notification));

        verify(restTemplate).postForEntity(eq(callbackUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void sendNotification_ShouldHandleGenericException() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        assertDoesNotThrow(() -> notificationService.sendNotification(callbackUrl, notification));

        verify(restTemplate).postForEntity(eq(callbackUrl), any(HttpEntity.class), eq(String.class));
    }
}