package br.com.fiap.fase4mspagamento.usecase;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.exception.PaymentNotFoundException;
import br.com.fiap.fase4mspagamento.gateway.PaymentGateway;
import org.springframework.stereotype.Service;

@Service
public class GetPaymentUsecase {
    private final PaymentGateway paymentGateway;

    public GetPaymentUsecase(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public Payment execute(Long id) {
        return paymentGateway.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado com ID: " + id));
    }

    public Payment executeByExternalId(String externalId) {
        return paymentGateway.findByExternalId(externalId)
                .orElseThrow(() -> new PaymentNotFoundException("Pagamento não encontrado com ID externo: " + externalId));
    }
}