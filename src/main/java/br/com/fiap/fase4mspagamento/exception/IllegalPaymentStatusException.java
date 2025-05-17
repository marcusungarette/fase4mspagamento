package br.com.fiap.fase4mspagamento.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalPaymentStatusException extends RuntimeException {
    public IllegalPaymentStatusException(String message) {
        super(message);
    }
}