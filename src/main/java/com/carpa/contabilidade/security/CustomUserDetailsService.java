package com.carpa.contabilidade.security;

import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço customizado para carregar detalhes do usuário no processo de autenticação.
 * Implementa a interface UserDetailsService do Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carrega um usuário pelo email (username).
     * Método usado pelo Spring Security durante o processo de autenticação.
     *
     * @param email Email do usuário (usado como username)
     * @return UserDetails contendo informações do usuário
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        // Verifica se o usuário está ativo
        if (!usuario.getAtivo()) {
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        // Cria as autoridades (roles) baseadas no tipo de usuário
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getTipoUsuario().name()));

        // Retorna um UserDetails com as informações do usuário
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!usuario.getAtivo())
                .credentialsExpired(false)
                .disabled(!usuario.getAtivo())
                .build();
    }
}
