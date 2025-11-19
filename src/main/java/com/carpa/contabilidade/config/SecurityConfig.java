package com.carpa.contabilidade.config;

import com.carpa.contabilidade.security.CustomAuthenticationSuccessHandler;
import com.carpa.contabilidade.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança do Spring Security.
 * Define as regras de autenticação, autorização e criptografia de senhas.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    /**
     * Define o encoder de senha usando BCrypt.
     *
     * @return PasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura o provedor de autenticação.
     *
     * @return DaoAuthenticationProvider configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura o gerenciador de autenticação.
     *
     * @param authConfig Configuração de autenticação
     * @return AuthenticationManager
     * @throws Exception em caso de erro na configuração
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura a cadeia de filtros de segurança.
     * Define as regras de autorização e configurações de login/logout.
     *
     * @param http HttpSecurity para configuração
     * @return SecurityFilterChain configurado
     * @throws Exception em caso de erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso público aos recursos estáticos, landing page e login
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/login").permitAll()
                        // API de contato público
                        .requestMatchers("/api/contato/**").permitAll()
                        // Apenas ADMIN pode acessar rotas /admin/** e página de gerenciamento
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/gerenciar-usuarios.html").hasRole("ADMIN")
                        // API de usuários apenas para ADMIN
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        // API de documentos e relatórios para CLIENTE
                        .requestMatchers("/api/documentos/**").hasRole("CLIENTE")
                        .requestMatchers("/api/relatorios/**").hasRole("CLIENTE")
                        // Apenas CLIENTE pode acessar rotas /cliente/**
                        .requestMatchers("/cliente/**").hasRole("CLIENTE")
                        // Todas as outras requisições requerem autenticação
                        .anyRequest().authenticated()
                )
                // Desabilita CSRF para rotas da API REST
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .formLogin(form -> form
                        // Configurações da página de login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        // Configurações de logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
