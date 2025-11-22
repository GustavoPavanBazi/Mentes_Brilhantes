-- --------------------------------------------------------
-- Servidor: 127.0.0.1
-- Versao do servidor: 8.4.3 - MySQL Community Server - GPL
-- OS do Servidor: Win64
-- HeidiSQL Versao: 12.8.0.6908
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
-- TABELA: cad_administrador
-- ========================================================
CREATE TABLE IF NOT EXISTS `cad_administrador` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `senha` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Senha com hash',
  `data_cadastro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `data_atualizacao` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Administradores do sistema';

-- Inserir dados do administrador
INSERT INTO `cad_administrador` (`id_admin`, `email`, `senha`, `data_cadastro`, `data_atualizacao`) VALUES
(1, 'adm_mentes.brilhantes@gmail.com', '$2y$10$ZX2RUm.Urt.SQiod7B6NXuWAo0SSOEbfsa6PPAlN915V0E7cvkP92', '2025-10-22 14:30:04', '2025-10-22 14:30:04')
ON DUPLICATE KEY UPDATE email = email;

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
  `token_recuperacao` VARCHAR(100) NULL DEFAULT NULL,
  `token_expiracao` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id_responsa`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `cpf` (`cpf`),
  KEY `idx_nome` (`nome`),
  KEY `idx_email` (`email`),
  KEY `idx_data_cadastro` (`data_cadastro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Responsaveis pelos neurodivergentes';

-- Inserir dados do responsavel
INSERT INTO `cad_responsavel` (
  `nome`,
  `email`,
  `data_nascimento`,
  `cpf`,
  `sexo`,
  `celular`,
  `cep`,
  `rua`,
  `bairro`,
  `cidade`,
  `numero`,
  `complemento`,
  `senha`
) VALUES (
  'Responsavel Teste',
  'responsavel@gmail.com',
  '1980-03-15',
  '12345678901',
  'Masculino',
  '11987654321',
  '01310100',
  'Avenida Paulista',
  'Bela Vista',
  'Sao Paulo',
  '1578',
  'Apto 102',
  '123456'
);

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

-- Inserir dados do especialista
INSERT INTO `cad_especialista` (
  `nome`,
  `email`,
  `cpf`,
  `data_nascimento`,
  `sexo`,
  `formacao`,
  `certificado`,
  `descricao`,
  `senha`,
  `status`
) VALUES (
  'Especialista Teste',
  'especialista@gmail.com',
  '15203592845',
  '1980-03-15',
  'Masculino',
  'Psicologia',
  'certificados/teste_certificado.pdf',
  'Especialista em neuropsicologia com experiencia em autismo',
  '123456',
  'aprovado'
);

-- ========================================================
-- TABELA: cad_neurodivergentes
-- Armazena dados das pessoas neurodivergentes
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

-- Inserir dados de neurodivergente de teste
INSERT INTO `cad_neurodivergentes` (
  `nome`,
  `email`,
  `data_nascimento`,
  `rg`,
  `cpf`,
  `sexo`,
  `celular`,
  `cep`,
  `rua`,
  `bairro`,
  `cidade`,
  `numero`,
  `complemento`,
  `senha`,
  `id_responsa`
) VALUES (
  'Autista Teste',
  'autista@gmail.com',
  '2010-06-20',
  '123456789012',
  '98765432109',
  'Masculino',
  '11999887766',
  '01310100',
  'Avenida Paulista',
  'Bela Vista',
  'Sao Paulo',
  '1578',
  'Apto 102',
  '123456',
  1
);
-- Tabela para armazenar os chats
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

-- ========================================================
-- TABELA: conversas
-- Armazena as conversas/sessões de chat
-- ========================================================
CREATE TABLE conversas (
    id_conversa INT AUTO_INCREMENT PRIMARY KEY,
    id_responsavel INT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    deletada TINYINT(1) DEFAULT 0 COMMENT 'Soft delete: 0=ativa, 1=oculta do usuário',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_ultima_mensagem TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_responsavel (id_responsavel),
    INDEX idx_data_ultima (data_ultima_mensagem),
    INDEX idx_deletada (deletada),
    FOREIGN KEY (id_responsavel) REFERENCES cad_responsavel(id_responsa) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE mensagens (
    id_mensagem INT AUTO_INCREMENT PRIMARY KEY,
    id_conversa INT NOT NULL,
    remetente ENUM('usuario', 'ia') NOT NULL,
    conteudo TEXT NOT NULL,
    data_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversa (id_conversa),
    INDEX idx_data_envio (data_envio),
    FOREIGN KEY (id_conversa) REFERENCES conversas(id_conversa) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================================
-- TABELA: vinculo_responsavel_neurodivergente
-- Permite que múltiplos responsáveis gerenciem o mesmo neurodivergente
-- ========================================================
CREATE TABLE IF NOT EXISTS `vinculo_responsavel_neurodivergente` (
  `id_vinculo` int NOT NULL AUTO_INCREMENT,
  `id_responsa` int NOT NULL COMMENT 'ID do responsável secundário',
  `id_neuro` int NOT NULL COMMENT 'ID do neurodivergente vinculado',
  `tipo_vinculo` enum('principal','secundario') DEFAULT 'secundario',
  `data_vinculo` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_vinculo`),
  UNIQUE KEY `unique_vinculo` (`id_responsa`,`id_neuro`),
  KEY `fk_vinculo_responsavel` (`id_responsa`),
  KEY `fk_vinculo_neurodivergente` (`id_neuro`),
  CONSTRAINT `fk_vinculo_neurodivergente` 
    FOREIGN KEY (`id_neuro`) 
    REFERENCES `cad_neurodivergentes` (`id_neuro`) 
    ON DELETE CASCADE,
  CONSTRAINT `fk_vinculo_responsavel` 
    FOREIGN KEY (`id_responsa`) 
    REFERENCES `cad_responsavel` (`id_responsa`) 
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Vínculos entre responsáveis e neurodivergentes';

-- ========================================================
-- TABELA: configuracoes_usuario
-- Armazena as preferências e configurações de cada responsável
-- ========================================================



SELECT 'BANCO DE DADOS CONFIGURADO COM SUCESSO!' as Status;