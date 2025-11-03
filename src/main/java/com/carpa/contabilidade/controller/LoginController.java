package com.carpa.contabilidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller responsável pela página de login.
 */
@Controller
public class LoginController {

    /**
     * Exibe a página de login.
     *
     * @param error Indica se houve erro no login
     * @param logout Indica se o usuário acabou de fazer logout
     * @param model Model para passar dados para a view
     * @return Nome da view de login
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Email ou senha inválidos. Tente novamente.");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Logout realizado com sucesso.");
        }

        return "login";
    }
}
