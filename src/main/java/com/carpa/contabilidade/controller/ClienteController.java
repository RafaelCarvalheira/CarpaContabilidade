package com.carpa.contabilidade.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsável pelas páginas do cliente.
 */
@Controller
@RequestMapping("/cliente")
public class ClienteController {

    /**
     * Exibe o dashboard do cliente.
     *
     * @param authentication Informações do usuário autenticado
     * @param model Model para passar dados para a view
     * @return Nome da view do dashboard do cliente
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Obtém o email do usuário autenticado
        String email = authentication.getName();
        model.addAttribute("email", email);
        model.addAttribute("tipoUsuario", "Cliente");

        return "cliente/dashboard";
    }
}
