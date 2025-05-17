package br.com.fiap.fase4mspagamento.gateway;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentGateway {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByExternalId(String externalId);
    List<Payment> findAll();
}