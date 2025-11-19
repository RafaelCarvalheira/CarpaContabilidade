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

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Atualiza um usuário existente.
     * Se a senha for fornecida (não vazia), ela será criptografada.
     *
     * @param id ID do usuário a ser atualizado
     * @param usuarioAtualizado Dados atualizados do usuário
     * @return Usuario atualizado
     * @throws RuntimeException se o usuário não for encontrado
     */
    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + id));

        // Atualiza os campos
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setTipoUsuario(usuarioAtualizado.getTipoUsuario());
        usuario.setAtivo(usuarioAtualizado.getAtivo());

        // Só atualiza a senha se uma nova senha foi fornecida
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Deleta um usuário pelo ID.
     *
     * @param id ID do usuário a ser deletado
     * @throws RuntimeException se o usuário não for encontrado
     */
    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Conta total de clientes cadastrados.
     * @return Quantidade de usuários do tipo CLIENTE
     */
    public long contarClientes() {
        return usuarioRepository.countByTipoUsuario(com.carpa.contabilidade.model.TipoUsuario.CLIENTE);
    }
}
