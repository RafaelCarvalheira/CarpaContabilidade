package com.carpa.contabilidade.repository;

import com.carpa.contabilidade.model.Documento;
import com.carpa.contabilidade.model.Relatorio;
import com.carpa.contabilidade.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gerenciar operações de banco de dados da entidade Relatorio.
 */
@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    /**
     * Busca todos os relatórios de um usuário.
     * @param usuario Usuário proprietário
     * @return Lista de relatórios do usuário
     */
    List<Relatorio> findByUsuario(Usuario usuario);

    /**
     * Busca relatórios por mês e ano de referência.
     * @param mesReferencia Mês de referência (1-12)
     * @param anoReferencia Ano de referência
     * @return Lista de relatórios do período
     */
    List<Relatorio> findByMesReferenciaAndAnoReferencia(Integer mesReferencia, Integer anoReferencia);

    /**
     * Busca relatórios de um usuário por mês e ano.
     * @param usuario Usuário proprietário
     * @param mesReferencia Mês de referência
     * @param anoReferencia Ano de referência
     * @return Lista de relatórios do usuário no período
     */
    List<Relatorio> findByUsuarioAndMesReferenciaAndAnoReferencia(
            Usuario usuario, Integer mesReferencia, Integer anoReferencia);

    /**
     * Busca relatório de um documento específico.
     * @param documento Documento de origem
     * @return Optional contendo o relatório se encontrado
     */
    Optional<Relatorio> findByDocumento(Documento documento);

    /**
     * Busca relatórios de um usuário ordenados por data de geração decrescente.
     * @param usuario Usuário proprietário
     * @return Lista ordenada de relatórios
     */
    List<Relatorio> findByUsuarioOrderByDataGeracaoDesc(Usuario usuario);

    /**
     * Busca os últimos N relatórios de um usuário.
     * @param usuario Usuário proprietário
     * @return Lista com os últimos relatórios
     */
    List<Relatorio> findTop5ByUsuarioOrderByDataGeracaoDesc(Usuario usuario);

    /**
     * Conta relatórios por mês e ano de referência.
     * @param mesReferencia Mês de referência
     * @param anoReferencia Ano de referência
     * @return Quantidade de relatórios no período
     */
    long countByMesReferenciaAndAnoReferencia(Integer mesReferencia, Integer anoReferencia);
}
