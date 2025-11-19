package com.carpa.contabilidade.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service responsável pelo armazenamento e gerenciamento de arquivos no sistema.
 * Gerencia upload, exclusão e organização de arquivos por cliente.
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path uploadPath;

    /**
     * Inicializa o diretório de upload na inicialização do serviço.
     */
    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            log.info("Diretório de upload inicializado: {}", uploadPath);
        } catch (IOException e) {
            log.error("Erro ao criar diretório de upload", e);
            throw new RuntimeException("Não foi possível criar o diretório de upload", e);
        }
    }

    /**
     * Salva um arquivo no sistema de arquivos.
     *
     * @param file Arquivo a ser salvo
     * @param usuarioId ID do usuário proprietário
     * @param mesReferencia Mês de referência
     * @param anoReferencia Ano de referência
     * @return Caminho relativo onde o arquivo foi salvo
     * @throws IOException Se houver erro ao salvar
     */
    public String salvarArquivo(MultipartFile file, Long usuarioId, Integer mesReferencia, Integer anoReferencia)
            throws IOException {

        // Validar arquivo
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo vazio não pode ser enviado");
        }

        // Validar extensão
        String extensao = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!isExtensaoValida(extensao)) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido. Use CSV ou XLSX");
        }

        // Criar estrutura de diretórios: uploads/cliente_123/2024/05/
        Path clientePath = uploadPath.resolve("cliente_" + usuarioId);
        Path anoPath = clientePath.resolve(anoReferencia.toString());
        Path mesPath = anoPath.resolve(String.format("%02d", mesReferencia));
        Files.createDirectories(mesPath);

        // Gerar nome único para o arquivo
        String nomeUnico = gerarNomeUnico(file.getOriginalFilename());
        Path destinoPath = mesPath.resolve(nomeUnico);

        // Copiar arquivo
        Files.copy(file.getInputStream(), destinoPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Arquivo salvo: {}", destinoPath);

        // Retornar caminho relativo
        return uploadPath.relativize(destinoPath).toString();
    }

    /**
     * Deleta um arquivo do sistema.
     *
     * @param caminhoRelativo Caminho relativo do arquivo
     * @throws IOException Se houver erro ao deletar
     */
    public void deletarArquivo(String caminhoRelativo) throws IOException {
        Path arquivoPath = uploadPath.resolve(caminhoRelativo).normalize();

        // Verificar se o arquivo está dentro do diretório de upload (segurança)
        if (!arquivoPath.startsWith(uploadPath)) {
            throw new SecurityException("Acesso negado ao arquivo");
        }

        Files.deleteIfExists(arquivoPath);
        log.info("Arquivo deletado: {}", arquivoPath);
    }

    /**
     * Obtém o caminho completo de um arquivo.
     *
     * @param caminhoRelativo Caminho relativo do arquivo
     * @return Path absoluto do arquivo
     */
    public Path obterCaminhoCompleto(String caminhoRelativo) {
        return uploadPath.resolve(caminhoRelativo).normalize();
    }

    /**
     * Verifica se uma extensão de arquivo é válida.
     *
     * @param extensao Extensão do arquivo
     * @return true se válida, false caso contrário
     */
    private boolean isExtensaoValida(String extensao) {
        return extensao != null &&
               (extensao.equalsIgnoreCase("csv") ||
                extensao.equalsIgnoreCase("xlsx") ||
                extensao.equalsIgnoreCase("xls"));
    }

    /**
     * Gera um nome único para o arquivo baseado em timestamp e UUID.
     *
     * @param nomeOriginal Nome original do arquivo
     * @return Nome único gerado
     */
    private String gerarNomeUnico(String nomeOriginal) {
        String extensao = FilenameUtils.getExtension(nomeOriginal);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s.%s", timestamp, uuid, extensao);
    }

    /**
     * Valida o tamanho do arquivo.
     *
     * @param file Arquivo a ser validado
     * @param maxSizeMB Tamanho máximo em MB
     * @return true se válido, false caso contrário
     */
    public boolean validarTamanho(MultipartFile file, long maxSizeMB) {
        long maxSizeBytes = maxSizeMB * 1024 * 1024;
        return file.getSize() <= maxSizeBytes;
    }
}
