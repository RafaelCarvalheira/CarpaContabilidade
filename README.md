# CARPA Contabilidade - Sistema Web de Gestão Contábil

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue?style=for-the-badge&logo=postgresql)
![Status](https://img.shields.io/badge/Status-Concluído-success?style=for-the-badge)

**Projeto de Laboratório de Programação II**

**Instituto Militar de Engenharia (IME)**

</div>

---

## 👥 Integrantes

- **Rafael Carvalheira** - Desenvolvimento Full Stack
- **Marcell Parra** - Desenvolvimento Full Stack

**Orientador:** Cap Vanzan
**Disciplina:** Laboratório de Programação II
**Instituição:** Instituto Militar de Engenharia (IME)
**Ano:** 2024

---

## 📋 Sobre o Projeto

Sistema web completo para gerenciamento de serviços contábeis, desenvolvido como projeto final da disciplina de Laboratório de Programação II do IME. O sistema permite que escritórios de contabilidade gerenciem seus clientes e que os clientes acessem seus documentos e relatórios financeiros de forma online e automatizada.

### 🎯 Principais Funcionalidades

#### 🔐 Autenticação e Segurança
- Login seguro com Spring Security
- Criptografia de senhas com BCrypt
- Controle de acesso baseado em roles (ADMIN/CLIENTE)
- Sessões seguras com CSRF protection

#### 👨‍💼 Módulo Administrativo
- Dashboard com estatísticas do sistema
- CRUD completo de usuários
- API REST para gerenciamento
- Interface responsiva de administração

#### 👤 Módulo do Cliente
- **Upload de Documentos CSV/Excel**
  - Drag and drop de arquivos
  - Validação automática de formato
  - Processamento em tempo real
  - Histórico de documentos enviados

- **Geração Automática de Relatórios**
  - Processamento de arquivos CSV/Excel
  - Cálculo automático de métricas financeiras
  - Análises por categoria, centro de custo e forma de pagamento

- **Dashboard Interativo**
  - 4 KPIs principais (Receita, Despesa, Saldo, Margem)
  - Gráficos de pizza (Chart.js)
  - Gráfico de barras comparativo
  - Top 10 receitas e despesas
  - Filtragem por mês e ano

---

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.5** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM (Object-Relational Mapping)
- **PostgreSQL** - Banco de dados relacional

### Frontend
- **Thymeleaf** - Template engine server-side
- **HTML5 + CSS3** - Estrutura e estilização
- **JavaScript ES6+** - Interatividade
- **Chart.js 4.4.0** - Gráficos interativos
- **Fetch API** - Requisições AJAX

### Bibliotecas de Processamento
- **Apache POI 5.2.5** - Leitura de arquivos Excel (.xlsx)
- **OpenCSV 5.9** - Leitura de arquivos CSV
- **Commons IO 2.15.1** - Utilitários de I/O

### Build e Deploy
- **Gradle 8.x** - Gerenciamento de dependências
- **Tomcat Embedded** - Servidor de aplicação

---

## 📦 Estrutura do Projeto

```
CarpaContabilidade/
├── src/
│   ├── main/
│   │   ├── java/com/carpa/contabilidade/
│   │   │   ├── config/              # Configurações (Security, DataLoader)
│   │   │   ├── controller/          # Controllers MVC e REST
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── repository/          # Repositories (Spring Data)
│   │   │   ├── security/            # Customizações de segurança
│   │   │   └── service/             # Camada de negócio
│   │   └── resources/
│   │       ├── static/css/          # Arquivos CSS
│   │       ├── templates/           # Templates Thymeleaf
│   │       └── application.properties
│   └── test/                        # Testes unitários
├── database/                        # Scripts SQL
├── docs/                            # Documentação
├── uploads/                         # Arquivos enviados (gitignored)
├── build.gradle                     # Configuração Gradle
├── DOCUMENTACAO_PROJETO.md         # Documentação completa
└── README.md                        # Este arquivo
```

---

## 🗄️ Modelo de Dados

### Entidades Principais

#### 1. Usuario
Armazena informações de administradores e clientes.
- Atributos: id, nome, email, senha, tipo_usuario, ativo, data_criacao

#### 2. Documento
Metadados dos arquivos CSV/Excel enviados.
- Atributos: id, nome_arquivo, tipo_arquivo, tamanho, caminho_storage, mes_referencia, ano_referencia, usuario_id, status, data_upload

#### 3. Relatorio
Relatórios mensais gerados com métricas calculadas.
- Atributos: id, mes_referencia, ano_referencia, documento_id, usuario_id, receita_total, despesa_total, saldo, margem_lucro, total_transacoes

#### 4. ItemRelatorio
Transações individuais do CSV processado.
- Atributos: id, relatorio_id, data, descricao, categoria, tipo, valor, forma_pagamento, centro_custo

### Relacionamentos
- Usuario 1:N Documento
- Usuario 1:N Relatorio
- Documento 1:1 Relatorio
- Relatorio 1:N ItemRelatorio

---

## 🔧 Instalação e Execução

### Pré-requisitos

- **Java JDK 17** ou superior
- **PostgreSQL 12** ou superior
- **Gradle** (incluído via wrapper)
- **Git** (para clonar o repositório)

### Passo a Passo

#### 1. Clonar o Repositório
```bash
git clone https://github.com/[seu-usuario]/CarpaContability.git
cd CarpaContability/CarpaContabilidade
```

#### 2. Criar o Banco de Dados
```sql
-- Conectar ao PostgreSQL
psql -U postgres

-- Criar banco
CREATE DATABASE carpa_contabilidade;

-- Sair
\q
```

#### 3. Configurar Credenciais (se necessário)
Edite `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/carpa_contabilidade
spring.datasource.username=postgres
spring.datasource.password=postgres
```

#### 4. Compilar o Projeto
**Windows:**
```bash
gradlew.bat clean build
```

**Linux/Mac:**
```bash
./gradlew clean build
```

#### 5. Executar a Aplicação
**Windows:**
```bash
gradlew.bat bootRun
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

#### 6. Acessar o Sistema
```
http://localhost:8080
```

### 👥 Usuários de Teste

O sistema cria automaticamente dois usuários:

| Tipo | Email | Senha | Descrição |
|------|-------|-------|-----------|
| **ADMIN** | admin@carpa.com | admin123 | Acesso administrativo completo |
| **CLIENTE** | cliente@teste.com | cliente123 | Acesso de cliente padrão |

---

## 📊 Formato de Arquivo CSV

O sistema aceita arquivos CSV com o seguinte formato:

```csv
Data,Descrição,Categoria,Tipo,Valor,Forma_Pagamento,Centro_Custo,Observações
01/05/2024,Venda de produtos,Vendas,Receita,15000.00,PIX,Comercial,Pedido 123
10/05/2024,Folha de pagamento,Folha de Pagamento,Despesa,12000.00,Transferência,RH,Maio/2024
```

### Colunas Obrigatórias:
- **Data**: DD/MM/AAAA ou AAAA-MM-DD
- **Descrição**: Texto livre
- **Categoria**: Nome da categoria contábil
- **Tipo**: "Receita" ou "Despesa"
- **Valor**: Número decimal (aceita ponto ou vírgula)
- **Forma_Pagamento**: Método de pagamento
- **Centro_Custo**: Departamento/área
- **Observações**: Campo opcional

Um arquivo de exemplo está incluído: `exemplo_dados_maio_2024.csv`

---

## 🔌 API REST

### Endpoints Principais

#### Gestão de Usuários (ADMIN)
- `GET /api/usuarios` - Listar usuários
- `GET /api/usuarios/{id}` - Buscar por ID
- `POST /api/usuarios` - Criar usuário
- `PUT /api/usuarios/{id}` - Atualizar usuário
- `DELETE /api/usuarios/{id}` - Remover usuário

#### Gestão de Documentos (CLIENTE)
- `POST /api/documentos/upload` - Upload de CSV/Excel
- `GET /api/documentos` - Listar documentos
- `GET /api/documentos/{id}` - Buscar por ID
- `DELETE /api/documentos/{id}` - Excluir documento

#### Relatórios (CLIENTE)
- `GET /api/relatorios` - Listar relatórios (com filtros)
- `GET /api/relatorios/{id}/dados` - Dados completos do relatório
- `GET /api/relatorios/recentes` - Últimos 5 relatórios

**Documentação completa:** Ver arquivo `DOCUMENTACAO_PROJETO.md`

---

## 🎨 Capturas de Tela

### Tela de Login
![Login](docs/screenshots/login.png)

### Dashboard do Cliente
![Dashboard Cliente](docs/screenshots/dashboard_cliente.png)

### Upload de Documentos
![Upload](docs/screenshots/upload_documentos.png)

### Dashboard de Relatórios
![Relatórios](docs/screenshots/relatorios_dashboard.png)

*Mais screenshots disponíveis na documentação completa.*

---

## 🧪 Teste Rápido

### 1. Fazer Login
```
Email: cliente@teste.com
Senha: cliente123
```

### 2. Enviar Documento de Teste
- Clique em "Enviar Documento"
- Use o arquivo `exemplo_dados_maio_2024.csv`
- Mês: 5 (Maio)
- Ano: 2024
- Clique em "Enviar Documento"

### 3. Visualizar Relatório
- Clique em "Relatórios Mensais"
- Selecione o relatório de Maio/2024
- Clique em "Visualizar"
- Explore os gráficos e análises

---

## 🏗️ Arquitetura

O sistema segue o padrão **MVC** com camada de serviços:

```
Cliente (Browser)
    ↓
Spring Security (Autenticação)
    ↓
Controllers (Recebem requisições)
    ↓
Services (Lógica de negócio)
    ↓
Repositories (Acesso a dados)
    ↓
PostgreSQL (Banco de dados)
```

### Padrões de Projeto Utilizados
- **MVC** (Model-View-Controller)
- **Repository Pattern**
- **Service Layer**
- **Dependency Injection**
- **DTO** (Data Transfer Object) - onde necessário

---

## 🔐 Segurança

### Medidas Implementadas
✅ Senhas criptografadas com BCrypt (salt automático)
✅ Proteção CSRF em formulários
✅ Autorização baseada em roles
✅ Validação de entrada em múltiplas camadas
✅ Sanitização de uploads de arquivo
✅ Sessões HTTP seguras
✅ SQL Injection prevenido (JPA/Hibernate)

---

## 📚 Documentação Adicional

- **Documentação Completa:** `DOCUMENTACAO_PROJETO.md`
- **Guia de Execução:** `COMO_EXECUTAR.md`
- **Scripts SQL:** `database/create_database.sql`

---

## 🚧 Melhorias Futuras

- [ ] Exportação de relatórios em PDF
- [ ] Notificações por email
- [ ] Comparação entre períodos
- [ ] Gráfico de evolução temporal
- [ ] Upload em lote
- [ ] Testes automatizados (JUnit)
- [ ] Deploy em nuvem (Heroku/AWS)
- [ ] API pública com Swagger
- [ ] Sistema de backup automático

---

## 🤝 Contribuições

Este é um projeto acadêmico desenvolvido para a disciplina de **Laboratório de Programação II** do **Instituto Militar de Engenharia (IME)**.

**Desenvolvido por:**
- Rafael Carvalheira
- Marcell Parra

**Orientação:**
- Cap Vanzan

---

## 📄 Licença

Este projeto é de uso acadêmico e pertence ao Instituto Militar de Engenharia (IME).

---

## 📞 Contato

Para questões sobre o projeto:
- **Instituição:** Instituto Militar de Engenharia (IME)
- **Disciplina:** Laboratório de Programação II
- **Professor:** Cap Vanzan

---

<div align="center">

**Desenvolvido com 💚💛 no Instituto Militar de Engenharia**

![IME](https://img.shields.io/badge/IME-Instituto%20Militar%20de%20Engenharia-green?style=for-the-badge)

</div>
