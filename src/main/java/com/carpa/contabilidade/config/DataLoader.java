package com.carpa.contabilidade.config;

import com.carpa.contabilidade.model.TipoUsuario;
import com.carpa.contabilidade.model.Usuario;
import com.carpa.contabilidade.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Classe responsável por carregar dados iniciais no banco de dados.
 * Executa automaticamente ao iniciar a aplicação.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioService usuarioService;

    /**
     * Método executado ao iniciar a aplicação.
     * Cria usuários iniciais se ainda não existirem no banco.
     *
     * @param args Argumentos de linha de comando
     */
    @Override
    public void run(String... args) {
        // Cria usuário administrador se não existir
        if (!usuarioService.existePorEmail("admin@carpa.com")) {
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@carpa.com");
            admin.setSenha("admin123");
            admin.setTipoUsuario(TipoUsuario.ADMIN);
            admin.setAtivo(true);

            usuarioService.salvar(admin);
            System.out.println("Usuário ADMIN criado: admin@carpa.com / admin123");
        }

        // Cria usuário cliente de teste se não existir
        if (!usuarioService.existePorEmail("cliente@teste.com")) {
            Usuario cliente = new Usuario();
            cliente.setNome("Cliente Teste");
            cliente.setEmail("cliente@teste.com");
            cliente.setSenha("cliente123");
            cliente.setTipoUsuario(TipoUsuario.CLIENTE);
            cliente.setAtivo(true);

            usuarioService.salvar(cliente);
            System.out.println("Usuário CLIENTE criado: cliente@teste.com / cliente123");
        }
    }
}
