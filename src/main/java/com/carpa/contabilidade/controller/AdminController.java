package com.carpa.contabilidade.controller;

import com.carpa.contabilidade.model.TipoUsuario;
import com.carpa.contabilidade.model.Usuario;
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
        model.addAttribute("email", email);
        model.addAttribute("tipoUsuario", "Administrador");

        return "admin/dashboard";
    }

    /**
     * Exibe o formulário de cadastro de novos usuários.
     *
     * @param authentication Informações do usuário autenticado
     * @param model Model para passar dados para a view
     * @return Nome da view de cadastro de usuários
     */
    @GetMapping("/cadastrar-usuario")
    public String exibirFormularioCadastro(Authentication authentication, Model model) {
        String email = authentication.getName();
        model.addAttribute("email", email);

        // Carrega a lista de usuários para exibir na página
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);

        return "admin/cadastrar-usuario";
    }

    /**
     * Processa o cadastro de um novo usuário.
     *
     * @param nome Nome do usuário
     * @param email Email do usuário
     * @param senha Senha do usuário
     * @param confirmarSenha Confirmação da senha
     * @param tipoUsuario Tipo do usuário (ADMIN ou CLIENTE)
     * @param ativo Status do usuário
     * @param redirectAttributes Atributos para mensagens de redirecionamento
     * @return Redirecionamento para a página de cadastro
     */
    @PostMapping("/cadastrar-usuario")
    public String cadastrarUsuario(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            @RequestParam TipoUsuario tipoUsuario,
            @RequestParam Boolean ativo,
            RedirectAttributes redirectAttributes) {

        try {
            // Validação: verifica se as senhas coincidem
            if (!senha.equals(confirmarSenha)) {
                redirectAttributes.addFlashAttribute("errorMessage", "As senhas não coincidem!");
                return "redirect:/admin/cadastrar-usuario";
            }

            // Validação: verifica se o email já existe
            if (usuarioService.existePorEmail(email)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Já existe um usuário cadastrado com este email!");
                return "redirect:/admin/cadastrar-usuario";
            }

            // Validação: verifica o tamanho mínimo da senha
            if (senha.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "A senha deve ter no mínimo 6 caracteres!");
                return "redirect:/admin/cadastrar-usuario";
            }

            // Cria o novo usuário
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(senha); // Será criptografada no service
            novoUsuario.setTipoUsuario(tipoUsuario);
            novoUsuario.setAtivo(ativo);

            // Salva o usuário
            usuarioService.salvar(novoUsuario);

            // Mensagem de sucesso
            String tipoTexto = tipoUsuario == TipoUsuario.ADMIN ? "Administrador" : "Cliente";
            redirectAttributes.addFlashAttribute("successMessage",
                tipoTexto + " cadastrado com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Erro ao cadastrar usuário: " + e.getMessage());
        }

        return "redirect:/admin/cadastrar-usuario";
    }
}
