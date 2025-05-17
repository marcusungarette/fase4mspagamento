package br.com.fiap.fase4mspagamento.controller.mapper;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentNotification;
import br.com.fiap.fase4mspagamento.controller.dto.PaymentRequest;
import br.com.fiap.fase4mspagamento.controller.dto.PaymentResponse;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toDomain(PaymentRequest request) {
        return new Payment(
                request.getAmount(),
                request.getCreditCardNumber(),
                request.getOrderId(),
                request.getCallbackUrl()
        );
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getExternalId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getMessage(),
                payment.getOrderId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public PaymentNotification toNotification(Payment payment) {
        return new PaymentNotification(
                payment.getId(),
                payment.getExternalId(),
                payment.getStatus(),
                payment.getMessage(),
                payment.getOrderId()
        );
    }
}