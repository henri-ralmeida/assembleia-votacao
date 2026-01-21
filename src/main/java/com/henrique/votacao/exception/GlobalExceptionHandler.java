package com.henrique.votacao.exception;

import com.henrique.votacao.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, String> buildBody(String msg) {
        Map<String, String> body = new HashMap<>();
        body.put("error", msg);
        return body;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        log.warn("BusinessException: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        log.warn("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("ResponseStatusException: {}", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode())
                .body(buildBody(ex.getReason() != null ? ex.getReason() : "Erro inesperado"));
    }

    // Domain Exception Handlers
    @ExceptionHandler(PautaNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handlePautaNaoEncontradaException(PautaNaoEncontradaException ex) {
        log.warn("PautaNaoEncontradaException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(PautaDuplicadaException.class)
    public ResponseEntity<Map<String, String>> handlePautaDuplicadaException(PautaDuplicadaException ex) {
        log.warn("PautaDuplicadaException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(VotoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> handleVotoDuplicadoException(VotoDuplicadoException ex) {
        log.warn("VotoDuplicadoException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(AssociadoNaoAutorizadoException.class)
    public ResponseEntity<Map<String, String>> handleAssociadoNaoAutorizadoException(AssociadoNaoAutorizadoException ex) {
        log.warn("AssociadoNaoAutorizadoException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(SessaoFechadaException.class)
    public ResponseEntity<Map<String, String>> handleSessaoFechadaException(SessaoFechadaException ex) {
        log.warn("SessaoFechadaException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(SessaoNaoAbertaException.class)
    public ResponseEntity<Map<String, String>> handleSessaoNaoAbertaException(SessaoNaoAbertaException ex) {
        log.warn("SessaoNaoAbertaException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(SessaoJaAbertaException.class)
    public ResponseEntity<Map<String, String>> handleSessaoJaAbertaException(SessaoJaAbertaException ex) {
        log.warn("SessaoJaAbertaException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildBody(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody("Erro inesperado"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseException(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Request Body inválido ou malformado");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
