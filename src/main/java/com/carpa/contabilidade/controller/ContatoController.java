package com.carpa.contabilidade.controller;

import com.carpa.contabilidade.model.ContatoDTO;
import com.carpa.contabilidade.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar mensagens de contato do site.
 */
@RestController
@RequestMapping("/api/contato")
@RequiredArgsConstructor
@Slf4j
public class ContatoController {

    private final EmailService emailService;

    /**
     * Recebe e processa uma mensagem de contato do formulário.
     *
     * @param contato Dados do formulário de contato
     * @param result Resultado da validação
     * @return ResponseEntity com mensagem de sucesso ou erro
     */
    @PostMapping("/enviar")
    public ResponseEntity<Map<String, Object>> enviarMensagem(
            @Valid @RequestBody ContatoDTO contato,
            BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        // Validar campos
        if (result.hasErrors()) {
            String erros = result.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

            log.warn("Validação falhou ao enviar mensagem de contato: {}", erros);

            response.put("sucesso", false);
            response.put("mensagem", "Dados inválidos: " + erros);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Enviar email
            emailService.enviarEmailContato(contato);

            log.info("Mensagem de contato recebida de: {} ({})", contato.getNome(), contato.getEmail());

            response.put("sucesso", true);
            response.put("mensagem", "Mensagem enviada com sucesso! Entraremos em contato em breve.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao processar mensagem de contato", e);

            response.put("sucesso", false);
            response.put("mensagem", "Erro ao enviar mensagem. Por favor, tente novamente mais tarde.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
