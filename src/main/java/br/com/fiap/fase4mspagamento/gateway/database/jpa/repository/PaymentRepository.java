package br.com.fiap.fase4mspagamento.gateway.database.jpa.repository;

import br.com.fiap.fase4mspagamento.gateway.database.jpa.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByExternalId(String externalId);
}