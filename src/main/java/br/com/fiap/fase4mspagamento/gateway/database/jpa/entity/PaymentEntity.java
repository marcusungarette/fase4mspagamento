package br.com.fiap.fase4mspagamento.gateway.database.jpa.entity;

import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.domain.entity.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "credit_card_number", nullable = false)
    private String creditCardNumber;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "callback_url", nullable = false)
    private String callbackUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public PaymentEntity(Payment payment) {
        this.id = payment.getId();
        this.externalId = payment.getExternalId();
        this.amount = payment.getAmount();
        this.creditCardNumber = payment.getCreditCardNumber();
        this.orderId = payment.getOrderId();
        this.callbackUrl = payment.getCallbackUrl();
        this.status = payment.getStatus();
        this.message = payment.getMessage();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
    }

    public Payment toDomain() {
        return new Payment(
                this.id,
                this.externalId,
                this.amount,
                this.creditCardNumber,
                this.orderId,
                this.callbackUrl,
                this.status,
                this.message,
                this.createdAt,
                this.updatedAt
        );
    }
}