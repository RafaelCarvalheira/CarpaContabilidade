package com.carpa.contabilidade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Carpa Contabilidade.
 * Esta classe inicia a aplicação Spring Boot.
 *
 * @author Carpa Contabilidade
 * @version 1.0.0
 */
@SpringBootApplication
public class CarpaContabilidadeApplication {

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args Argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(CarpaContabilidadeApplication.class, args);
        System.out.println("\n" +
                "╔═══════════════════════════════════════════════════════════╗\n" +
                "║                                                           ║\n" +
                "║         CARPA CONTABILIDADE - Sistema Iniciado            ║\n" +
                "║                                                           ║\n" +
                "║  Acesse: http://localhost:8080                            ║\n" +
                "║                                                           ║\n" +
                "║  Usuários de Teste:                                       ║\n" +
                "║  • Admin:   admin@carpa.com / admin123                    ║\n" +
                "║  • Cliente: cliente@teste.com / cliente123                ║\n" +
                "║                                                           ║\n" +
                "╚═══════════════════════════════════════════════════════════╝\n");
    }
}
