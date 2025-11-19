package com.carpa.contabilidade.controller;

import com.carpa.contabilidade.model.TipoUsuario;
import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.service.DocumentoService;
import com.carpa.contabilidade.service.RelatorioService;
import com.carpa.contabilidade.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller responsável pelas páginas do administrador.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioService usuarioService;
    private final DocumentoService documentoService;
    private final RelatorioService relatorioService;

    /**
     * Exibe o dashboard do administrador.
     *
     * @param authentication Informações do usuário autenticado
     * @param model Model para passar dados para a view
     * @return Nome da view do dashboard do admin
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Obtém o email do usuário autenticado
        String email = authentication.getName();

        // Busca estatísticas reais
        long totalClientes = usuarioService.contarClientes();
        long documentosPendentes = documentoService.contarDocumentosPendentes();
        long relatoriosDoMes = relatorioService.contarRelatoriosDoMesAtual();

        model.addAttribute("email", email);
        model.addAttribute("tipoUsuario", "Administrador");
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("documentosPendentes", documentosPendentes);
        model.addAttribute("relatoriosDoMes", relatoriosDoMes);

        return "admin/dashboard";
    }

    /**
     * Exibe a página de gerenciamento de usuários via API REST.
     * Esta página usa JavaScript puro para consumir a API REST.
     *
     * @param authentication Informações do usuário autenticado
     * @param model Model para passar dados para a view
     * @return Nome da view de gerenciamento via API
     */
    @GetMapping("/gerenciar-usuarios-api")
    public String gerenciarUsuariosApi(Authentication authentication, Model model) {
        String email = authentication.getName();
        model.addAttribute("email", email);

        return "admin/gerenciar-usuarios-api";
    }
}
