package br.com.fiap.fase4mspagamento.port;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;

public interface ExternalPaymentService {
    String processPayment(Payment payment);
    String checkStatus(String transactionId);
}