package br.com.fiap.fase4mspagamento.controller.dto;

import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private String externalId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String message;
    private String orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}