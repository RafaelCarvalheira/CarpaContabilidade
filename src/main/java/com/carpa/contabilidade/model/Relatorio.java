package com.carpa.contabilidade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um relatório mensal gerado a partir de um documento.
 * Contém as métricas e análises calculadas dos dados do CSV/Excel.
 */
@Entity
@Table(name = "relatorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O mês de referência é obrigatório")
    @Min(value = 1, message = "Mês deve ser entre 1 e 12")
    @Max(value = 12, message = "Mês deve ser entre 1 e 12")
    @Column(nullable = false)
    private Integer mesReferencia;

    @NotNull(message = "O ano de referência é obrigatório")
    @Min(value = 2000, message = "Ano deve ser maior ou igual a 2000")
    @Column(nullable = false)
    private Integer anoReferencia;

    @NotNull(message = "O documento de origem é obrigatório")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id", nullable = false)
    @JsonIgnore
    private Documento documento;

    @NotNull(message = "O usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    // Métricas principais
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal receitaTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal despesaTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal margemLucro = BigDecimal.ZERO; // Percentual

    @Column(nullable = false)
    private Integer totalTransacoes = 0;

    @Column(nullable = false)
    private Integer totalReceitas = 0;

    @Column(nullable = false)
    private Integer totalDespesas = 0;

    // Relacionamento com itens individuais do relatório
    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ItemRelatorio> itens = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataGeracao;

    /**
     * Método auxiliar para adicionar item ao relatório
     */
    public void addItem(ItemRelatorio item) {
        itens.add(item);
        item.setRelatorio(this);
    }

    /**
     * Método auxiliar para remover item do relatório
     */
    public void removeItem(ItemRelatorio item) {
        itens.remove(item);
        item.setRelatorio(null);
    }
}
