-- --------------------------------------------------------
-- Servidor:                     127.0.0.1
-- Versao do servidor:           8.4.3 - MySQL Community Server - GPL
-- OS do Servidor:               Win64
-- HeidiSQL Versao:              12.8.0.6908
-- ATUALIZADO E CORRIGIDO: Script completo e funcional
-- Data: 2025
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ========================================================
-- CRIAR BANCO DE DADOS SE NAO EXISTIR
-- ========================================================
CREATE DATABASE IF NOT EXISTS `mentes_brilhantes` 
/*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ 
/*!80016 DEFAULT ENCRYPTION='N' */;

USE `mentes_brilhantes`;

-- ========================================================
-- TABELA: cad_responsavel
-- Armazena dados dos responsaveis pelos neurodivergentes
-- ========================================================
DROP TABLE IF EXISTS `cad_neurodivergentes`;
DROP TABLE IF EXISTS `cad_responsavel`;

CREATE TABLE `cad_responsavel` (
  `id_responsa` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `email` varchar(100) NOT NULL,
  `data_nascimento` date DEFAULT NULL COMMENT 'Data de nascimento do responsavel',
  `cpf` char(11) NOT NULL COMMENT 'CPF sem formatacao (apenas numeros)',
  `sexo` enum('Masculino','Feminino') DEFAULT NULL,
  `celular` char(11) NOT NULL COMMENT 'Celular sem formatacao (apenas numeros)',
  `cep` char(8) NOT NULL COMMENT 'CEP sem formatacao (apenas numeros)',
  `rua` varchar(100) NOT NULL,
  `bairro` varchar(80) NOT NULL,
  `cidade` varchar(80) NOT NULL,
  `numero` varchar(10) NOT NULL,
  `complemento` varchar(50) DEFAULT NULL,
  `senha` varchar(255) NOT NULL COMMENT 'Senha com hash',
  `perfil` varchar(255) DEFAULT NULL COMMENT 'Caminho da foto de perfil',
  `data_cadastro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_responsa`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `cpf` (`cpf`),
  KEY `idx_nome` (`nome`),
  KEY `idx_email` (`email`),
  KEY `idx_data_cadastro` (`data_cadastro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Responsaveis pelos neurodivergentes';

-- ========================================================
-- TABELA: cad_especialista
-- Armazena dados dos especialistas (psicologos, terapeutas, etc)
-- ========================================================
DROP TABLE IF EXISTS `cad_especialista`;

CREATE TABLE `cad_especialista` (
  `id_especial` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `email` varchar(120) NOT NULL,
  `cpf` char(11) NOT NULL COMMENT 'CPF sem formatacao (apenas numeros)',
  `data_nascimento` date DEFAULT NULL,
  `sexo` enum('Masculino','Feminino','Outro','Prefiro nao informar') DEFAULT NULL,
  `formacao` varchar(100) NOT NULL COMMENT 'Formacao academica',
  `certificado` varchar(255) NOT NULL COMMENT 'Caminho do arquivo do certificado',
  `descricao` text COMMENT 'Descricao profissional do especialista',
  `perfil` varchar(255) DEFAULT NULL COMMENT 'Caminho da foto de perfil',
  `senha` varchar(255) NOT NULL COMMENT 'Senha com hash',
  `status` enum('pendente','aprovado','rejeitado') DEFAULT 'pendente' COMMENT 'Status da aprovacao',
  `data_cadastro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_especial`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `cpf` (`cpf`),
  KEY `idx_status` (`status`),
  KEY `idx_nome` (`nome`),
  KEY `idx_data_cadastro` (`data_cadastro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Especialistas cadastrados na plataforma';

-- ========================================================
-- TABELA: cad_neurodivergentes
-- Armazena dados das pessoas neurodivergentes
-- CORRIGIDA E ATUALIZADA
-- ========================================================

CREATE TABLE `cad_neurodivergentes` (
  `id_neuro` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `email` varchar(100) NOT NULL,
  `data_nascimento` date DEFAULT NULL COMMENT 'Data de nascimento do neurodivergente',
  `rg` char(12) NOT NULL COMMENT 'RG sem formatacao (apenas numeros)',
  `cpf` char(11) NOT NULL COMMENT 'CPF sem formatacao (apenas numeros)',
  `sexo` enum('Masculino','Feminino') NOT NULL,
  `celular` char(11) DEFAULT NULL COMMENT 'Celular sem formatacao - OPCIONAL',
  `cep` char(8) NOT NULL COMMENT 'CEP sem formatacao (apenas numeros)',
  `rua` varchar(100) NOT NULL,
  `bairro` varchar(80) NOT NULL,
  `cidade` varchar(80) NOT NULL,
  `numero` varchar(10) NOT NULL,
  `complemento` varchar(50) DEFAULT NULL COMMENT 'Complemento do endereco - OPCIONAL',
  `senha` varchar(255) NOT NULL COMMENT 'Senha com hash',
  `perfil` varchar(255) DEFAULT NULL COMMENT 'Caminho da foto de perfil - OPCIONAL',
  `id_responsa` int NOT NULL COMMENT 'ID do responsavel (chave estrangeira)',
  `data_cadastro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_neuro`),
  UNIQUE KEY `cpf` (`cpf`),
  UNIQUE KEY `rg` (`rg`),
  UNIQUE KEY `email` (`email`),
  KEY `FK_neurodivergentes_responsavel` (`id_responsa`),
  KEY `idx_nome` (`nome`),
  KEY `idx_data_cadastro` (`data_cadastro`),
  KEY `idx_data_nascimento` (`data_nascimento`),
  CONSTRAINT `FK_neurodivergentes_responsavel` FOREIGN KEY (`id_responsa`) 
    REFERENCES `cad_responsavel` (`id_responsa`) 
    ON DELETE CASCADE 
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Neurodivergentes cadastrados';

-- ========================================================
-- VERIFICACOES E DIAGNOSTICOS
-- ========================================================

-- Verificar estrutura da tabela principal
SELECT 'Estrutura da tabela cad_neurodivergentes:' as Info;
DESCRIBE `cad_neurodivergentes`;

-- Verificar se ha responsaveis cadastrados
SELECT 'Total de responsaveis cadastrados:' as Info, COUNT(*) as Total 
FROM `cad_responsavel`;

-- Verificar constraints de chave estrangeira
SELECT 'Chaves estrangeiras configuradas:' as Info;
SELECT 
    TABLE_NAME as Tabela,
    COLUMN_NAME as Coluna,
    CONSTRAINT_NAME as Constraint_Name,
    REFERENCED_TABLE_NAME as Tabela_Referenciada,
    REFERENCED_COLUMN_NAME as Coluna_Referenciada
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'mentes_brilhantes'
    AND TABLE_NAME = 'cad_neurodivergentes'
    AND REFERENCED_TABLE_NAME IS NOT NULL;

-- Verificar encoding das tabelas
SELECT 'Encoding das tabelas:' as Info;
SELECT 
    TABLE_NAME as Tabela,
    TABLE_COLLATION as Collation,
    CHARACTER_SET_NAME as Charset
FROM INFORMATION_SCHEMA.TABLES t
JOIN INFORMATION_SCHEMA.COLLATION_CHARACTER_SET_APPLICABILITY ccsa 
    ON t.TABLE_COLLATION = ccsa.COLLATION_NAME
WHERE TABLE_SCHEMA = 'mentes_brilhantes'
    AND TABLE_NAME IN ('cad_responsavel', 'cad_neurodivergentes', 'cad_especialista');

-- ========================================================
-- DADOS DE TESTE (OPCIONAL - Descomente para usar)
-- ========================================================

/*
-- Inserir um responsavel de teste
INSERT INTO `cad_responsavel` 
    (`nome`, `email`, `data_nascimento`, `cpf`, `sexo`, `celular`, `cep`, `rua`, `bairro`, `cidade`, `numero`, `senha`) 
VALUES 
    ('Maria Silva', 'maria.teste@example.com', '1985-05-15', '12345678901', 'Feminino', '11987654321', '01310100', 'Avenida Paulista', 'Bela Vista', 'Sao Paulo', '1000', '$2y$10$abcdefghijklmnopqrstuv');

-- Verificar ID gerado
SELECT LAST_INSERT_ID() as id_responsavel_teste;
*/

-- ========================================================
-- RESTAURAR CONFIGURACOES
-- ========================================================
/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

-- ========================================================
-- MENSAGEM FINAL
-- ========================================================
SELECT 'BANCO DE DADOS CONFIGURADO COM SUCESSO!' as Status,
       'Execute o script PHP para testar o cadastro' as Proximo_Passo;

SELECT 'Principais correcoes aplicadas:' as Info;
SELECT '1. Campo data_nascimento adicionado' as Correcao
UNION ALL SELECT '2. Encoding UTF-8 configurado em todas as tabelas'
UNION ALL SELECT '3. Campos opcionais (celular, complemento, perfil) aceitam NULL'
UNION ALL SELECT '4. CPF, RG e Celular armazenam apenas numeros'
UNION ALL SELECT '5. Indices otimizados para melhor performance'
UNION ALL SELECT '6. Chave estrangeira com CASCADE configurada'
UNION ALL SELECT '7. Timestamps automaticos adicionados'
UNION ALL SELECT '8. Ordem correta de DROP TABLE (primeiro filha, depois pai)'
UNION ALL SELECT '9. Caracteres especiais removidos para compatibilidade';