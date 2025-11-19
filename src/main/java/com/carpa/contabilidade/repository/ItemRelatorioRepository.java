package com.carpa.contabilidade.repository;

import com.carpa.contabilidade.model.ItemRelatorio;
import com.carpa.contabilidade.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Repository para gerenciar operações de banco de dados da entidade ItemRelatorio.
 */
@Repository
public interface ItemRelatorioRepository extends JpaRepository<ItemRelatorio, Long> {

    /**
     * Busca todos os itens de um relatório.
     * @param relatorio Relatório
     * @return Lista de itens do relatório
     */
    List<ItemRelatorio> findByRelatorio(Relatorio relatorio);

    /**
     * Busca itens de um relatório por tipo.
     * @param relatorio Relatório
     * @param tipo Tipo da transação (RECEITA ou DESPESA)
     * @return Lista de itens filtrados
     */
    List<ItemRelatorio> findByRelatorioAndTipo(Relatorio relatorio, ItemRelatorio.TipoTransacao tipo);

    /**
     * Busca itens de um relatório por categoria.
     * @param relatorio Relatório
     * @param categoria Nome da categoria
     * @return Lista de itens da categoria
     */
    List<ItemRelatorio> findByRelatorioAndCategoria(Relatorio relatorio, String categoria);

    /**
     * Busca itens ordenados por valor decrescente.
     * @param relatorio Relatório
     * @return Lista ordenada de itens
     */
    List<ItemRelatorio> findByRelatorioOrderByValorDesc(Relatorio relatorio);

    /**
     * Busca itens ordenados por data.
     * @param relatorio Relatório
     * @return Lista ordenada de itens
     */
    List<ItemRelatorio> findByRelatorioOrderByDataAsc(Relatorio relatorio);

    /**
     * Busca os N maiores itens por valor.
     * @param relatorio Relatório
     * @return Lista com os maiores itens
     */
    List<ItemRelatorio> findTop10ByRelatorioOrderByValorDesc(Relatorio relatorio);
}
