package com.carpa.contabilidade.service;

import com.carpa.contabilidade.model.Documento;
import com.carpa.contabilidade.model.ItemRelatorio;
import com.carpa.contabilidade.model.Relatorio;
import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.repository.RelatorioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.List;

/**
 * Service para gerenciar relatórios mensais gerados a partir dos documentos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final CsvProcessadorService csvProcessadorService;
    private final FileStorageService fileStorageService;

    /**
     * Gera um relatório a partir de um documento processado.
     *
     * @param documento Documento de origem
     * @return Relatório gerado
     * @throws IOException Se houver erro ao ler o arquivo
     */
    @Transactional
    public Relatorio gerarRelatorio(Documento documento) throws IOException {
        log.info("Gerando relatório para documento: {}", documento.getId());

        // Obter caminho completo do arquivo
        Path caminhoArquivo = fileStorageService.obterCaminhoCompleto(documento.getCaminhoStorage());

        // Processar arquivo e obter lista de itens
        List<ItemRelatorio> itens = csvProcessadorService.processarArquivo(
            caminhoArquivo, documento.getTipoArquivo());

        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não contém dados válidos");
        }

        // Criar relatório
        Relatorio relatorio = new Relatorio();
        relatorio.setDocumento(documento);
        relatorio.setUsuario(documento.getUsuario());
        relatorio.setMesReferencia(documento.getMesReferencia());
        relatorio.setAnoReferencia(documento.getAnoReferencia());

        // Adicionar itens ao relatório
        for (ItemRelatorio item : itens) {
            relatorio.addItem(item);
        }

        // Calcular métricas
        calcularMetricas(relatorio);

        // Salvar relatório (cascade salva os itens)
        relatorio = relatorioRepository.save(relatorio);

        log.info("Relatório gerado com sucesso: ID={}, Total Transações={}",
                 relatorio.getId(), relatorio.getTotalTransacoes());

        return relatorio;
    }

    /**
     * Calcula todas as métricas do relatório.
     */
    private void calcularMetricas(Relatorio relatorio) {
        BigDecimal receitaTotal = BigDecimal.ZERO;
        BigDecimal despesaTotal = BigDecimal.ZERO;
        int totalReceitas = 0;
        int totalDespesas = 0;

        for (ItemRelatorio item : relatorio.getItens()) {
            if (item.getTipo() == ItemRelatorio.TipoTransacao.RECEITA) {
                receitaTotal = receitaTotal.add(item.getValor());
                totalReceitas++;
            } else {
                despesaTotal = despesaTotal.add(item.getValor());
                totalDespesas++;
            }
        }

        // Calcular saldo
        BigDecimal saldo = receitaTotal.subtract(despesaTotal);

        // Calcular margem de lucro (%)
        BigDecimal margemLucro = BigDecimal.ZERO;
        if (receitaTotal.compareTo(BigDecimal.ZERO) > 0) {
            margemLucro = saldo
                .multiply(BigDecimal.valueOf(100))
                .divide(receitaTotal, 2, RoundingMode.HALF_UP);
        }

        // Atualizar relatório
        relatorio.setReceitaTotal(receitaTotal);
        relatorio.setDespesaTotal(despesaTotal);
        relatorio.setSaldo(saldo);
        relatorio.setMargemLucro(margemLucro);
        relatorio.setTotalTransacoes(relatorio.getItens().size());
        relatorio.setTotalReceitas(totalReceitas);
        relatorio.setTotalDespesas(totalDespesas);

        log.debug("Métricas calculadas - Receita: {}, Despesa: {}, Saldo: {}, Margem: {}%",
                  receitaTotal, despesaTotal, saldo, margemLucro);
    }

    /**
     * Busca todos os relatórios de um usuário.
     */
    public List<Relatorio> listarRelatoriosDoUsuario(Usuario usuario) {
        return relatorioRepository.findByUsuarioOrderByDataGeracaoDesc(usuario);
    }

    /**
     * Busca relatório por ID.
     */
    public Relatorio buscarPorId(Long id) {
        return relatorioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado"));
    }

    /**
     * Busca relatórios por período.
     */
    public List<Relatorio> buscarPorPeriodo(Usuario usuario, Integer mes, Integer ano) {
        return relatorioRepository.findByUsuarioAndMesReferenciaAndAnoReferencia(usuario, mes, ano);
    }

    /**
     * Busca os últimos 5 relatórios do usuário.
     */
    public List<Relatorio> buscarUltimosRelatorios(Usuario usuario) {
        return relatorioRepository.findTop5ByUsuarioOrderByDataGeracaoDesc(usuario);
    }

    /**
     * Busca relatório de um documento específico.
     */
    public Relatorio buscarPorDocumento(Documento documento) {
        return relatorioRepository.findByDocumento(documento)
            .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado para este documento"));
    }

    /**
     * Conta relatórios do mês atual (para admin).
     */
    public long contarRelatoriosDoMesAtual() {
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        int mesAtual = agora.getMonthValue();
        int anoAtual = agora.getYear();
        return relatorioRepository.countByMesReferenciaAndAnoReferencia(mesAtual, anoAtual);
    }
}
