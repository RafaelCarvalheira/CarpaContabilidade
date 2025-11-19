package com.carpa.contabilidade.repository;

import com.carpa.contabilidade.model.Documento;
import com.carpa.contabilidade.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciar operações de banco de dados da entidade Documento.
 */
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    /**
     * Busca todos os documentos de um usuário.
     * @param usuario Usuário proprietário dos documentos
     * @return Lista de documentos do usuário
     */
    List<Documento> findByUsuario(Usuario usuario);

    /**
     * Busca documentos por mês e ano de referência.
     * @param mesReferencia Mês de referência (1-12)
     * @param anoReferencia Ano de referência
     * @return Lista de documentos do período
     */
    List<Documento> findByMesReferenciaAndAnoReferencia(Integer mesReferencia, Integer anoReferencia);

    /**
     * Busca documentos de um usuário por mês e ano.
     * @param usuario Usuário proprietário
     * @param mesReferencia Mês de referência
     * @param anoReferencia Ano de referência
     * @return Lista de documentos do usuário no período
     */
    List<Documento> findByUsuarioAndMesReferenciaAndAnoReferencia(
            Usuario usuario, Integer mesReferencia, Integer anoReferencia);

    /**
     * Verifica se já existe um documento para o usuário no mês/ano especificado.
     * @param usuario Usuário
     * @param mesReferencia Mês
     * @param anoReferencia Ano
     * @return true se existe, false caso contrário
     */
    boolean existsByUsuarioAndMesReferenciaAndAnoReferencia(
            Usuario usuario, Integer mesReferencia, Integer anoReferencia);

    /**
     * Busca documentos de um usuário ordenados por data de upload decrescente.
     * @param usuario Usuário proprietário
     * @return Lista ordenada de documentos
     */
    List<Documento> findByUsuarioOrderByDataUploadDesc(Usuario usuario);

    /**
     * Conta total de documentos de um usuário.
     * @param usuario Usuário proprietário
     * @return Quantidade de documentos
     */
    long countByUsuario(Usuario usuario);

    /**
     * Conta documentos por status.
     * @param status Status do documento
     * @return Quantidade de documentos com o status
     */
    long countByStatus(Documento.StatusProcessamento status);
}
