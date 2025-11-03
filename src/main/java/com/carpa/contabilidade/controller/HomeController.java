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
     * Redireciona para a página apropriada baseado no tipo de usuário.
     * Se não autenticado, redireciona para login.
     *
     * @param authentication Informações do usuário autenticado (pode ser null)
     * @return Redirecionamento para a página apropriada
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        // Redireciona baseado nas autoridades do usuário
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/cliente/dashboard";
        }
    }
}
