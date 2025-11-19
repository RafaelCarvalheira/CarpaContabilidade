package com.carpa.contabilidade.repository;

import com.carpa.contabilidade.model.TipoUsuario;
import com.carpa.contabilidade.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para gerenciar operações de banco de dados da entidade Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email fornecido.
     * @param email Email a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Conta usuários por tipo.
     * @param tipoUsuario Tipo de usuário
     * @return Quantidade de usuários do tipo especificado
     */
    long countByTipoUsuario(TipoUsuario tipoUsuario);
}
