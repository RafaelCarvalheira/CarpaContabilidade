package com.carpa.contabilidade.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar uma mensagem de contato enviada pelo formulário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContatoDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "O assunto é obrigatório")
    @Size(min = 5, max = 200, message = "O assunto deve ter entre 5 e 200 caracteres")
    private String assunto;

    @NotBlank(message = "A mensagem é obrigatória")
    @Size(min = 10, max = 1000, message = "A mensagem deve ter entre 10 e 1000 caracteres")
    private String mensagem;
}
