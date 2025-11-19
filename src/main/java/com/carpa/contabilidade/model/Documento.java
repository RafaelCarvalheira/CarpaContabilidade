package com.carpa.contabilidade.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa um documento (CSV/Excel) enviado pelo cliente.
 * Contém metadados do arquivo e informações sobre o período de referência.
 */
@Entity
@Table(name = "documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do arquivo é obrigatório")
    @Column(nullable = false)
    private String nomeArquivo;

    @NotBlank(message = "O tipo do arquivo é obrigatório")
    @Column(nullable = false)
    private String tipoArquivo; // CSV ou XLSX

    @Column(nullable = false)
    private Long tamanho; // Tamanho em bytes

    @NotBlank(message = "O caminho de armazenamento é obrigatório")
    @Column(nullable = false, length = 500)
    private String caminhoStorage;

    @NotNull(message = "O mês de referência é obrigatório")
    @Min(value = 1, message = "Mês deve ser entre 1 e 12")
    @Max(value = 12, message = "Mês deve ser entre 1 e 12")
    @Column(nullable = false)
    private Integer mesReferencia;

    @NotNull(message = "O ano de referência é obrigatório")
    @Min(value = 2000, message = "Ano deve ser maior ou igual a 2000")
    @Column(nullable = false)
    private Integer anoReferencia;

    @NotNull(message = "O usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"senha", "documentos", "relatorios"})
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProcessamento status = StatusProcessamento.PENDENTE;

    @Column(length = 1000)
    private String mensagemErro; // Caso ocorra erro no processamento

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataUpload;

    @Column
    private LocalDateTime dataProcessamento;

    /**
     * Enum que representa o status de processamento do documento
     */
    public enum StatusProcessamento {
        PENDENTE,      // Arquivo enviado, aguardando processamento
        PROCESSANDO,   // Sendo processado no momento
        PROCESSADO,    // Processado com sucesso
        ERRO           // Erro durante processamento
    }
}
