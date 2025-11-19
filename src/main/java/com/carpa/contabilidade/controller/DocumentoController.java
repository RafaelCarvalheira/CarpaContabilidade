package com.carpa.contabilidade.controller;

import com.carpa.contabilidade.model.Documento;
import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller para gerenciar documentos (CSV/Excel) dos clientes.
 */
@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DocumentoController {

    private final DocumentoService documentoService;
    private final com.carpa.contabilidade.repository.UsuarioRepository usuarioRepository;

    /**
     * Upload de documento.
     *
     * POST /api/documentos/upload
     *
     * @param file Arquivo CSV ou Excel
     * @param mesReferencia Mês de referência (1-12)
     * @param anoReferencia Ano de referência
     * @param userDetails Usuário autenticado
     * @return Documento salvo ou erro
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocumento(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mesReferencia") Integer mesReferencia,
            @RequestParam("anoReferencia") Integer anoReferencia,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            // Buscar usuário autenticado
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            // Fazer upload e processar
            Documento documento = documentoService.uploadDocumento(
                file, usuario, mesReferencia, anoReferencia);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Documento enviado e processado com sucesso");
            response.put("documento", documento);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação no upload: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("Erro ao fazer upload de documento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao processar documento: " + e.getMessage()
            ));
        }
    }

    /**
     * Lista todos os documentos do usuário autenticado.
     *
     * GET /api/documentos
     */
    @GetMapping
    public ResponseEntity<?> listarDocumentos(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            List<Documento> documentos = documentoService.listarDocumentosDoUsuario(usuario);

            return ResponseEntity.ok(documentos);

        } catch (Exception e) {
            log.error("Erro ao listar documentos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao listar documentos: " + e.getMessage()
            ));
        }
    }

    /**
     * Busca documento por ID.
     *
     * GET /api/documentos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            Documento documento = documentoService.buscarPorId(id);

            // Verificar se o documento pertence ao usuário
            if (!documento.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Acesso negado a este documento"
                ));
            }

            return ResponseEntity.ok(documento);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Erro ao buscar documento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao buscar documento: " + e.getMessage()
            ));
        }
    }

    /**
     * Deleta um documento.
     *
     * DELETE /api/documentos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarDocumento(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            documentoService.deletarDocumento(id, usuario);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Documento deletado com sucesso"
            ));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));

        } catch (Exception e) {
            log.error("Erro ao deletar documento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao deletar documento: " + e.getMessage()
            ));
        }
    }
}
