package com.carpa.contabilidade.controller;

import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.service.DocumentoService;
import com.carpa.contabilidade.service.UsuarioService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ClienteController {

    private final UsuarioService usuarioService;
    private final DocumentoService documentoService;

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

        // Busca usuário no banco
        Usuario usuario = usuarioService.buscarPorEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Busca estatísticas reais
        long totalDocumentos = documentoService.contarDocumentosDoUsuario(usuario);

        model.addAttribute("email", email);
        model.addAttribute("tipoUsuario", "Cliente");
        model.addAttribute("totalDocumentos", totalDocumentos);

        return "cliente/dashboard";
    }

    /**
     * Exibe a página de upload de documentos.
     *
     * @param authentication Informações do usuário autenticado
     * @param model Model para passar dados para a view
     * @return Nome da view de upload
     */
    @GetMapping("/upload-documento")
    public String uploadDocumento(Authentication authentication, Model model) {
        String email = authentication.getName();
        model.addAttribute("email", email);
        return "cliente/upload-documento";
    }

    /**
     * Exibe a página de relatórios mensais.
     *
     * @param authentication Informações do usuário autenticado
     * @param model Model para passar dados para a view
     * @return Nome da view de relatórios
     */
    @GetMapping("/relatorios-mensais")
    public String relatoriosMensais(Authentication authentication, Model model) {
        String email = authentication.getName();
        model.addAttribute("email", email);
        return "cliente/relatorios-mensais";
    }
}
