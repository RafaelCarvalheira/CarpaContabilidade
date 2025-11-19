package com.carpa.contabilidade.service;

import com.carpa.contabilidade.model.ItemRelatorio;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service responsável por processar arquivos CSV e Excel.
 * Lê os arquivos e converte em objetos ItemRelatorio.
 */
@Service
@Slf4j
public class CsvProcessadorService {

    private static final String[] FORMATOS_DATA = {
        "dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy", "MM/dd/yyyy"
    };

    /**
     * Processa um arquivo CSV ou Excel e retorna lista de itens.
     *
     * @param caminhoArquivo Path do arquivo
     * @param tipoArquivo Tipo do arquivo (CSV ou XLSX)
     * @return Lista de ItemRelatorio processados
     * @throws IOException Se houver erro ao ler o arquivo
     */
    public List<ItemRelatorio> processarArquivo(Path caminhoArquivo, String tipoArquivo) throws IOException {
        log.info("Processando arquivo: {} (tipo: {})", caminhoArquivo, tipoArquivo);

        if (tipoArquivo.equalsIgnoreCase("CSV")) {
            return processarCsv(caminhoArquivo);
        } else if (tipoArquivo.equalsIgnoreCase("XLSX") || tipoArquivo.equalsIgnoreCase("XLS")) {
            return processarExcel(caminhoArquivo);
        } else {
            throw new IllegalArgumentException("Tipo de arquivo não suportado: " + tipoArquivo);
        }
    }

    /**
     * Processa arquivo CSV.
     */
    private List<ItemRelatorio> processarCsv(Path caminhoArquivo) throws IOException {
        List<ItemRelatorio> itens = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(caminhoArquivo.toFile()))) {
            List<String[]> linhas = reader.readAll();

            if (linhas.isEmpty()) {
                throw new IllegalArgumentException("Arquivo CSV está vazio");
            }

            // Primeira linha é o cabeçalho
            String[] cabecalho = linhas.get(0);
            validarCabecalho(cabecalho);

            // Processar linhas de dados (começando da linha 1)
            for (int i = 1; i < linhas.size(); i++) {
                String[] linha = linhas.get(i);

                // Ignorar linhas vazias
                if (isLinhaVazia(linha)) {
                    continue;
                }

                try {
                    ItemRelatorio item = criarItemDeLinha(linha, i + 1);
                    itens.add(item);
                } catch (Exception e) {
                    log.warn("Erro ao processar linha {}: {}", i + 1, e.getMessage());
                    throw new IllegalArgumentException("Erro na linha " + (i + 1) + ": " + e.getMessage());
                }
            }

            log.info("CSV processado com sucesso: {} itens", itens.size());
            return itens;

        } catch (CsvException e) {
            log.error("Erro ao ler arquivo CSV", e);
            throw new IOException("Erro ao processar CSV: " + e.getMessage(), e);
        }
    }

    /**
     * Processa arquivo Excel.
     */
    private List<ItemRelatorio> processarExcel(Path caminhoArquivo) throws IOException {
        List<ItemRelatorio> itens = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(caminhoArquivo.toFile());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getPhysicalNumberOfRows() == 0) {
                throw new IllegalArgumentException("Planilha Excel está vazia");
            }

            // Primeira linha é o cabeçalho
            Row cabecalhoRow = sheet.getRow(0);
            validarCabecalhoExcel(cabecalhoRow);

            // Processar linhas de dados
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null || isLinhaVaziaExcel(row)) {
                    continue;
                }

                try {
                    ItemRelatorio item = criarItemDeLinhaExcel(row, i + 1);
                    itens.add(item);
                } catch (Exception e) {
                    log.warn("Erro ao processar linha {}: {}", i + 1, e.getMessage());
                    throw new IllegalArgumentException("Erro na linha " + (i + 1) + ": " + e.getMessage());
                }
            }

            log.info("Excel processado com sucesso: {} itens", itens.size());
            return itens;
        }
    }

    /**
     * Valida o cabeçalho do CSV.
     */
    private void validarCabecalho(String[] cabecalho) {
        if (cabecalho.length < 7) {
            throw new IllegalArgumentException(
                "CSV deve ter pelo menos 7 colunas: Data,Descrição,Categoria,Tipo,Valor,Forma_Pagamento,Centro_Custo");
        }
    }

    /**
     * Valida o cabeçalho do Excel.
     */
    private void validarCabecalhoExcel(Row cabecalho) {
        if (cabecalho == null || cabecalho.getPhysicalNumberOfCells() < 7) {
            throw new IllegalArgumentException(
                "Excel deve ter pelo menos 7 colunas: Data,Descrição,Categoria,Tipo,Valor,Forma_Pagamento,Centro_Custo");
        }
    }

    /**
     * Cria ItemRelatorio a partir de linha CSV.
     * Formato esperado: Data,Descrição,Categoria,Tipo,Valor,Forma_Pagamento,Centro_Custo,Observações
     */
    private ItemRelatorio criarItemDeLinha(String[] linha, int numeroLinha) {
        ItemRelatorio item = new ItemRelatorio();

        try {
            // Coluna 0: Data
            item.setData(parseData(linha[0].trim()));

            // Coluna 1: Descrição
            item.setDescricao(linha[1].trim());
            if (item.getDescricao().isEmpty()) {
                throw new IllegalArgumentException("Descrição não pode estar vazia");
            }

            // Coluna 2: Categoria
            item.setCategoria(linha[2].trim());

            // Coluna 3: Tipo (Receita ou Despesa)
            item.setTipo(parseTipo(linha[3].trim()));

            // Coluna 4: Valor
            item.setValor(parseValor(linha[4].trim()));

            // Coluna 5: Forma de Pagamento
            item.setFormaPagamento(linha.length > 5 ? linha[5].trim() : null);

            // Coluna 6: Centro de Custo
            item.setCentroCusto(linha.length > 6 ? linha[6].trim() : null);

            // Coluna 7: Observações (opcional)
            item.setObservacoes(linha.length > 7 ? linha[7].trim() : null);

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar dados: " + e.getMessage());
        }

        return item;
    }

    /**
     * Cria ItemRelatorio a partir de linha Excel.
     */
    private ItemRelatorio criarItemDeLinhaExcel(Row row, int numeroLinha) {
        ItemRelatorio item = new ItemRelatorio();

        try {
            // Coluna 0: Data
            Cell dataCell = row.getCell(0);
            item.setData(parseDataExcel(dataCell));

            // Coluna 1: Descrição
            item.setDescricao(getCellValueAsString(row.getCell(1)));
            if (item.getDescricao().isEmpty()) {
                throw new IllegalArgumentException("Descrição não pode estar vazia");
            }

            // Coluna 2: Categoria
            item.setCategoria(getCellValueAsString(row.getCell(2)));

            // Coluna 3: Tipo
            item.setTipo(parseTipo(getCellValueAsString(row.getCell(3))));

            // Coluna 4: Valor
            item.setValor(parseValorExcel(row.getCell(4)));

            // Coluna 5: Forma de Pagamento
            item.setFormaPagamento(getCellValueAsString(row.getCell(5)));

            // Coluna 6: Centro de Custo
            item.setCentroCusto(getCellValueAsString(row.getCell(6)));

            // Coluna 7: Observações (opcional)
            Cell obsCell = row.getCell(7);
            item.setObservacoes(obsCell != null ? getCellValueAsString(obsCell) : null);

        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao processar dados: " + e.getMessage());
        }

        return item;
    }

    /**
     * Converte string para LocalDate tentando vários formatos.
     */
    private LocalDate parseData(String dataStr) {
        for (String formato : FORMATOS_DATA) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(formato);
                sdf.setLenient(false);
                Date date = sdf.parse(dataStr);
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (ParseException e) {
                // Tentar próximo formato
            }
        }
        throw new IllegalArgumentException("Formato de data inválido: " + dataStr +
            ". Use DD/MM/AAAA ou AAAA-MM-DD");
    }

    /**
     * Parse data de célula Excel.
     */
    private LocalDate parseDataExcel(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else if (cell.getCellType() == CellType.STRING) {
            return parseData(cell.getStringCellValue());
        }
        throw new IllegalArgumentException("Formato de data inválido na célula");
    }

    /**
     * Converte string para TipoTransacao.
     */
    private ItemRelatorio.TipoTransacao parseTipo(String tipoStr) {
        try {
            return ItemRelatorio.TipoTransacao.valueOf(tipoStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Tipo inválido: '" + tipoStr + "'. Use 'Receita' ou 'Despesa'");
        }
    }

    /**
     * Converte string para BigDecimal.
     */
    private BigDecimal parseValor(String valorStr) {
        try {
            // Aceitar tanto vírgula quanto ponto como separador decimal
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');

            DecimalFormat format = new DecimalFormat("#,##0.00", symbols);
            format.setParseBigDecimal(true);

            // Remover espaços e símbolo de moeda se houver
            String cleanValue = valorStr.trim()
                .replace("R$", "")
                .replace(" ", "");

            // Tentar parse com vírgula
            try {
                return (BigDecimal) format.parse(cleanValue);
            } catch (ParseException e) {
                // Tentar com ponto
                return new BigDecimal(cleanValue.replace(',', '.'));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Valor inválido: " + valorStr);
        }
    }

    /**
     * Parse valor de célula Excel.
     */
    private BigDecimal parseValorExcel(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.STRING) {
            return parseValor(cell.getStringCellValue());
        }
        throw new IllegalArgumentException("Valor inválido na célula");
    }

    /**
     * Obtém valor de célula como String.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * Verifica se linha está vazia.
     */
    private boolean isLinhaVazia(String[] linha) {
        if (linha == null || linha.length == 0) {
            return true;
        }
        for (String valor : linha) {
            if (valor != null && !valor.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se linha Excel está vazia.
     */
    private boolean isLinhaVaziaExcel(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
