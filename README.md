# CARPA Contabilidade - Sistema Web

Sistema web completo para gerenciamento de contabilidade desenvolvido com Spring Boot, PostgreSQL e Thymeleaf.

## 📋 Tecnologias Utilizadas

- **Backend:** Java 17 + Spring Boot 3.2.5
- **Build Tool:** Gradle
- **Banco de Dados:** PostgreSQL
- **Template Engine:** Thymeleaf
- **Segurança:** Spring Security com BCrypt
- **Frontend:** HTML5 + CSS3
- **ORM:** Spring Data JPA / Hibernate

## 🚀 Funcionalidades

### Autenticação e Autorização
- Login seguro com Spring Security
- Senhas criptografadas com BCrypt
- Dois tipos de usuários: ADMIN e CLIENTE
- Redirecionamento automático baseado no tipo de usuário

### Dashboard Administrativo
- Painel completo para administradores
- Estatísticas e métricas
- Gerenciamento de clientes (preparado para implementação)
- Acesso a relatórios e configurações

### Dashboard do Cliente
- Portal personalizado para clientes
- Visualização de documentos
- Acompanhamento de faturas
- Sistema de suporte

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java JDK 17** ou superior
- **PostgreSQL 12** ou superior
- **Gradle** (ou use o wrapper incluído)
- **Git** (opcional)

## 🔧 Instalação e Configuração

### 1. Criar o Banco de Dados

Execute o script SQL incluído no projeto para criar o banco de dados:

```sql
-- Conecte-se ao PostgreSQL e execute:
CREATE DATABASE carpa_contabilidade;
```

Ou use o script fornecido em `database/create_database.sql`

### 2. Configurar o Banco de Dados

As configurações padrão estão em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/carpa_contabilidade
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**Importante:** Altere as credenciais se necessário!

### 3. Compilar o Projeto

#### No Windows:
```bash
gradlew.bat clean build
```

#### No Linux/Mac:
```bash
./gradlew clean build
```

### 4. Executar a Aplicação

#### Opção 1: Usando Gradle
```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

#### Opção 2: Usando o JAR
```bash
java -jar build/libs/carpa-contabilidade-1.0.0.jar
```

### 5. Acessar o Sistema

Abra seu navegador e acesse:
```
http://localhost:8080
```

## 👥 Usuários de Teste

O sistema cria automaticamente dois usuários para teste:

| Tipo | Email | Senha |
|------|-------|-------|
| **Administrador** | admin@carpa.com | admin123 |
| **Cliente** | cliente@teste.com | cliente123 |

## 📁 Estrutura do Projeto

```
carpa-contabilidade/
│
├── src/
│   ├── main/
│   │   ├── java/com/carpa/contabilidade/
│   │   │   ├── config/              # Configurações
│   │   │   │   ├── DataLoader.java
│   │   │   │   └── SecurityConfig.java
│   │   │   │
│   │   │   ├── controller/          # Controllers
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── ClienteController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   └── LoginController.java
│   │   │   │
│   │   │   ├── model/               # Entidades
│   │   │   │   ├── TipoUsuario.java
│   │   │   │   └── Usuario.java
│   │   │   │
│   │   │   ├── repository/          # Repositories
│   │   │   │   └── UsuarioRepository.java
│   │   │   │
│   │   │   ├── security/            # Segurança
│   │   │   │   ├── CustomAuthenticationSuccessHandler.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   │
│   │   │   ├── service/             # Serviços
│   │   │   │   └── UsuarioService.java
│   │   │   │
│   │   │   └── CarpaContabilidadeApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   └── css/             # Arquivos CSS
│   │       │       ├── style.css
│   │       │       ├── login.css
│   │       │       └── dashboard.css
│   │       │
│   │       ├── templates/           # Templates HTML
│   │       │   ├── admin/
│   │       │   │   └── dashboard.html
│   │       │   ├── cliente/
│   │       │   │   └── dashboard.html
│   │       │   └── login.html
│   │       │
│   │       └── application.properties
│   │
│   └── test/                        # Testes
│
├── database/
│   └── create_database.sql         # Script de criação do banco
│
├── build.gradle                     # Configuração do Gradle
├── settings.gradle
└── README.md
```

## 🔐 Segurança

### Recursos de Segurança Implementados:
- ✅ Autenticação via Spring Security
- ✅ Senhas criptografadas com BCrypt
- ✅ Proteção contra CSRF
- ✅ Autorização baseada em roles (ADMIN/CLIENTE)
- ✅ Sessões seguras
- ✅ Rotas protegidas

### Configurações de Segurança:

- Rotas públicas: `/login`, `/css/**`, `/js/**`, `/images/**`
- Rotas admin: `/admin/**` (apenas ROLE_ADMIN)
- Rotas cliente: `/cliente/**` (apenas ROLE_CLIENTE)
- Todas as outras rotas requerem autenticação

## 🎨 Personalização

### Alterar Cores
Edite as variáveis CSS em `src/main/resources/static/css/style.css`:

```css
:root {
    --primary-color: #1e3a8a;  /* Azul principal */
    --success-color: #10b981;  /* Verde sucesso */
    --error-color: #ef4444;    /* Vermelho erro */
    /* ... outras cores */
}
```

### Adicionar Novos Usuários
Edite `src/main/java/com/carpa/contabilidade/config/DataLoader.java`

## 🐛 Solução de Problemas

### Erro de Conexão com o Banco
- Verifique se o PostgreSQL está rodando
- Confirme as credenciais em `application.properties`
- Verifique se o banco `carpa_contabilidade` foi criado

### Porta 8080 já em uso
Altere a porta em `application.properties`:
```properties
server.port=8081
```

### Erro ao compilar
Certifique-se de ter o Java 17 instalado:
```bash
java -version
```

## 📝 Próximos Passos / Melhorias Futuras

- [ ] Implementar CRUD completo de clientes
- [ ] Sistema de upload de documentos
- [ ] Geração de relatórios em PDF
- [ ] API REST para integração
- [ ] Recuperação de senha
- [ ] Notificações por email
- [ ] Dashboard com gráficos
- [ ] Auditoria de ações

## 👨‍💻 Desenvolvimento

### Executar em Modo de Desenvolvimento
```bash
gradlew.bat bootRun
```

O Spring DevTools está incluído e permitirá recarregamento automático durante o desenvolvimento.

### Executar Testes
```bash
gradlew.bat test
```

## 📄 Licença

Este projeto é privado e proprietário da Carpa Contabilidade.

## 📞 Suporte

Para questões e suporte:
- Email: contato@carpa.com
- Telefone: (XX) XXXX-XXXX

---

**Desenvolvido com ❤️ para Carpa Contabilidade**
