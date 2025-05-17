package br.com.fiap.fase4mspagamento.controller;

import br.com.fiap.fase4mspagamento.controller.dto.PaymentRequest;
import br.com.fiap.fase4mspagamento.controller.dto.PaymentResponse;
import br.com.fiap.fase4mspagamento.controller.mapper.PaymentMapper;
import br.com.fiap.fase4mspagamento.domain.entity.Payment;
import br.com.fiap.fase4mspagamento.usecase.GetPaymentUsecase;
import br.com.fiap.fase4mspagamento.usecase.ProcessPaymentUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final ProcessPaymentUsecase processPaymentUsecase;
    private final GetPaymentUsecase getPaymentUsecase;
    private final PaymentMapper paymentMapper;

    public PaymentController(
            ProcessPaymentUsecase processPaymentUsecase,
            GetPaymentUsecase getPaymentUsecase,
            PaymentMapper paymentMapper) {
        this.processPaymentUsecase = processPaymentUsecase;
        this.getPaymentUsecase = getPaymentUsecase;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentMapper.toDomain(request);
        Payment processedPayment = processPaymentUsecase.execute(payment);
        PaymentResponse response = paymentMapper.toResponse(processedPayment);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        Payment payment = getPaymentUsecase.execute(id);
        PaymentResponse response = paymentMapper.toResponse(payment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/external/{externalId}")
    public ResponseEntity<PaymentResponse> getPaymentByExternalId(@PathVariable String externalId) {
        Payment payment = getPaymentUsecase.executeByExternalId(externalId);
        PaymentResponse response = paymentMapper.toResponse(payment);
        return ResponseEntity.ok(response);
    }
}