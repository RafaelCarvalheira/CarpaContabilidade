package com.carpa.contabilidade.service;

import com.carpa.contabilidade.model.ContatoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsável pelo envio de emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.contato}")
    private String emailDestino;

    /**
     * Envia um email de contato para o endereço configurado.
     *
     * @param contato Dados do formulário de contato
     */
    public void enviarEmailContato(ContatoDTO contato) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            // Configurar remetente e destinatário
            message.setFrom(contato.getEmail());
            message.setTo(emailDestino);
            message.setReplyTo(contato.getEmail());

            // Assunto
            message.setSubject("Contato Site CARPA - " + contato.getAssunto());

            // Corpo do email
            String corpo = String.format(
                "Nova mensagem de contato recebida:\n\n" +
                "Nome: %s\n" +
                "Email: %s\n" +
                "Assunto: %s\n\n" +
                "Mensagem:\n%s\n\n" +
                "---\n" +
                "Este email foi enviado através do formulário de contato do site CARPA Contabilidade.",
                contato.getNome(),
                contato.getEmail(),
                contato.getAssunto(),
                contato.getMensagem()
            );

            message.setText(corpo);

            // Enviar email
            mailSender.send(message);

            log.info("Email de contato enviado com sucesso. Remetente: {}", contato.getEmail());

        } catch (Exception e) {
            log.error("Erro ao enviar email de contato: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar email. Por favor, tente novamente mais tarde.", e);
        }
    }
}
