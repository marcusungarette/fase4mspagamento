package br.com.fiap.fase4mspagamento.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long id;
    private String externalId;
    private BigDecimal amount;
    private String creditCardNumber;
    private String orderId;
    private String callbackUrl;
    private PaymentStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment(BigDecimal amount, String creditCardNumber, String orderId, String callbackUrl) {
        this.amount = amount;
        this.creditCardNumber = creditCardNumber;
        this.orderId = orderId;
        this.callbackUrl = callbackUrl;
        this.status = PaymentStatus.PENDING;
        this.message = "Processando pagamento";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Payment withStatus(PaymentStatus newStatus, String newMessage) {
        return new Payment(
                this.id,
                this.externalId,
                this.amount,
                this.creditCardNumber,
                this.orderId,
                this.callbackUrl,
                newStatus,
                newMessage,
                this.createdAt,
                LocalDateTime.now()
        );
    }
}