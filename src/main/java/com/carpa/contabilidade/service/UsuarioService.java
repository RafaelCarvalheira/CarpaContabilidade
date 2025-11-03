package com.carpa.contabilidade.service;

import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio relacionada aos usuários.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Salva um novo usuário no banco de dados.
     * A senha é criptografada antes de ser salva.
     *
     * @param usuario Usuário a ser salvo
     * @return Usuário salvo
     */
    @Transactional
    public Usuario salvar(Usuario usuario) {
        // Criptografa a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Busca todos os usuários cadastrados.
     *
     * @return Lista de todos os usuários
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Verifica se já existe um usuário com o email fornecido.
     *
     * @param email Email a ser verificado
     * @return true se o email já existe, false caso contrário
     */
    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
