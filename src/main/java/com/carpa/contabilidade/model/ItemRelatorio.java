package com.carpa.contabilidade.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa uma linha individual do CSV processado.
 * Corresponde a uma transação (receita ou despesa) registrada no documento.
 */
@Entity
@Table(name = "itens_relatorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRelatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O relatório é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_id", nullable = false)
    @JsonIgnoreProperties({"itens", "documento", "usuario"})
    private Relatorio relatorio;

    @NotNull(message = "A data é obrigatória")
    @Column(nullable = false)
    private LocalDate data;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(nullable = false, length = 500)
    private String descricao;

    @NotBlank(message = "A categoria é obrigatória")
    @Column(nullable = false)
    private String categoria;

    @NotNull(message = "O tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @NotNull(message = "O valor é obrigatório")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(length = 100)
    private String formaPagamento;

    @Column(length = 100)
    private String centroCusto;

    @Column(length = 1000)
    private String observacoes;

    /**
     * Enum que representa o tipo de transação
     */
    public enum TipoTransacao {
        RECEITA,
        DESPESA
    }
}
