-- ===================================================================
-- Script de Criação do Banco de Dados - Carpa Contabilidade
-- ===================================================================
--
-- Este script cria o banco de dados necessário para a aplicação.
-- Execute este script antes de iniciar a aplicação.
--
-- Versão: 1.0.0
-- Data: 2024
-- ===================================================================

-- Conecte-se ao PostgreSQL como superusuário (postgres) e execute:

-- 1. Criar o banco de dados
DROP DATABASE IF EXISTS carpa_contabilidade;
CREATE DATABASE carpa_contabilidade
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Portuguese_Brazil.1252'
    LC_CTYPE = 'Portuguese_Brazil.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE carpa_contabilidade IS 'Banco de dados do sistema Carpa Contabilidade';

-- ===================================================================
-- Conecte-se ao banco carpa_contabilidade para criar as tabelas
-- ===================================================================

\c carpa_contabilidade

-- 2. A tabela usuarios será criada automaticamente pelo Hibernate
-- Este é apenas um exemplo da estrutura que será criada:

/*
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(50) NOT NULL CHECK (tipo_usuario IN ('ADMIN', 'CLIENTE')),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhorar a performance
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_tipo ON usuarios(tipo_usuario);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema';
COMMENT ON COLUMN usuarios.id IS 'Identificador único do usuário';
COMMENT ON COLUMN usuarios.nome IS 'Nome completo do usuário';
COMMENT ON COLUMN usuarios.email IS 'Email do usuário (usado para login)';
COMMENT ON COLUMN usuarios.senha IS 'Senha criptografada com BCrypt';
COMMENT ON COLUMN usuarios.tipo_usuario IS 'Tipo do usuário (ADMIN ou CLIENTE)';
COMMENT ON COLUMN usuarios.ativo IS 'Indica se o usuário está ativo no sistema';
COMMENT ON COLUMN usuarios.data_criacao IS 'Data e hora de criação do registro';
*/

-- ===================================================================
-- Configurações adicionais (opcional)
-- ===================================================================

-- Criar extensão para UUIDs (se necessário no futuro)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Configurar timezone
SET timezone = 'America/Sao_Paulo';

-- ===================================================================
-- Instruções de Uso
-- ===================================================================

-- Para executar este script:
--
-- OPÇÃO 1: Via linha de comando
-- psql -U postgres -f database/create_database.sql
--
-- OPÇÃO 2: Via pgAdmin
-- 1. Abra o pgAdmin
-- 2. Conecte-se ao servidor PostgreSQL
-- 3. Clique em "Tools" > "Query Tool"
-- 4. Abra este arquivo ou copie e cole o conteúdo
-- 5. Execute o script
--
-- OPÇÃO 3: Via terminal PostgreSQL
-- 1. Abra o terminal do PostgreSQL
-- 2. Execute: \i database/create_database.sql

-- ===================================================================
-- Verificação
-- ===================================================================

-- Para verificar se o banco foi criado corretamente:
SELECT
    datname as "Database",
    pg_encoding_to_char(encoding) as "Encoding",
    datcollate as "Collate",
    datctype as "Ctype"
FROM pg_database
WHERE datname = 'carpa_contabilidade';

-- ===================================================================
-- FIM DO SCRIPT
-- ===================================================================
