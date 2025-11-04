package com.carpa.contabilidade.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsável pela página inicial.
 */
@Controller
public class HomeController {

    /**
     * Exibe a landing page para visitantes não autenticados.
     * Redireciona usuários autenticados para seus dashboards apropriados.
     *
     * @param authentication Informações do usuário autenticado (pode ser null)
     * @return Nome da view (landing page) ou redirecionamento
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        // Se não autenticado, mostra a landing page
        if (authentication == null) {
            return "index";
        }

        // Se autenticado, redireciona baseado nas autoridades do usuário
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/cliente/dashboard";
        }
    }
}
