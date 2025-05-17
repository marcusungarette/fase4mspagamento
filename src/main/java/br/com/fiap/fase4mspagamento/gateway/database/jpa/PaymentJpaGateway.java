package br.com.fiap.fase4mspagamento.gateway.database.jpa;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.gateway.PaymentGateway;
import br.com.fiap.fase4mspagamento.gateway.database.jpa.entity.PaymentEntity;
import br.com.fiap.fase4mspagamento.gateway.database.jpa.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentJpaGateway implements PaymentGateway {
    private final PaymentRepository paymentRepository;

    public PaymentJpaGateway(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = new PaymentEntity(payment);
        PaymentEntity savedEntity = paymentRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id)
                .map(PaymentEntity::toDomain);
    }

    @Override
    public Optional<Payment> findByExternalId(String externalId) {
        return paymentRepository.findByExternalId(externalId)
                .map(PaymentEntity::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll().stream()
                .map(PaymentEntity::toDomain)
                .collect(Collectors.toList());
    }
}