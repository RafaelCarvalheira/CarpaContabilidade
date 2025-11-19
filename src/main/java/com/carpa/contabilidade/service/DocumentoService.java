package com.carpa.contabilidade.service;

import com.carpa.contabilidade.model.Documento;
import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service para gerenciar documentos (CSV/Excel) enviados pelos clientes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final FileStorageService fileStorageService;
    private final RelatorioService relatorioService;

    private static final long MAX_FILE_SIZE_MB = 10;

    /**
     * Faz upload de um documento e processa automaticamente.
     *
     * @param file Arquivo enviado
     * @param usuario Usuário proprietário
     * @param mesReferencia Mês de referência
     * @param anoReferencia Ano de referência
     * @return Documento salvo
     * @throws IOException Se houver erro no upload
     */
    @Transactional
    public Documento uploadDocumento(MultipartFile file, Usuario usuario,
                                      Integer mesReferencia, Integer anoReferencia) throws IOException {

        // Validações
        validarUpload(file, usuario, mesReferencia, anoReferencia);

        // Verificar se já existe documento para este mês/ano
        if (documentoRepository.existsByUsuarioAndMesReferenciaAndAnoReferencia(
                usuario, mesReferencia, anoReferencia)) {
            throw new IllegalArgumentException(
                "Já existe um documento para " + mesReferencia + "/" + anoReferencia +
                ". Exclua o anterior antes de enviar um novo.");
        }

        // Salvar arquivo no sistema de arquivos
        String caminhoArquivo = fileStorageService.salvarArquivo(
            file, usuario.getId(), mesReferencia, anoReferencia);

        // Criar e salvar entidade Documento
        Documento documento = new Documento();
        documento.setNomeArquivo(file.getOriginalFilename());
        documento.setTipoArquivo(FilenameUtils.getExtension(file.getOriginalFilename()).toUpperCase());
        documento.setTamanho(file.getSize());
        documento.setCaminhoStorage(caminhoArquivo);
        documento.setMesReferencia(mesReferencia);
        documento.setAnoReferencia(anoReferencia);
        documento.setUsuario(usuario);
        documento.setStatus(Documento.StatusProcessamento.PENDENTE);

        documento = documentoRepository.save(documento);
        log.info("Documento salvo: ID={}, Usuario={}", documento.getId(), usuario.getEmail());

        // Processar documento assincronamente
        processarDocumento(documento);

        return documento;
    }

    /**
     * Processa um documento e gera o relatório.
     *
     * @param documento Documento a ser processado
     */
    @Transactional
    public void processarDocumento(Documento documento) {
        try {
            log.info("Iniciando processamento do documento: {}", documento.getId());

            // Atualizar status para PROCESSANDO
            documento.setStatus(Documento.StatusProcessamento.PROCESSANDO);
            documentoRepository.save(documento);

            // Processar arquivo e gerar relatório
            relatorioService.gerarRelatorio(documento);

            // Atualizar status para PROCESSADO
            documento.setStatus(Documento.StatusProcessamento.PROCESSADO);
            documento.setDataProcessamento(LocalDateTime.now());
            documento.setMensagemErro(null);
            documentoRepository.save(documento);

            log.info("Documento processado com sucesso: {}", documento.getId());

        } catch (Exception e) {
            log.error("Erro ao processar documento: {}", documento.getId(), e);

            // Atualizar status para ERRO
            documento.setStatus(Documento.StatusProcessamento.ERRO);
            documento.setMensagemErro(e.getMessage());
            documentoRepository.save(documento);

            throw new RuntimeException("Erro ao processar documento: " + e.getMessage(), e);
        }
    }

    /**
     * Busca todos os documentos de um usuário.
     */
    public List<Documento> listarDocumentosDoUsuario(Usuario usuario) {
        return documentoRepository.findByUsuarioOrderByDataUploadDesc(usuario);
    }

    /**
     * Busca documento por ID.
     */
    public Documento buscarPorId(Long id) {
        return documentoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));
    }

    /**
     * Busca documentos por mês e ano.
     */
    public List<Documento> buscarPorPeriodo(Usuario usuario, Integer mes, Integer ano) {
        return documentoRepository.findByUsuarioAndMesReferenciaAndAnoReferencia(usuario, mes, ano);
    }

    /**
     * Deleta um documento e seu arquivo físico.
     */
    @Transactional
    public void deletarDocumento(Long documentoId, Usuario usuario) throws IOException {
        Documento documento = buscarPorId(documentoId);

        // Verificar se o documento pertence ao usuário
        if (!documento.getUsuario().getId().equals(usuario.getId())) {
            throw new SecurityException("Você não tem permissão para deletar este documento");
        }

        // Deletar arquivo físico
        fileStorageService.deletarArquivo(documento.getCaminhoStorage());

        // Deletar do banco (cascade deleta o relatório associado)
        documentoRepository.delete(documento);

        log.info("Documento deletado: {}", documentoId);
    }

    /**
     * Valida o upload do arquivo.
     */
    private void validarUpload(MultipartFile file, Usuario usuario,
                               Integer mesReferencia, Integer anoReferencia) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não informado");
        }

        if (mesReferencia == null || mesReferencia < 1 || mesReferencia > 12) {
            throw new IllegalArgumentException("Mês de referência inválido");
        }

        if (anoReferencia == null || anoReferencia < 2000) {
            throw new IllegalArgumentException("Ano de referência inválido");
        }

        // Validar tamanho
        if (!fileStorageService.validarTamanho(file, MAX_FILE_SIZE_MB)) {
            throw new IllegalArgumentException(
                "Arquivo muito grande. Tamanho máximo: " + MAX_FILE_SIZE_MB + "MB");
        }

        // Validar extensão
        String extensao = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extensao == null ||
            (!extensao.equalsIgnoreCase("csv") &&
             !extensao.equalsIgnoreCase("xlsx") &&
             !extensao.equalsIgnoreCase("xls"))) {
            throw new IllegalArgumentException("Formato de arquivo inválido. Use CSV ou XLSX");
        }
    }
}
