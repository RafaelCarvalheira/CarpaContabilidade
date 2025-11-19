package com.carpa.contabilidade.controller;

import com.carpa.contabilidade.model.ItemRelatorio;
import com.carpa.contabilidade.model.Relatorio;
import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.repository.ItemRelatorioRepository;
import com.carpa.contabilidade.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller para gerenciar relatórios mensais.
 */
@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final ItemRelatorioRepository itemRelatorioRepository;
    private final com.carpa.contabilidade.repository.UsuarioRepository usuarioRepository;

    /**
     * Lista todos os relatórios do usuário.
     *
     * GET /api/relatorios
     */
    @GetMapping
    public ResponseEntity<?> listarRelatorios(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Relatorio> relatorios;

            if (mes != null && ano != null) {
                // Filtrar por período
                relatorios = relatorioService.buscarPorPeriodo(usuario, mes, ano);
            } else {
                // Listar todos
                relatorios = relatorioService.listarRelatoriosDoUsuario(usuario);
            }

            return ResponseEntity.ok(relatorios);

        } catch (Exception e) {
            log.error("Erro ao listar relatórios", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao listar relatórios: " + e.getMessage()
            ));
        }
    }

    /**
     * Busca um relatório por ID e retorna todos os dados para o dashboard.
     *
     * GET /api/relatorios/{id}/dados
     */
    @GetMapping("/{id}/dados")
    public ResponseEntity<?> buscarDadosRelatorio(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            Relatorio relatorio = relatorioService.buscarPorId(id);

            // Verificar permissão
            if (!relatorio.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Acesso negado a este relatório"
                ));
            }

            // Buscar itens do relatório
            List<ItemRelatorio> itens = itemRelatorioRepository.findByRelatorioOrderByDataAsc(relatorio);

            // Construir resposta completa com todas as análises
            Map<String, Object> dados = new HashMap<>();

            // 1. Informações básicas
            dados.put("id", relatorio.getId());
            dados.put("mesReferencia", relatorio.getMesReferencia());
            dados.put("anoReferencia", relatorio.getAnoReferencia());
            dados.put("dataGeracao", relatorio.getDataGeracao());

            // 2. Métricas principais (KPIs)
            Map<String, Object> metricas = new HashMap<>();
            metricas.put("receitaTotal", relatorio.getReceitaTotal());
            metricas.put("despesaTotal", relatorio.getDespesaTotal());
            metricas.put("saldo", relatorio.getSaldo());
            metricas.put("margemLucro", relatorio.getMargemLucro());
            metricas.put("totalTransacoes", relatorio.getTotalTransacoes());
            metricas.put("totalReceitas", relatorio.getTotalReceitas());
            metricas.put("totalDespesas", relatorio.getTotalDespesas());
            dados.put("metricas", metricas);

            // 3. Análise por Categoria
            dados.put("porCategoria", analisarPorCategoria(itens));

            // 4. Análise por Centro de Custo
            dados.put("porCentroCusto", analisarPorCentroCusto(itens));

            // 5. Análise por Forma de Pagamento
            dados.put("porFormaPagamento", analisarPorFormaPagamento(itens));

            // 6. Top 10 Transações
            dados.put("top10Receitas", obterTop10Receitas(itens));
            dados.put("top10Despesas", obterTop10Despesas(itens));

            // 7. Todos os itens
            dados.put("itens", itens);

            return ResponseEntity.ok(dados);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("Erro ao buscar dados do relatório", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao buscar dados: " + e.getMessage()
            ));
        }
    }

    /**
     * Busca os últimos 5 relatórios do usuário.
     *
     * GET /api/relatorios/recentes
     */
    @GetMapping("/recentes")
    public ResponseEntity<?> buscarRecentes(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Relatorio> relatorios = relatorioService.buscarUltimosRelatorios(usuario);

            return ResponseEntity.ok(relatorios);

        } catch (Exception e) {
            log.error("Erro ao buscar relatórios recentes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao buscar relatórios: " + e.getMessage()
            ));
        }
    }

    // ==================== MÉTODOS AUXILIARES DE ANÁLISE ====================

    /**
     * Analisa transações por categoria.
     */
    private List<Map<String, Object>> analisarPorCategoria(List<ItemRelatorio> itens) {
        Map<String, Map<String, Object>> categorias = new HashMap<>();

        for (ItemRelatorio item : itens) {
            String categoria = item.getCategoria();
            categorias.putIfAbsent(categoria, new HashMap<>());

            Map<String, Object> dados = categorias.get(categoria);
            BigDecimal totalAtual = (BigDecimal) dados.getOrDefault("total", BigDecimal.ZERO);
            Integer qtdAtual = (Integer) dados.getOrDefault("quantidade", 0);

            dados.put("categoria", categoria);
            dados.put("total", totalAtual.add(item.getValor()));
            dados.put("quantidade", qtdAtual + 1);
            dados.put("tipo", item.getTipo().toString());
        }

        return new ArrayList<>(categorias.values());
    }

    /**
     * Analisa transações por centro de custo.
     */
    private List<Map<String, Object>> analisarPorCentroCusto(List<ItemRelatorio> itens) {
        Map<String, Map<String, Object>> centros = new HashMap<>();

        for (ItemRelatorio item : itens) {
            String centro = item.getCentroCusto() != null ? item.getCentroCusto() : "Não informado";
            centros.putIfAbsent(centro, new HashMap<>());

            Map<String, Object> dados = centros.get(centro);
            BigDecimal receitaAtual = (BigDecimal) dados.getOrDefault("receitas", BigDecimal.ZERO);
            BigDecimal despesaAtual = (BigDecimal) dados.getOrDefault("despesas", BigDecimal.ZERO);

            dados.put("centroCusto", centro);

            if (item.getTipo() == ItemRelatorio.TipoTransacao.RECEITA) {
                dados.put("receitas", receitaAtual.add(item.getValor()));
                dados.put("despesas", despesaAtual);
            } else {
                dados.put("receitas", receitaAtual);
                dados.put("despesas", despesaAtual.add(item.getValor()));
            }

            BigDecimal receita = (BigDecimal) dados.get("receitas");
            BigDecimal despesa = (BigDecimal) dados.get("despesas");
            dados.put("resultado", receita.subtract(despesa));
        }

        return new ArrayList<>(centros.values());
    }

    /**
     * Analisa transações por forma de pagamento.
     */
    private List<Map<String, Object>> analisarPorFormaPagamento(List<ItemRelatorio> itens) {
        Map<String, Map<String, Object>> formas = new HashMap<>();

        for (ItemRelatorio item : itens) {
            String forma = item.getFormaPagamento() != null ? item.getFormaPagamento() : "Não informado";
            formas.putIfAbsent(forma, new HashMap<>());

            Map<String, Object> dados = formas.get(forma);
            BigDecimal receitaAtual = (BigDecimal) dados.getOrDefault("receitas", BigDecimal.ZERO);
            BigDecimal despesaAtual = (BigDecimal) dados.getOrDefault("despesas", BigDecimal.ZERO);

            dados.put("formaPagamento", forma);

            if (item.getTipo() == ItemRelatorio.TipoTransacao.RECEITA) {
                dados.put("receitas", receitaAtual.add(item.getValor()));
                dados.put("despesas", despesaAtual);
            } else {
                dados.put("receitas", receitaAtual);
                dados.put("despesas", despesaAtual.add(item.getValor()));
            }

            BigDecimal total = ((BigDecimal) dados.get("receitas")).add((BigDecimal) dados.get("despesas"));
            dados.put("totalMovimentado", total);
        }

        return new ArrayList<>(formas.values());
    }

    /**
     * Obtém as 10 maiores receitas.
     */
    private List<ItemRelatorio> obterTop10Receitas(List<ItemRelatorio> itens) {
        return itens.stream()
            .filter(item -> item.getTipo() == ItemRelatorio.TipoTransacao.RECEITA)
            .sorted((a, b) -> b.getValor().compareTo(a.getValor()))
            .limit(10)
            .collect(Collectors.toList());
    }

    /**
     * Obtém as 10 maiores despesas.
     */
    private List<ItemRelatorio> obterTop10Despesas(List<ItemRelatorio> itens) {
        return itens.stream()
            .filter(item -> item.getTipo() == ItemRelatorio.TipoTransacao.DESPESA)
            .sorted((a, b) -> b.getValor().compareTo(a.getValor()))
            .limit(10)
            .collect(Collectors.toList());
    }
}
