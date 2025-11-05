package com.carpa.contabilidade.controller;

import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller para gerenciar usuários via API.
 * Fornece endpoints para as operações CRUD (Create, Read, Update, Delete).
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    /**
     * Retorna todos os usuários cadastrados.
     *
     * GET /api/usuarios
     *
     * @return Lista de usuários em formato JSON
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Retorna um usuário específico pelo ID.
     *
     * GET /api/usuarios/{id}
     *
     * @param id ID do usuário
     * @return Usuario em formato JSON ou 404 se não encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cria um novo usuário.
     *
     * POST /api/usuarios
     *
     * @param usuario Dados do usuário no corpo da requisição (JSON)
     * @return Usuario criado com status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        try {
            // Validação: verifica se o email já existe
            if (usuarioService.existePorEmail(usuario.getEmail())) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Já existe um usuário cadastrado com este email!");
            }

            // Validação: verifica se a senha tem no mínimo 6 caracteres
            if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
                return ResponseEntity
                    .badRequest()
                    .body("A senha deve ter no mínimo 6 caracteres!");
            }

            Usuario novoUsuario = usuarioService.salvar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);

        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    /**
     * Atualiza um usuário existente.
     *
     * PUT /api/usuarios/{id}
     *
     * @param id ID do usuário a ser atualizado
     * @param usuario Dados atualizados do usuário (JSON)
     * @return Usuario atualizado ou 404 se não encontrado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            // Verifica se o usuário existe
            if (!usuarioService.buscarPorId(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }

            // Validação: se a senha foi fornecida, verificar tamanho mínimo
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()
                && usuario.getSenha().length() < 6) {
                return ResponseEntity
                    .badRequest()
                    .body("A senha deve ter no mínimo 6 caracteres!");
            }

            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    /**
     * Deleta um usuário pelo ID.
     *
     * DELETE /api/usuarios/{id}
     *
     * @param id ID do usuário a ser deletado
     * @return Status 204 (No Content) se deletado com sucesso, 404 se não encontrado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao deletar usuário: " + e.getMessage());
        }
    }
}
