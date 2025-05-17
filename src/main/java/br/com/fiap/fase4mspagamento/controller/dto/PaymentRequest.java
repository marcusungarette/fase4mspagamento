package br.com.fiap.fase4mspagamento.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private BigDecimal amount;
    private String creditCardNumber;
    private String orderId;
    private String callbackUrl;
}