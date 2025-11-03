package com.carpa.contabilidade.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler customizado para tratar o sucesso da autenticação.
 * Redireciona o usuário para a página apropriada baseado em seu tipo (ADMIN ou CLIENTE).
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Obtém as autoridades (roles) do usuário autenticado
        String redirectUrl = "/";

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            // Redireciona baseado no tipo de usuário
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (role.equals("ROLE_CLIENTE")) {
                redirectUrl = "/cliente/dashboard";
                break;
            }
        }

        // Realiza o redirecionamento
        response.sendRedirect(redirectUrl);
    }
}
