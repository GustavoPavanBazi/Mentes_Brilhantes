-- --------------------------------------------------------
-- Servidor:                     127.0.0.1
-- Versão do servidor:           8.0.30 - MySQL Community Server - GPL
-- OS do Servidor:               Win64
-- HeidiSQL Versão:              12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Copiando estrutura do banco de dados para mentes_brilhantes
CREATE DATABASE IF NOT EXISTS `mentes_brilhantes` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `mentes_brilhantes`;

-- Copiando estrutura para tabela mentes_brilhantes.cad_especialista
CREATE TABLE IF NOT EXISTS `cad_especialista` (
  `id_especial` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cpf` char(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `formacao` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `certificado` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `descricao` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `perfil` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_especial`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `cpf` (`cpf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela mentes_brilhantes.cad_especialista: ~0 rows (aproximadamente)

-- Copiando estrutura para tabela mentes_brilhantes.cad_neurodivergentes
CREATE TABLE IF NOT EXISTS `cad_neurodivergentes` (
  `id_neuro` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `email` varchar(100) NOT NULL,
  `rg` char(12) NOT NULL,
  `cpf` char(14) NOT NULL,
  `celular` char(15) NOT NULL,
  `cep` char(9) NOT NULL,
  `rua` varchar(100) NOT NULL,
  `bairro` varchar(80) NOT NULL,
  `cidade` varchar(80) NOT NULL,
  `numero` varchar(10) NOT NULL,
  `complemento` varchar(50) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `id_responsa` int NOT NULL,
  `perfil` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_neuro`) USING BTREE,
  UNIQUE KEY `cpf` (`cpf`),
  UNIQUE KEY `rg` (`rg`),
  KEY `FK__cad_responsavel` (`id_responsa`),
  CONSTRAINT `FK__cad_responsavel` FOREIGN KEY (`id_responsa`) REFERENCES `cad_responsavel` (`id_responsa`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela mentes_brilhantes.cad_neurodivergentes: ~0 rows (aproximadamente)

-- Copiando estrutura para tabela mentes_brilhantes.cad_responsavel
CREATE TABLE IF NOT EXISTS `cad_responsavel` (
  `id_responsa` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `email` varchar(100) NOT NULL,
  `cpf` char(14) NOT NULL,
  `celular` char(15) NOT NULL,
  `cep` char(9) NOT NULL,
  `rua` varchar(100) NOT NULL,
  `bairro` varchar(80) NOT NULL,
  `cidade` varchar(80) NOT NULL,
  `numero` varchar(10) NOT NULL,
  `complemento` varchar(50) NOT NULL,
  `senha` varchar(250) NOT NULL,
  `perfil` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id_responsa`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `cpf` (`cpf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Copiando dados para a tabela mentes_brilhantes.cad_responsavel: ~0 rows (aproximadamente)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
