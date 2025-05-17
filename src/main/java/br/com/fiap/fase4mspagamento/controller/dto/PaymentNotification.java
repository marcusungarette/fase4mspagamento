package br.com.fiap.fase4mspagamento.controller.dto;

import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotification {
    private Long paymentId;
    private String externalId;
    private PaymentStatus status;
    private String message;
    private String orderId;
}