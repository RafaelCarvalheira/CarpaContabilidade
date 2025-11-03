# 🚀 Como Executar o Projeto - Guia Rápido

## Passos Simples para Rodar a Aplicação

### 1️⃣ Criar o Banco de Dados

Abra o **pgAdmin** ou o **psql** e execute:

```sql
CREATE DATABASE carpa_contabilidade;
```

Ou execute o script completo:
```bash
psql -U postgres -f database/create_database.sql
```

### 2️⃣ Configurar Credenciais (se necessário)

Se suas credenciais do PostgreSQL forem diferentes de `postgres/postgres`, edite:

**Arquivo:** `src/main/resources/application.properties`

```properties
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

### 3️⃣ Executar a Aplicação

#### Opção A: Usando o Gradle Wrapper (Recomendado)

**Windows:**
```bash
gradlew.bat bootRun
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

#### Opção B: Usando sua IDE

1. Abra o projeto na sua IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Aguarde a importação das dependências
3. Execute a classe `CarpaContabilidadeApplication.java`

### 4️⃣ Acessar o Sistema

Abra o navegador em: **http://localhost:8080**

### 5️⃣ Fazer Login

Use uma das credenciais de teste:

**Administrador:**
- Email: `admin@carpa.com`
- Senha: `admin123`

**Cliente:**
- Email: `cliente@teste.com`
- Senha: `cliente123`

---

## ⚠️ Problemas Comuns

### Erro: "Banco de dados não existe"
Execute o script SQL na etapa 1.

### Erro: "Porta 8080 já em uso"
Altere a porta em `application.properties`:
```properties
server.port=8081
```

### Erro: "Falha na autenticação do PostgreSQL"
Verifique as credenciais em `application.properties`.

---

## 📞 Precisa de Ajuda?

Consulte o arquivo **README.md** para instruções detalhadas.

**Bom desenvolvimento! 🎉**
