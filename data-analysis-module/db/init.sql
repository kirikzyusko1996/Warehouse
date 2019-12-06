CREATE DATABASE warehouse;
use warehouse;

-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: warehouse
-- ------------------------------------------------------
-- Server version	5.6.44-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `act`
--

DROP TABLE IF EXISTS `act`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `act` (
  `id_act` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_act_type` smallint(5) unsigned NOT NULL,
  `id_user` bigint(20) unsigned NOT NULL,
  `deleted` date DEFAULT NULL,
  `id_warehouse` bigint(20) unsigned DEFAULT NULL,
  `note` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id_act`),
  KEY `act_ibfk_1` (`id_user`),
  KEY `act_ibfk_3` (`id_act_type`),
  KEY `act_ibfk_4` (`id_warehouse`),
  CONSTRAINT `act_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `act_ibfk_3` FOREIGN KEY (`id_act_type`) REFERENCES `act_type` (`id_act_type`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `act_ibfk_4` FOREIGN KEY (`id_warehouse`) REFERENCES `warehouse` (`id_warehouse`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `act`
--

LOCK TABLES `act` WRITE;
/*!40000 ALTER TABLE `act` DISABLE KEYS */;
INSERT INTO `act` VALUES (1,'2019-05-31 20:32:19',2,4,NULL,10,NULL),(2,'2019-05-31 20:32:19',3,5,NULL,10,NULL),(3,'2019-05-31 20:32:19',1,4,NULL,10,NULL);
/*!40000 ALTER TABLE `act` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `act_goods`
--

DROP TABLE IF EXISTS `act_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `act_goods` (
  `id_act_goods` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_act` bigint(20) unsigned NOT NULL,
  `id_goods` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_act_goods`),
  KEY `act_goods_ibfk_1` (`id_act`),
  KEY `act_goods_ibfk_2` (`id_goods`),
  CONSTRAINT `act_goods_ibfk_1` FOREIGN KEY (`id_act`) REFERENCES `act` (`id_act`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `act_goods_ibfk_2` FOREIGN KEY (`id_goods`) REFERENCES `goods` (`id_goods`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `act_goods`
--

LOCK TABLES `act_goods` WRITE;
/*!40000 ALTER TABLE `act_goods` DISABLE KEYS */;
INSERT INTO `act_goods` VALUES (1,1,3),(2,2,5),(3,2,4),(4,3,18);
/*!40000 ALTER TABLE `act_goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `act_type`
--

DROP TABLE IF EXISTS `act_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `act_type` (
  `id_act_type` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_act_type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `act_type`
--

LOCK TABLES `act_type` WRITE;
/*!40000 ALTER TABLE `act_type` DISABLE KEYS */;
INSERT INTO `act_type` VALUES (1,'WRITE_OFF_ACT'),(2,'ACT_OF_THEFT'),(3,'MISMATCH_ACT'),(4,'ACT_OF_LOSS');
/*!40000 ALTER TABLE `act_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company_price_list`
--

DROP TABLE IF EXISTS `company_price_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `company_price_list` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_warehouse_company` bigint(20) unsigned NOT NULL,
  `setting_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `price_per_month` decimal(12,2) NOT NULL,
  `comment` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `company_price_list_ibfk_1` (`id_warehouse_company`),
  CONSTRAINT `company_price_list_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company_price_list`
--

LOCK TABLES `company_price_list` WRITE;
/*!40000 ALTER TABLE `company_price_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `company_price_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `databasechangelog`
--

DROP TABLE IF EXISTS `databasechangelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `databasechangelog` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `databasechangelog`
--

LOCK TABLES `databasechangelog` WRITE;
/*!40000 ALTER TABLE `databasechangelog` DISABLE KEYS */;
INSERT INTO `databasechangelog` VALUES ('1','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',1,'EXECUTED','7:e48aac0f2db3d0f854cd2721a94e02e1','createTable','',NULL,'3.4.1',NULL,NULL),('2','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',2,'EXECUTED','7:7714be4f4111271823dfb3a3b88cbd18','createTable','',NULL,'3.4.1',NULL,NULL),('3','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',3,'EXECUTED','7:629340deabef9049c431c9aa53e0f8e7','createTable','',NULL,'3.4.1',NULL,NULL),('4','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',4,'EXECUTED','7:e0e184998d65cbb7acc4720b501ad23d','createTable','',NULL,'3.4.1',NULL,NULL),('5','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',5,'EXECUTED','7:db8550dda4b596cb10bdf238380a8928','createTable','',NULL,'3.4.1',NULL,NULL),('6','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',6,'EXECUTED','7:7f3e1fc5bca20eb6ed8a6259892896aa','createTable','',NULL,'3.4.1',NULL,NULL),('7','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',7,'EXECUTED','7:8b794f6178401a7e11f317d0423a7798','createTable','',NULL,'3.4.1',NULL,NULL),('8','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',8,'EXECUTED','7:38c6767e4478395c783070995c4a170a','createTable','',NULL,'3.4.1',NULL,NULL),('9','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',9,'EXECUTED','7:cf93936dedcb73233324825b245ddf80','createTable','',NULL,'3.4.1',NULL,NULL),('10','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:14',10,'EXECUTED','7:0638721ce37117200bacaa02ef7ed563','createTable','',NULL,'3.4.1',NULL,NULL),('11','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',11,'EXECUTED','7:d4db1cdc67dca3e9c57d64993c6728f7','createTable','',NULL,'3.4.1',NULL,NULL),('12','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',12,'EXECUTED','7:caed34793676f8c0429afcf3907515d3','createTable','',NULL,'3.4.1',NULL,NULL),('13','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',13,'EXECUTED','7:1d8e3ab0ece229c5db96e7cf12f31fe5','createTable','',NULL,'3.4.1',NULL,NULL),('14','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',14,'EXECUTED','7:37d142cdf3c81fc69b2308e6ff2f9686','createTable','',NULL,'3.4.1',NULL,NULL),('15','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',15,'EXECUTED','7:bcd83dc6b7933e62c02b886f39d2c05e','createTable','',NULL,'3.4.1',NULL,NULL),('16','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',16,'EXECUTED','7:ff3ee5cca1df4e30f19527649f636928','createTable (x3)','',NULL,'3.4.1',NULL,NULL),('17','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',17,'EXECUTED','7:3e79cb3780b8092bbeb22a7b130330ca','createTable','',NULL,'3.4.1',NULL,NULL),('18','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',18,'EXECUTED','7:338920651f4e531359511a51b3426822','createTable','',NULL,'3.4.1',NULL,NULL),('19','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',19,'EXECUTED','7:c2c8f8591f73d4d1e468948d7a7cf22d','createTable','',NULL,'3.4.1',NULL,NULL),('20','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',20,'EXECUTED','7:3b933e96286aa435e75c0b17cf2d695d','createTable','',NULL,'3.4.1',NULL,NULL),('21','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',21,'EXECUTED','7:cb6c2156737134f94b475e0cd43b89e3','createTable','',NULL,'3.4.1',NULL,NULL),('22','ulad_bondar','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',22,'EXECUTED','7:3e4f30346ef931871d37c800ef54d66e','createTable','',NULL,'3.4.1',NULL,NULL),('23','alexey_maleyko','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',23,'EXECUTED','7:ea21f25248dc62496e4c8bb9fbf9769b','createTable','',NULL,'3.4.1',NULL,NULL),('24','kirill_zyusko','liquibase/changelog/db.changelog-1.0.xml','2019-05-31 23:32:15',24,'EXECUTED','7:7efc83bf08bc169b4ad01531be76f29d','createTable','',NULL,'3.4.1',NULL,NULL),('68','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:15',25,'EXECUTED','7:3eb935c8032ccdbb7aeb95eb197c8cec','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('70','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:15',26,'EXECUTED','7:f2456df548c0de87570e8cd0b46f5ebf','addForeignKeyConstraint (x2)','',NULL,'3.4.1',NULL,NULL),('71','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:15',27,'EXECUTED','7:aeb7bc897d87472d94c614043d588bac','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('72','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:15',28,'EXECUTED','7:209246006ec5ec6a37ba0b916f8479c6','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('73','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:15',29,'EXECUTED','7:13fe26340748486bbeb7132adad7b897','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('74','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:15',30,'EXECUTED','7:30c0d9d60e3c87d66bc1b4578ba3157d','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('75','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',31,'EXECUTED','7:bb7c607d3e019be578beeb24f034cff8','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('76','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',32,'EXECUTED','7:1598e3a4e07c02dfb104c3cb75c464c3','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('77','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',33,'EXECUTED','7:2d516000815c02cdd7d9166a95cbd2ae','addForeignKeyConstraint (x5)','',NULL,'3.4.1',NULL,NULL),('78','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',34,'EXECUTED','7:b8dc26880d6c0c08db639099a8d3bed0','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('79','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',35,'EXECUTED','7:f1b44ecf975812264ec73f852a17d070','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('80','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',36,'EXECUTED','7:7157b36280cd87ba23d122c6283dd356','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('81','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',37,'EXECUTED','7:c1538ed69c6e0866f7e52f0865377181','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('82','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',38,'EXECUTED','7:691b749811d5ef4df14084327cd7dc1a','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('84','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:16',39,'EXECUTED','7:55a732aa3e6e9909bc2b3605d303427c','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('85','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',40,'EXECUTED','7:75ccd476ea62c5ad7a2ede17323c5973','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('88','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',41,'EXECUTED','7:055e51f9cb9fe3ae38e0695b24b229ac','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('89','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',42,'EXECUTED','7:4c2c1fea3f5cd2f8f54587534bef1d49','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('90','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',43,'EXECUTED','7:efd2250248088a32e730d0a0c9f7bf23','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('91','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',44,'EXECUTED','7:1ddc958aafb61c7da51786571edca4dc','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('92','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',45,'EXECUTED','7:37d2d5c934e8a03a1904e81a5be2243a','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('93','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',46,'EXECUTED','7:73ffbafa6d8e938c1fee391d22b2b657','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('94','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',47,'EXECUTED','7:aedcddd308793c029d7a08f2d63ef1ad','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('95','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',48,'EXECUTED','7:22819cccbdcb00b84a24cf8c1b130e02','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('96','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',49,'EXECUTED','7:dc53154efd98fa92abc7a7d601d694a6','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('96.1','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',50,'EXECUTED','7:66b395433d0426fe619b14f2d82a5580','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('96.2','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',51,'EXECUTED','7:8e00fa82e88668a65b2bce61f020cf4f','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('96.3','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',52,'EXECUTED','7:e6b82d612286348ccb9fdf0c750cf927','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('97','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:17',53,'EXECUTED','7:97ca388b7d038e990e8e49c6091a5404','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('98','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:18',54,'EXECUTED','7:57ef18802746e1211c1beef99f1aa35c','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('99','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:18',55,'EXECUTED','7:1079459a2b9a8b415ceba9d244f59fd9','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('100','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:18',56,'EXECUTED','7:c3056468e2068c15e3c453fa0f94fdc1','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('100a','ulad_bondar','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:18',57,'EXECUTED','7:47c374a9eb97aaa5d26ca64ebff58bc8','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('101','alexey_maleyko','liquibase/changelog/db.changelog-1.1.xml','2019-05-31 23:32:18',58,'EXECUTED','7:ecd6725f7737fd03ea14b01f4391e7ea','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('addColumn','gilimovich','liquibase/changelog/db.changelog-1.2.xml','2019-05-31 23:32:18',59,'EXECUTED','7:32a3845939d7c96e19d79dde07e10920','addColumn (x2)','',NULL,'3.4.1',NULL,NULL),('add constraint','gilimovich','liquibase/changelog/db.changelog-1.2.xml','2019-05-31 23:32:18',60,'EXECUTED','7:2989eda64089364ae0c91e6e7e247ea9','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('act_goods','gilimovich','liquibase/changelog/db.changelog-1.3.xml','2019-05-31 23:32:18',61,'EXECUTED','7:78a88ce55bc620c306a4456dadebbd5c','createTable','',NULL,'3.4.1',NULL,NULL),('act_goods_ibfk_1','gilimovich','liquibase/changelog/db.changelog-1.3.xml','2019-05-31 23:32:18',62,'EXECUTED','7:8c6775b234739c1f92f463f42718e856','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('act_goods_ibfk_2','gilimovich','liquibase/changelog/db.changelog-1.3.xml','2019-05-31 23:32:18',63,'EXECUTED','7:e638a30867afd043bb834a143a9ba580','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('add_column_warehouse','gilimovich','liquibase/changelog/db.changelog-1.4.xml','2019-05-31 23:32:18',64,'EXECUTED','7:21d15641d065cf15cc9dff4bfd728f44','addColumn','',NULL,'3.4.1',NULL,NULL),('invoice_warehouse_ibfk_1','gilimovich','liquibase/changelog/db.changelog-1.4.xml','2019-05-31 23:32:18',65,'EXECUTED','7:e334de8a5a6c56808f986cfa94394e03','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('goods_strategy_ibfk_1','kirill_zyusko','liquibase/changelog/db.changelog-1.4.xml','2019-05-31 23:32:18',66,'EXECUTED','7:1b05dd43a5d5c0012799b9d25f0111a4','addForeignKeyConstraint','',NULL,'3.4.1',NULL,NULL),('warehouse-company','zyusko','liquibase/changelog/db.changelog-warehouse-company.xml','2019-05-31 23:32:18',67,'EXECUTED','7:449c1be3fed19116e8d6e04cab3f276f','insert (x10)','',NULL,'3.4.1',NULL,NULL),('warehouse-company','zyusko','liquibase/changelog/db.changelog-warehouse-company-status.xml','2019-05-31 23:32:18',68,'EXECUTED','7:d01804f0ca263299c74f6583a2188a9b','insert (x5)','',NULL,'3.4.1',NULL,NULL),('wcc1','ulad_bondar','liquibase/changelog/db.changelog-warehouse-customer-company.xml','2019-05-31 23:32:18',69,'EXECUTED','7:8b21f229bb6ae828abe3c66d16d5ec71','insert (x12)','',NULL,'3.4.1',NULL,NULL),('tc1','ulad_bondar','liquibase/changelog/db.changelog-transport-company.xml','2019-05-31 23:32:18',70,'EXECUTED','7:b8c2752d61d01181662a0179d1691596','insert (x11)','',NULL,'3.4.1',NULL,NULL),('warehouse','zyusko','liquibase/changelog/db.changelog-warehouse.xml','2019-05-31 23:32:18',71,'EXECUTED','7:fff3f4aaaa37759eae6d2f2b46326a0d','insert (x15)','',NULL,'3.4.1',NULL,NULL),('user','zyusko','liquibase/changelog/db.changelog-user.xml','2019-05-31 23:32:19',72,'EXECUTED','7:08d3acfca5f1b1bf2c9e8a04b53fd34b','insert (x9)','',NULL,'3.4.1',NULL,NULL),('role','zyusko','liquibase/changelog/db.changelog-role.xml','2019-05-31 23:32:19',73,'EXECUTED','7:ff2a7fd4a7308684d62b302052dd7c8b','insert (x6)','',NULL,'3.4.1',NULL,NULL),('user-role','zyusko','liquibase/changelog/db.changelog-user-role.xml','2019-05-31 23:32:19',74,'EXECUTED','7:4b93a885546df191806a9c977ce23b80','insert (x9)','',NULL,'3.4.1',NULL,NULL),('driver','maleyko','liquibase/changelog/db.changelog-driver.xml','2019-05-31 23:32:19',75,'EXECUTED','7:52fb756a352871351cbe4f7c87597a85','insert (x10)','',NULL,'3.4.1',NULL,NULL),('storage-space-type','maleyko','liquibase/changelog/db.changelog-storage-space-type.xml','2019-05-31 23:32:19',76,'EXECUTED','7:ec7839f6648e7c4fd0502766f68eea5a','insert (x5)','',NULL,'3.4.1',NULL,NULL),('price-list','maleyko','liquibase/changelog/db.changelog-price-list.xml','2019-05-31 23:32:19',77,'EXECUTED','7:3513ab5125216327e73e99c49a943a32','insert (x5)','',NULL,'3.4.1',NULL,NULL),('price-list','maleyko','liquibase/changelog/db.changelog-price-list-1.xml','2019-05-31 23:32:19',78,'EXECUTED','7:1cc1c604f5b4111d7a0774dad0e0fd54','insert (x5)','',NULL,'3.4.1',NULL,NULL),('storage-space','maleyko','liquibase/changelog/db.changelog-storage-space.xml','2019-05-31 23:32:19',79,'EXECUTED','7:2e790d17d1ccbc0990b48336c8c278b6','insert (x33)','',NULL,'3.4.1',NULL,NULL),('price_unit','gilimovich','liquibase/changelog/db.changelog-price-unit.xml','2019-05-31 23:32:19',80,'EXECUTED','7:db4bd3c906fb157df040248eb60ed84e','insert','',NULL,'3.4.1',NULL,NULL),('quantity_unit','gilimovich','liquibase/changelog/db.changelog-quantity-unit.xml','2019-05-31 23:32:19',81,'EXECUTED','7:25867c3c12d3eaa4e769516334b167b9','insert (x7)','',NULL,'3.4.1',NULL,NULL),('weight_unit','gilimovich','liquibase/changelog/db.changelog-weight-unit.xml','2019-05-31 23:32:19',82,'EXECUTED','7:afe85c58604d0879cae00d2efc75fbcc','insert (x2)','',NULL,'3.4.1',NULL,NULL),('invoice','ulad_bondar','liquibase/changelog/db.changelog-invoice.xml','2019-05-31 23:32:19',83,'EXECUTED','7:19788ee57dcaa997520b23397b378474','insert (x10)','',NULL,'3.4.1',NULL,NULL),('invoice-status-name','ulad_bondar','liquibase/changelog/db.changelog-invoice-status-name.xml','2019-05-31 23:32:19',84,'EXECUTED','7:74de72956632555de868f4157bc71d97','insert (x6)','',NULL,'3.4.1',NULL,NULL),('invoice-status','ulad_bondar','liquibase/changelog/db.changelog-invoice-status.xml','2019-05-31 23:32:19',85,'EXECUTED','7:66bc2bbfa3059723891f17bcd7d1f8c9','insert (x10)','',NULL,'3.4.1',NULL,NULL),('act-type','gilimovich','liquibase/changelog/db.changelog-act-type.xml','2019-05-31 23:32:19',86,'EXECUTED','7:49b4aab1eb8a0791db7645cdfc37460e','insert (x4)','',NULL,'3.4.1',NULL,NULL),('act','gilimovich','liquibase/changelog/db.changelog-act.xml','2019-05-31 23:32:19',87,'EXECUTED','7:010563d9e2e04d3998f297897f352fe1','insert (x3)','',NULL,'3.4.1',NULL,NULL),('storage-cell','maleyko','liquibase/changelog/db.changelog-storage-cell.xml','2019-05-31 23:32:19',88,'EXECUTED','7:6cee6ba3b2f40c3ac7fdcf726daa46b5','insert (x265)','',NULL,'3.4.1',NULL,NULL),('goods-status-name','gilimovich','liquibase/changelog/db.changelog-goods-status-name.xml','2019-05-31 23:32:19',89,'EXECUTED','7:162d3b8e656d143c00ff3127ce046c25','insert (x12)','',NULL,'3.4.1',NULL,NULL),('strategy','kirill_zyusko','liquibase/changelog/db.changelog-strategy.xml','2019-05-31 23:32:19',90,'EXECUTED','7:bdc95a7fbb8eb9563c3e610e6d15403d','insert (x7)','',NULL,'3.4.1',NULL,NULL),('goods','gilimovich','liquibase/changelog/db.changelog-goods.xml','2019-05-31 23:32:19',91,'EXECUTED','7:69c839c971aa05ac0773177460e920b8','insert (x34)','',NULL,'3.4.1',NULL,NULL),('goods-status','gilimovich','liquibase/changelog/db.changelog-goods-status.xml','2019-05-31 23:32:19',92,'EXECUTED','7:ed26cd0b8a6d1a4cc2eaa2178f28d274','insert (x57)','',NULL,'3.4.1',NULL,NULL),('act_goods','gilimovich','liquibase/changelog/db.changelog-act-goods.xml','2019-05-31 23:32:19',93,'EXECUTED','7:da4aa2abd678aed649a71712cb392b38','insert (x4)','',NULL,'3.4.1',NULL,NULL),('set_goods_status','gilimovich','liquibase/changelog/db.changelog-goods-2.xml','2019-05-31 23:32:19',94,'EXECUTED','7:3f9d1fa086c8799a1c3969fb89b4627c','sql (x37)','',NULL,'3.4.1',NULL,NULL),('set_invoice_status','ulad_bondar','liquibase/changelog/db.changelog-invoice-update.xml','2019-05-31 23:32:19',95,'EXECUTED','7:9e2e2b844fb470eda9990a395371fee0','sql (x10)','',NULL,'3.4.1',NULL,NULL);
/*!40000 ALTER TABLE `databasechangelog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `databasechangeloglock`
--

DROP TABLE IF EXISTS `databasechangeloglock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `databasechangeloglock` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `databasechangeloglock`
--

LOCK TABLES `databasechangeloglock` WRITE;
/*!40000 ALTER TABLE `databasechangeloglock` DISABLE KEYS */;
INSERT INTO `databasechangeloglock` VALUES (1,_binary '\0',NULL,NULL);
/*!40000 ALTER TABLE `databasechangeloglock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `driver`
--

DROP TABLE IF EXISTS `driver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `driver` (
  `id_driver` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `passport_number` varchar(20) NOT NULL,
  `country_code` varchar(20) NOT NULL,
  `issued_by` varchar(50) NOT NULL,
  `issue_date` date NOT NULL,
  `id_transport_company` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_driver`),
  KEY `driver_ibfk_1` (`id_transport_company`),
  CONSTRAINT `driver_ibfk_1` FOREIGN KEY (`id_transport_company`) REFERENCES `transport_company` (`id_transport_company`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `driver`
--

LOCK TABLES `driver` WRITE;
/*!40000 ALTER TABLE `driver` DISABLE KEYS */;
INSERT INTO `driver` VALUES (1,'Гилимович Александр','MP1234321','111','IssuedByField','2016-09-12',1),(2,'Зюсько Кирилл','MP2234123','222','IssuedByField','2016-12-12',2),(3,'Бондар Владислав','MP123412','333','IssuedByField','2017-01-02',3),(4,'Малейко Алексей','MP123412','444','IssuedByField','2017-02-02',4),(5,'Пушкин Александр','MP542353','555','IssuedByField','2015-01-03',5),(6,'Наполеон Бонапарт','MR979435','666','IssuedByField','2014-09-08',6),(7,'Грозный Иван','MR634563','777','IssuedByField','2012-06-08',7),(8,'Новик Геннадий','MP123412','888','IssuedByField','2017-02-01',8),(9,'Некрасов Антон','MP123412','999','IssuedByField','2017-02-01',9),(10,'Беляев Александр','MP123412','101010','IssuedByField','2017-02-01',10);
/*!40000 ALTER TABLE `driver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods`
--

DROP TABLE IF EXISTS `goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `goods` (
  `id_goods` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `id_storage_type` smallint(5) unsigned NOT NULL,
  `quantity` decimal(10,3) NOT NULL,
  `id_quantity_unit` smallint(5) unsigned NOT NULL,
  `weight` decimal(10,3) NOT NULL,
  `id_weight_unit` smallint(5) unsigned NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `id_price_unit` smallint(5) unsigned NOT NULL,
  `id_incoming_invoice` bigint(20) unsigned NOT NULL,
  `id_outgoing_invoice` bigint(20) unsigned DEFAULT NULL,
  `deleted` date DEFAULT NULL,
  `id_warehouse` bigint(20) unsigned DEFAULT NULL,
  `id_current_status` bigint(20) unsigned DEFAULT NULL,
  `id_registered_status` bigint(20) unsigned DEFAULT NULL,
  `id_moved_out_status` bigint(20) unsigned DEFAULT NULL,
  `id_strategy` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_goods`),
  KEY `goods_ibfk_1` (`id_storage_type`),
  KEY `goods_ibfk_2` (`id_quantity_unit`),
  KEY `goods_ibfk_3` (`id_weight_unit`),
  KEY `goods_ibfk_4` (`id_price_unit`),
  KEY `goods_ibfk_5` (`id_incoming_invoice`),
  KEY `goods_ibfk_6` (`id_outgoing_invoice`),
  KEY `goods_ibfk_7` (`id_warehouse`),
  KEY `goods_ibfk_8` (`id_current_status`),
  KEY `goods_ibfk_9` (`id_registered_status`),
  KEY `goods_ibfk_10` (`id_moved_out_status`),
  KEY `goods_strategy_ibfk_1` (`id_strategy`),
  CONSTRAINT `goods_ibfk_1` FOREIGN KEY (`id_storage_type`) REFERENCES `storage_space_type` (`id_storage_space_type`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_10` FOREIGN KEY (`id_moved_out_status`) REFERENCES `goods_status` (`id_goods_status`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_2` FOREIGN KEY (`id_quantity_unit`) REFERENCES `quantity_unit` (`id_quantity_unit`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_3` FOREIGN KEY (`id_weight_unit`) REFERENCES `weight_unit` (`id_weight_unit`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_4` FOREIGN KEY (`id_price_unit`) REFERENCES `price_unit` (`id_price_unit`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_5` FOREIGN KEY (`id_incoming_invoice`) REFERENCES `invoice` (`id_invoice`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_6` FOREIGN KEY (`id_outgoing_invoice`) REFERENCES `invoice` (`id_invoice`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_7` FOREIGN KEY (`id_warehouse`) REFERENCES `warehouse` (`id_warehouse`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_8` FOREIGN KEY (`id_current_status`) REFERENCES `goods_status` (`id_goods_status`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_ibfk_9` FOREIGN KEY (`id_registered_status`) REFERENCES `goods_status` (`id_goods_status`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_strategy_ibfk_1` FOREIGN KEY (`id_strategy`) REFERENCES `strategy` (`id_strategy`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods`
--

LOCK TABLES `goods` WRITE;
/*!40000 ALTER TABLE `goods` DISABLE KEYS */;
INSERT INTO `goods` VALUES (1,'Молоко',1,500.000,1,500.000,1,1.30,1,1,2,NULL,10,3,1,NULL,NULL),(2,'Хлеб',2,1000.000,7,500.000,1,1.30,1,1,2,NULL,10,6,4,NULL,NULL),(3,'Гвозди',2,500.000,6,1000.000,1,3.33,1,1,2,NULL,10,10,7,NULL,NULL),(4,'Краска',2,25.000,5,2500.000,1,500.00,1,3,4,NULL,10,12,11,NULL,NULL),(5,'Коммутатор',2,100.000,7,0.500,1,500.10,1,3,4,NULL,10,14,13,NULL,NULL),(6,'Уголь',4,100.000,4,10.000,1,13.71,1,5,6,NULL,10,20,15,20,NULL),(7,'Щебень',4,2500.000,4,250.000,1,5123.52,1,5,6,NULL,10,23,21,NULL,NULL),(8,'Посуда',1,100.000,6,1000.000,1,500.00,1,7,8,NULL,10,26,24,NULL,NULL),(9,'Дизель-генератор',2,100.000,7,5478.100,1,5687.57,1,9,10,NULL,10,29,27,NULL,NULL),(10,'Бумага',2,100.000,6,1000.000,1,420.57,1,9,10,NULL,10,32,30,NULL,NULL),(11,'Сырая нефть',2,100.000,5,1000.000,1,420.57,1,9,10,NULL,10,35,33,NULL,NULL),(12,'Газовая плита',2,100.000,7,1000.000,1,420.57,1,9,10,NULL,10,38,36,NULL,NULL),(13,'Обои',2,100.000,6,1000.000,1,420.57,1,9,10,NULL,10,41,39,NULL,NULL),(14,'Сок',4,100.000,6,1000.000,1,420.57,1,9,10,NULL,10,44,42,NULL,NULL),(15,'Металлопрофиль',2,100.000,2,1000.000,1,420.57,1,9,10,NULL,10,47,45,NULL,NULL),(16,'Зерно',4,100.000,4,1000.000,1,420.57,1,9,10,NULL,10,50,48,NULL,NULL),(17,'Ткань',2,100.000,6,1000.000,1,420.57,1,9,10,NULL,10,53,51,NULL,NULL),(18,'Овощи',2,100.000,4,1000.000,1,420.57,1,9,10,NULL,10,57,54,NULL,NULL),(19,'Спиннер',1,20.000,7,500.000,1,6.20,1,1,2,NULL,10,NULL,NULL,NULL,3),(20,'Клавиатура',1,100.000,7,500.000,1,50.40,1,5,6,NULL,10,NULL,NULL,NULL,1),(21,'AXE-FX 2',1,1.000,7,500.000,1,3200.00,1,3,4,NULL,10,NULL,NULL,NULL,2),(22,'Мышь компьютерная DefendeR',1,15.000,7,500.000,1,20.70,1,7,8,NULL,10,NULL,NULL,NULL,1),(23,'Мышь компьютерная DefendeR',1,100.000,7,500.000,1,5.20,1,9,10,NULL,10,NULL,NULL,NULL,3),(24,'Машина стиральная',1,25.000,7,500.000,1,500.00,1,5,6,NULL,10,NULL,NULL,NULL,4),(25,'Теливизор',1,10.000,7,500.000,1,920.00,1,5,6,NULL,10,NULL,NULL,NULL,5),(26,'Веб-камера',1,50.000,7,500.000,1,70.00,1,5,6,NULL,10,NULL,NULL,NULL,1),(27,'BMW X5',1,1.000,7,3.700,2,5000.00,1,5,6,NULL,10,NULL,NULL,NULL,2),(28,'Шуба',1,3.000,7,5.000,1,1600.00,1,5,6,NULL,10,NULL,NULL,NULL,2),(29,'Чехол от телефона',1,25.000,7,0.200,1,15.00,1,7,8,NULL,10,NULL,NULL,NULL,3),(30,'Стиральная машина',1,35.000,7,25.000,1,720.00,1,5,6,NULL,10,NULL,NULL,NULL,5),(32,'Pepsi Cola',1,1250.000,7,2.000,1,3.00,1,7,8,NULL,10,NULL,NULL,NULL,7),(33,'Fanta',1,1000.000,7,2.000,1,2.00,1,7,8,NULL,10,NULL,NULL,NULL,7),(34,'Xiaomi Redmi Pro 2',1,500.000,7,0.300,1,250.00,1,7,8,NULL,10,NULL,NULL,NULL,6),(35,'Xiaomi Redmi Pro 4',1,782.000,7,0.300,1,320.00,1,7,8,NULL,10,NULL,NULL,NULL,6);
/*!40000 ALTER TABLE `goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods_status`
--

DROP TABLE IF EXISTS `goods_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `goods_status` (
  `id_goods_status` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_goods_status_name` smallint(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_user` bigint(20) unsigned NOT NULL,
  `id_goods` bigint(20) unsigned NOT NULL,
  `note` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_goods_status`),
  KEY `goods_status_ibfk_1` (`id_goods_status_name`),
  KEY `goods_status_ibfk_2` (`id_goods`),
  KEY `goods_status_ibfk_3` (`id_user`),
  CONSTRAINT `goods_status_ibfk_1` FOREIGN KEY (`id_goods_status_name`) REFERENCES `goods_status_name` (`id_goods_status_name`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_status_ibfk_2` FOREIGN KEY (`id_goods`) REFERENCES `goods` (`id_goods`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `goods_status_ibfk_3` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_status`
--

LOCK TABLES `goods_status` WRITE;
/*!40000 ALTER TABLE `goods_status` DISABLE KEYS */;
INSERT INTO `goods_status` VALUES (1,1,'2019-05-31 20:32:19',3,1,''),(2,2,'2019-05-31 20:32:19',5,1,''),(3,3,'2019-05-31 20:32:19',4,1,''),(4,1,'2019-05-31 20:32:19',3,2,''),(5,2,'2019-05-31 20:32:19',5,2,''),(6,3,'2019-05-31 20:32:19',4,2,''),(7,1,'2019-05-31 20:32:19',7,3,''),(8,2,'2019-05-31 20:32:19',8,3,''),(9,3,'2019-05-31 20:32:19',9,3,''),(10,6,'2019-05-31 20:32:19',4,3,''),(11,1,'2019-05-31 20:32:19',3,4,''),(12,7,'2019-05-31 20:32:19',5,4,''),(13,1,'2019-05-31 20:32:19',7,5,''),(14,7,'2019-05-31 20:32:19',5,5,''),(15,1,'2019-05-31 20:32:19',3,6,''),(16,2,'2019-05-31 20:32:19',5,6,''),(17,3,'2019-05-31 20:32:19',9,6,''),(18,10,'2019-05-31 20:32:19',9,6,''),(19,11,'2019-05-31 20:32:19',8,6,''),(20,12,'2019-05-31 20:32:19',7,6,''),(21,1,'2019-05-31 20:32:19',7,7,''),(22,2,'2019-05-31 20:32:19',1,7,''),(23,3,'2019-05-31 20:32:19',4,7,''),(24,1,'2019-05-31 20:32:19',7,8,''),(25,2,'2019-05-31 20:32:19',5,8,''),(26,3,'2019-05-31 20:32:19',4,8,''),(27,1,'2019-05-31 20:32:19',3,9,''),(28,2,'2019-05-31 20:32:19',5,9,''),(29,3,'2019-05-31 20:32:19',9,9,''),(30,1,'2019-05-31 20:32:19',3,10,''),(31,2,'2019-05-31 20:32:19',5,10,''),(32,3,'2019-05-31 20:32:19',4,10,''),(33,1,'2019-05-31 20:32:19',7,11,''),(34,2,'2019-05-31 20:32:19',9,11,''),(35,3,'2019-05-31 20:32:19',8,11,''),(36,1,'2019-05-31 20:32:19',7,12,''),(37,2,'2019-05-31 20:32:19',9,12,''),(38,3,'2019-05-31 20:32:19',8,12,''),(39,1,'2019-05-31 20:32:19',3,13,''),(40,2,'2019-05-31 20:32:19',5,13,''),(41,3,'2019-05-31 20:32:19',9,13,''),(42,1,'2019-05-31 20:32:19',3,14,''),(43,2,'2019-05-31 20:32:19',5,14,''),(44,3,'2019-05-31 20:32:19',9,14,''),(45,1,'2019-05-31 20:32:19',7,15,''),(46,2,'2019-05-31 20:32:19',8,15,''),(47,3,'2019-05-31 20:32:19',4,15,''),(48,1,'2019-05-31 20:32:19',7,16,''),(49,2,'2019-05-31 20:32:19',8,16,''),(50,3,'2019-05-31 20:32:19',4,16,''),(51,1,'2019-05-31 20:32:19',7,17,''),(52,2,'2019-05-31 20:32:19',5,17,''),(53,3,'2019-05-31 20:32:19',9,17,''),(54,1,'2019-05-31 20:32:19',3,18,''),(55,2,'2019-05-31 20:32:19',8,18,''),(56,3,'2019-05-31 20:32:19',4,18,''),(57,9,'2019-05-31 20:32:19',4,18,'');
/*!40000 ALTER TABLE `goods_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods_status_name`
--

DROP TABLE IF EXISTS `goods_status_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `goods_status_name` (
  `id_goods_status_name` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id_goods_status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_status_name`
--

LOCK TABLES `goods_status_name` WRITE;
/*!40000 ALTER TABLE `goods_status_name` DISABLE KEYS */;
INSERT INTO `goods_status_name` VALUES (1,'REGISTERED'),(2,'CHECKED'),(3,'STORED'),(4,'LOST_BY_TRANSPORT_COMPANY'),(5,'LOST_BY_WAREHOUSE_COMPANY'),(6,'STOLEN'),(7,'TRANSPORT_COMPANY_MISMATCH'),(8,'SEIZED'),(9,'RECYCLED'),(10,'WITHDRAWN'),(11,'RELEASE_ALLOWED'),(12,'MOVED_OUT');
/*!40000 ALTER TABLE `goods_status_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `invoice` (
  `id_invoice` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` varchar(30) NOT NULL,
  `issue_date` date NOT NULL,
  `id_transport_company` bigint(20) unsigned NOT NULL,
  `id_supplier_company` bigint(20) unsigned DEFAULT NULL,
  `id_receiver_company` bigint(20) unsigned DEFAULT NULL,
  `transport_number` varchar(10) NOT NULL,
  `transport_name` varchar(20) NOT NULL,
  `id_driver` bigint(20) unsigned DEFAULT NULL,
  `goods_entry_count` varchar(64) DEFAULT NULL,
  `batch_description` varchar(100) DEFAULT NULL,
  `id_current_status` bigint(20) unsigned DEFAULT NULL,
  `id_warehouse` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_invoice`),
  UNIQUE KEY `number` (`number`),
  KEY `invoice_ibfk_1` (`id_driver`),
  KEY `invoice_ibfk_2` (`id_transport_company`),
  KEY `invoice_ibfk_4` (`id_supplier_company`),
  KEY `invoice_ibfk_5` (`id_receiver_company`),
  KEY `invoice_ibfk_8` (`id_current_status`),
  KEY `invoice_warehouse_ibfk_1` (`id_warehouse`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `invoice_ibfk_2` FOREIGN KEY (`id_transport_company`) REFERENCES `transport_company` (`id_transport_company`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `invoice_ibfk_4` FOREIGN KEY (`id_supplier_company`) REFERENCES `warehouse_customer_company` (`id_warehouse_customer_company`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `invoice_ibfk_5` FOREIGN KEY (`id_receiver_company`) REFERENCES `warehouse_customer_company` (`id_warehouse_customer_company`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `invoice_ibfk_8` FOREIGN KEY (`id_current_status`) REFERENCES `invoice_status` (`id_invoice_status`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `invoice_warehouse_ibfk_1` FOREIGN KEY (`id_warehouse`) REFERENCES `warehouse` (`id_warehouse`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,'100','2016-01-23',8,1,1,'100','Mercedes',1,'20','some description',1,10),(2,'101','2017-04-05',3,10,1,'200','Volvo',2,'20','',2,2),(3,'102','2016-09-12',2,1,7,'300','Skoda',4,'400','description',3,3),(4,'103','2017-03-03',5,10,9,'400','Mercedes',1,'20','',4,2),(5,'104','2015-02-25',7,5,6,'100','Mercedes',1,'20','some description',5,1),(6,'105','2017-04-05',5,2,8,'600','Chevrolet',2,'20','',6,2),(7,'106','2017-02-15',8,10,4,'700','Volvo',6,'200','',7,2),(8,'107','2017-11-29',1,10,4,'800','Jaguar',1,'200','',8,4),(9,'108','2016-07-30',2,1,2,'100','Mercedes',1,'20','some description',9,5),(10,'109','2017-11-03',9,6,3,'200','Volve',5,'20','some description',10,1);
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_status`
--

DROP TABLE IF EXISTS `invoice_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `invoice_status` (
  `id_invoice_status` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_status_name` smallint(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_user` bigint(20) unsigned NOT NULL,
  `id_invoice` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_invoice_status`),
  KEY `invoice_status_ibfk_1` (`id_status_name`),
  KEY `invoice_status_ibfk_2` (`id_user`),
  KEY `invoice_status_ibfk_3` (`id_invoice`),
  CONSTRAINT `invoice_status_ibfk_1` FOREIGN KEY (`id_status_name`) REFERENCES `invoice_status_name` (`id_invoice_status_name`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `invoice_status_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `invoice_status_ibfk_3` FOREIGN KEY (`id_invoice`) REFERENCES `invoice` (`id_invoice`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_status`
--

LOCK TABLES `invoice_status` WRITE;
/*!40000 ALTER TABLE `invoice_status` DISABLE KEYS */;
INSERT INTO `invoice_status` VALUES (1,1,'2019-05-31 20:32:19',5,1),(2,2,'2019-05-31 20:32:19',4,2),(3,3,'2019-05-31 20:32:19',3,3),(4,4,'2019-05-31 20:32:19',2,4),(5,5,'2019-05-31 20:32:19',1,5),(6,6,'2019-05-31 20:32:19',5,6),(7,2,'2019-05-31 20:32:19',4,7),(8,3,'2019-05-31 20:32:19',3,8),(9,6,'2019-05-31 20:32:19',2,9),(10,5,'2019-05-31 20:32:19',1,10);
/*!40000 ALTER TABLE `invoice_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_status_name`
--

DROP TABLE IF EXISTS `invoice_status_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `invoice_status_name` (
  `id_invoice_status_name` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_invoice_status_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_status_name`
--

LOCK TABLES `invoice_status_name` WRITE;
/*!40000 ALTER TABLE `invoice_status_name` DISABLE KEYS */;
INSERT INTO `invoice_status_name` VALUES (1,'REGISTERED_INCOMING'),(2,'CHECKED'),(3,'COMPLETED'),(4,'RELEASE_ALLOWED'),(5,'MOVED_OUT'),(6,'REGISTERED_OUTGOING');
/*!40000 ALTER TABLE `invoice_status_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `price_list`
--

DROP TABLE IF EXISTS `price_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `price_list` (
  `id_price_list` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_storage_space_type` smallint(5) unsigned NOT NULL,
  `setting_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL,
  `daily_price` decimal(12,2) NOT NULL,
  `id_warehouse_company` bigint(20) unsigned DEFAULT NULL,
  `comment` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id_price_list`),
  KEY `price_list_ibfk_1` (`id_storage_space_type`),
  KEY `price_list_ibfk_2` (`id_warehouse_company`),
  CONSTRAINT `price_list_ibfk_1` FOREIGN KEY (`id_storage_space_type`) REFERENCES `storage_space_type` (`id_storage_space_type`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `price_list_ibfk_2` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `price_list`
--

LOCK TABLES `price_list` WRITE;
/*!40000 ALTER TABLE `price_list` DISABLE KEYS */;
INSERT INTO `price_list` VALUES (1,1,'2017-01-01 21:00:00',NULL,20.25,1,NULL),(2,2,'2017-01-01 21:00:00',NULL,30.30,1,NULL),(3,3,'2017-01-01 21:00:00',NULL,10.00,2,NULL),(4,4,'2017-01-01 21:00:00',NULL,210.00,2,NULL),(5,5,'2017-01-01 21:00:00',NULL,54.00,3,NULL),(6,1,'2017-01-01 21:00:00',NULL,20.25,10,NULL),(7,2,'2017-01-01 21:00:00',NULL,30.30,10,NULL),(8,3,'2017-01-01 21:00:00',NULL,10.00,10,NULL),(9,4,'2017-01-01 21:00:00',NULL,210.00,10,NULL),(10,5,'2017-01-01 21:00:00',NULL,54.00,10,NULL);
/*!40000 ALTER TABLE `price_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `price_unit`
--

DROP TABLE IF EXISTS `price_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `price_unit` (
  `id_price_unit` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id_price_unit`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `price_unit`
--

LOCK TABLES `price_unit` WRITE;
/*!40000 ALTER TABLE `price_unit` DISABLE KEYS */;
INSERT INTO `price_unit` VALUES (1,'руб');
/*!40000 ALTER TABLE `price_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quantity_unit`
--

DROP TABLE IF EXISTS `quantity_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `quantity_unit` (
  `id_quantity_unit` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id_quantity_unit`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quantity_unit`
--

LOCK TABLES `quantity_unit` WRITE;
/*!40000 ALTER TABLE `quantity_unit` DISABLE KEYS */;
INSERT INTO `quantity_unit` VALUES (1,'л'),(2,'м'),(3,'м2'),(4,'м3'),(5,'бочка'),(6,'уп'),(7,'шт');
/*!40000 ALTER TABLE `quantity_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `role` (
  `id_role` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_role`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_SUPERVISOR'),(3,'ROLE_DISPATCHER'),(4,'ROLE_MANAGER'),(5,'ROLE_CONTROLLER'),(6,'ROLE_OWNER');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storage_cell`
--

DROP TABLE IF EXISTS `storage_cell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `storage_cell` (
  `id_storage_cell` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` varchar(10) NOT NULL,
  `status` bit(1) NOT NULL,
  `id_storage_space` bigint(20) unsigned NOT NULL,
  `id_goods` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_storage_cell`),
  KEY `storage_cell_ibfk_1` (`id_storage_space`),
  KEY `storage_cell_ibfk_2` (`id_goods`),
  CONSTRAINT `storage_cell_ibfk_1` FOREIGN KEY (`id_storage_space`) REFERENCES `storage_space` (`id_storage_space`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `storage_cell_ibfk_2` FOREIGN KEY (`id_goods`) REFERENCES `goods` (`id_goods`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=266 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_cell`
--

LOCK TABLES `storage_cell` WRITE;
/*!40000 ALTER TABLE `storage_cell` DISABLE KEYS */;
INSERT INTO `storage_cell` VALUES (1,'1',_binary '',2,NULL),(2,'2',_binary '',2,NULL),(3,'3',_binary '',2,NULL),(4,'4',_binary '',2,NULL),(5,'5',_binary '',2,NULL),(6,'6',_binary '',2,NULL),(7,'7',_binary '',2,NULL),(8,'8',_binary '',2,NULL),(9,'9',_binary '',2,NULL),(10,'10',_binary '',2,NULL),(11,'11',_binary '',1,NULL),(12,'20',_binary '',1,NULL),(13,'12',_binary '',1,NULL),(14,'13',_binary '',1,NULL),(15,'14',_binary '',1,NULL),(16,'15',_binary '',1,NULL),(17,'16',_binary '',1,NULL),(18,'17',_binary '',1,NULL),(19,'18',_binary '',1,NULL),(20,'19',_binary '',1,NULL),(21,'21',_binary '',3,NULL),(22,'22',_binary '',3,NULL),(23,'23',_binary '',3,NULL),(24,'24',_binary '',3,NULL),(25,'25',_binary '',3,NULL),(26,'26',_binary '',3,NULL),(27,'27',_binary '',3,NULL),(28,'28',_binary '',3,NULL),(29,'29',_binary '',3,NULL),(30,'30',_binary '',3,NULL),(31,'31',_binary '',4,NULL),(32,'32',_binary '',4,NULL),(33,'33',_binary '',4,NULL),(34,'34',_binary '',4,NULL),(35,'35',_binary '',4,NULL),(36,'36',_binary '',4,NULL),(37,'37',_binary '',4,NULL),(38,'38',_binary '',4,NULL),(39,'39',_binary '',4,NULL),(40,'40',_binary '',4,NULL),(41,'41',_binary '',5,NULL),(42,'42',_binary '',5,NULL),(43,'43',_binary '',5,NULL),(44,'44',_binary '',5,NULL),(45,'45',_binary '',5,NULL),(46,'46',_binary '',5,NULL),(47,'47',_binary '',5,NULL),(48,'48',_binary '',5,NULL),(49,'49',_binary '',5,NULL),(50,'50',_binary '',5,NULL),(51,'51',_binary '',6,NULL),(52,'52',_binary '',6,NULL),(53,'53',_binary '',6,NULL),(54,'54',_binary '',6,NULL),(55,'55',_binary '',6,NULL),(56,'56',_binary '',6,NULL),(57,'57',_binary '',6,NULL),(58,'58',_binary '',6,NULL),(59,'59',_binary '',6,NULL),(60,'60',_binary '',6,NULL),(61,'61',_binary '',7,NULL),(62,'62',_binary '',7,NULL),(63,'63',_binary '',7,NULL),(64,'64',_binary '',7,NULL),(65,'65',_binary '',7,NULL),(66,'66',_binary '',7,NULL),(67,'67',_binary '',7,NULL),(68,'68',_binary '',7,NULL),(69,'69',_binary '',7,NULL),(70,'70',_binary '',7,NULL),(71,'71',_binary '',8,NULL),(72,'72',_binary '',8,NULL),(73,'73',_binary '',8,NULL),(74,'74',_binary '',8,NULL),(75,'75',_binary '',8,NULL),(76,'76',_binary '',8,NULL),(77,'77',_binary '',8,NULL),(78,'78',_binary '',8,NULL),(79,'79',_binary '',8,NULL),(80,'80',_binary '',8,NULL),(81,'81',_binary '',9,NULL),(82,'82',_binary '',9,NULL),(83,'83',_binary '',9,NULL),(84,'84',_binary '',9,NULL),(85,'85',_binary '',9,NULL),(86,'86',_binary '',9,NULL),(87,'87',_binary '',9,NULL),(88,'88',_binary '',9,NULL),(89,'89',_binary '',9,NULL),(90,'90',_binary '',9,NULL),(91,'91',_binary '',10,NULL),(92,'92',_binary '',10,NULL),(93,'93',_binary '',10,NULL),(94,'94',_binary '',10,NULL),(95,'95',_binary '',10,NULL),(96,'96',_binary '',10,NULL),(97,'97',_binary '',10,NULL),(98,'98',_binary '',10,NULL),(99,'99',_binary '',10,NULL),(100,'100',_binary '',10,NULL),(101,'101',_binary '',10,NULL),(102,'1000',_binary '',11,NULL),(103,'1000',_binary '',12,NULL),(104,'1000',_binary '',13,NULL),(105,'1000',_binary '',14,NULL),(106,'1000',_binary '',15,NULL),(107,'1000',_binary '',16,NULL),(108,'1000',_binary '',17,NULL),(109,'1000',_binary '',18,NULL),(110,'1000',_binary '',19,NULL),(111,'1000',_binary '',20,NULL),(112,'1000',_binary '',21,NULL),(113,'1000',_binary '',22,NULL),(114,'1000',_binary '',23,NULL),(115,'1000',_binary '',24,NULL),(116,'1',_binary '',25,NULL),(117,'2',_binary '',25,NULL),(118,'3',_binary '',25,NULL),(119,'4',_binary '',25,NULL),(120,'5',_binary '',25,NULL),(121,'6',_binary '',25,NULL),(122,'7',_binary '',25,NULL),(123,'8',_binary '',25,NULL),(124,'9',_binary '',25,NULL),(125,'10',_binary '',25,NULL),(126,'11',_binary '',25,NULL),(127,'12',_binary '',25,NULL),(128,'13',_binary '\0',25,NULL),(129,'14',_binary '',25,NULL),(130,'15',_binary '',25,NULL),(131,'16',_binary '\0',25,NULL),(132,'17',_binary '',25,NULL),(133,'18',_binary '',25,NULL),(134,'19',_binary '\0',25,NULL),(135,'20',_binary '',26,NULL),(136,'21',_binary '',26,NULL),(137,'22',_binary '',26,NULL),(138,'23',_binary '',26,NULL),(139,'24',_binary '',26,NULL),(140,'25',_binary '',26,NULL),(141,'26',_binary '',26,NULL),(142,'27',_binary '',26,NULL),(143,'28',_binary '',26,NULL),(144,'29',_binary '',26,NULL),(145,'30',_binary '',26,NULL),(146,'31',_binary '',26,NULL),(147,'32',_binary '',26,NULL),(148,'33',_binary '',26,NULL),(149,'34',_binary '',26,NULL),(150,'35',_binary '',26,NULL),(151,'36',_binary '',26,NULL),(152,'37',_binary '',26,NULL),(153,'38',_binary '',26,NULL),(154,'39',_binary '',26,NULL),(155,'40',_binary '',27,NULL),(156,'41',_binary '',27,NULL),(157,'42',_binary '',27,NULL),(158,'43',_binary '',27,NULL),(159,'44',_binary '',27,NULL),(160,'45',_binary '',27,NULL),(161,'46',_binary '',27,NULL),(162,'47',_binary '',27,NULL),(163,'48',_binary '',27,NULL),(164,'49',_binary '',27,NULL),(165,'50',_binary '',27,NULL),(166,'51',_binary '',27,NULL),(167,'52',_binary '',27,NULL),(168,'53',_binary '',27,NULL),(169,'54',_binary '',27,NULL),(170,'55',_binary '',27,NULL),(171,'56',_binary '',27,NULL),(172,'57',_binary '',27,NULL),(173,'58',_binary '',27,NULL),(174,'59',_binary '',27,NULL),(175,'60',_binary '',27,NULL),(176,'61',_binary '',27,NULL),(177,'62',_binary '',27,NULL),(178,'63',_binary '',27,NULL),(179,'64',_binary '',27,NULL),(180,'70',_binary '',28,NULL),(181,'71',_binary '',28,NULL),(182,'72',_binary '',28,NULL),(183,'73',_binary '',28,NULL),(184,'74',_binary '',28,NULL),(185,'75',_binary '',28,NULL),(186,'76',_binary '',28,NULL),(187,'77',_binary '',28,NULL),(188,'78',_binary '',28,NULL),(189,'79',_binary '',28,NULL),(190,'80',_binary '',28,NULL),(191,'81',_binary '',28,NULL),(192,'82',_binary '',28,NULL),(193,'83',_binary '',28,NULL),(194,'84',_binary '',28,NULL),(195,'85',_binary '',28,NULL),(196,'86',_binary '',28,NULL),(197,'87',_binary '',28,NULL),(198,'88',_binary '',28,NULL),(199,'89',_binary '',28,NULL),(200,'90',_binary '',28,NULL),(201,'91',_binary '',28,NULL),(202,'92',_binary '',28,NULL),(203,'93',_binary '',28,NULL),(204,'94',_binary '',28,NULL),(205,'100',_binary '',29,NULL),(206,'101',_binary '',29,NULL),(207,'102',_binary '',29,NULL),(208,'103',_binary '',29,NULL),(209,'104',_binary '',29,NULL),(210,'105',_binary '',29,NULL),(211,'106',_binary '',29,NULL),(212,'107',_binary '',29,NULL),(213,'108',_binary '',29,NULL),(214,'109',_binary '',29,NULL),(215,'110',_binary '',29,NULL),(216,'111',_binary '',29,NULL),(217,'112',_binary '',29,NULL),(218,'113',_binary '',29,NULL),(219,'114',_binary '',29,NULL),(220,'115',_binary '',29,NULL),(221,'116',_binary '',29,NULL),(222,'117',_binary '',29,NULL),(223,'118',_binary '',29,NULL),(224,'119',_binary '',29,NULL),(225,'120',_binary '',29,NULL),(226,'121',_binary '',29,NULL),(227,'122',_binary '',29,NULL),(228,'123',_binary '',29,NULL),(229,'124',_binary '',29,NULL),(230,'133',_binary '\0',30,NULL),(231,'134',_binary '\0',30,NULL),(232,'135',_binary '\0',30,NULL),(233,'136',_binary '\0',30,NULL),(234,'137',_binary '\0',30,NULL),(235,'138',_binary '\0',30,NULL),(236,'140',_binary '\0',31,NULL),(237,'141',_binary '\0',31,NULL),(238,'142',_binary '\0',31,NULL),(239,'143',_binary '\0',31,NULL),(240,'144',_binary '\0',31,NULL),(241,'145',_binary '\0',31,NULL),(242,'1',_binary '',41,NULL),(243,'2',_binary '',41,NULL),(244,'3',_binary '',41,NULL),(245,'4',_binary '',41,NULL),(246,'5',_binary '',41,NULL),(247,'6',_binary '',41,NULL),(248,'7',_binary '',41,NULL),(249,'8',_binary '',42,NULL),(250,'9',_binary '',42,NULL),(251,'10',_binary '',42,NULL),(252,'11',_binary '',42,NULL),(253,'20',_binary '',42,NULL),(254,'12',_binary '',42,NULL),(255,'13',_binary '',42,NULL),(256,'14',_binary '',42,NULL),(257,'15',_binary '',42,NULL),(258,'16',_binary '',42,NULL),(259,'17',_binary '',42,NULL),(260,'18',_binary '',42,NULL),(261,'19',_binary '',42,NULL),(262,'19',_binary '',42,NULL),(263,'19',_binary '',42,NULL),(264,'19',_binary '',42,NULL),(265,'19',_binary '',42,NULL);
/*!40000 ALTER TABLE `storage_cell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storage_space`
--

DROP TABLE IF EXISTS `storage_space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `storage_space` (
  `id_storage_space` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_storage_space_type` smallint(5) unsigned NOT NULL,
  `status` bit(1) NOT NULL,
  `id_warehouse` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_storage_space`),
  KEY `storage_space_ibfk_1` (`id_storage_space_type`),
  KEY `storage_space_ibfk_2` (`id_warehouse`),
  CONSTRAINT `storage_space_ibfk_1` FOREIGN KEY (`id_storage_space_type`) REFERENCES `storage_space_type` (`id_storage_space_type`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `storage_space_ibfk_2` FOREIGN KEY (`id_warehouse`) REFERENCES `warehouse` (`id_warehouse`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_space`
--

LOCK TABLES `storage_space` WRITE;
/*!40000 ALTER TABLE `storage_space` DISABLE KEYS */;
INSERT INTO `storage_space` VALUES (1,1,_binary '',1),(2,2,_binary '',1),(3,3,_binary '',1),(4,4,_binary '',2),(5,5,_binary '',3),(6,1,_binary '',3),(7,1,_binary '',4),(8,1,_binary '',4),(9,1,_binary '',4),(10,1,_binary '',5),(11,2,_binary '',5),(12,2,_binary '',5),(13,1,_binary '',6),(14,1,_binary '',6),(15,1,_binary '',6),(16,4,_binary '',7),(17,4,_binary '',7),(18,4,_binary '',7),(19,3,_binary '',8),(20,3,_binary '',8),(21,3,_binary '',8),(22,3,_binary '',8),(23,1,_binary '',9),(24,2,_binary '',9),(25,2,_binary '',10),(26,3,_binary '',10),(27,1,_binary '',10),(28,4,_binary '',10),(29,5,_binary '',10),(30,5,_binary '\0',10),(31,1,_binary '\0',10),(41,1,_binary '',11),(42,2,_binary '',12);
/*!40000 ALTER TABLE `storage_space` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storage_space_type`
--

DROP TABLE IF EXISTS `storage_space_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `storage_space_type` (
  `id_storage_space_type` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id_storage_space_type`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_space_type`
--

LOCK TABLES `storage_space_type` WRITE;
/*!40000 ALTER TABLE `storage_space_type` DISABLE KEYS */;
INSERT INTO `storage_space_type` VALUES (1,'Отапливаемое помещение'),(2,'Неотапливаемое помещение'),(3,'Холодильная камера'),(4,'Открытая площадка'),(5,'Камера глубокой заморозки');
/*!40000 ALTER TABLE `storage_space_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `strategy`
--

DROP TABLE IF EXISTS `strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `strategy` (
  `id_strategy` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` text NOT NULL,
  PRIMARY KEY (`id_strategy`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `strategy`
--

LOCK TABLES `strategy` WRITE;
/*!40000 ALTER TABLE `strategy` DISABLE KEYS */;
INSERT INTO `strategy` VALUES (1,'Скидка','Предоставьте скидку на товар, чтобы привлечь внимание клиентов'),(2,'Реклама','Предпочтительной тактикой в реализации товара является усиление его рекламы в обществе.'),(3,'Розыгрыш','Если есть возможность пропиарить магазин, то почему бы просто так не разыграть товар? Магазин получит много внимания со стороны общественности, что в будущем значительно увеличит прибыль.'),(4,'Подарок','Дайте небольшой подарок клиенту, связанный с этим товаром (например, флешка к телефону).'),(5,'Привелегии магазина','Предоставьте скидку на последующие покупки в вашем магазине.'),(6,'Демо использование','Для того, чтобы клиенту понять: необходим ли ему данный товар или нет, можно дать некоторое время бесплатно поиспользовать данный товар. Очень часто, после использования товара пользователь к нему привыкает и непременно его покупает.'),(7,'Игровая акция','Для более быстрой реализации товара магазины часто прибегают к способу рекламной игры, которая заключается в розыгрыше определённых товаров, при покупке данного товара и регистрации игрового кода для принятия участия в игре. Далее случайным образом из всех зарегистрировавшихся номеров выбираются несколько, которые и являются победившими.');
/*!40000 ALTER TABLE `strategy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transport_company`
--

DROP TABLE IF EXISTS `transport_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `transport_company` (
  `id_transport_company` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `is_trusted` bit(1) NOT NULL,
  `login` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `id_warehouse_company` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_transport_company`),
  KEY `transport_company_ibfk_1` (`id_warehouse_company`),
  CONSTRAINT `transport_company_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transport_company`
--

LOCK TABLES `transport_company` WRITE;
/*!40000 ALTER TABLE `transport_company` DISABLE KEYS */;
INSERT INTO `transport_company` VALUES (1,'Перевозчик',_binary '','tr_company_1','p@@ssword',1),(2,'Механик',_binary '','tr_company_2','p@@ssword',2),(3,'Большой куш',_binary '','tr_company_3','p@@ssword',1),(4,'Рок н рольщик',_binary '','tr_company_4','p@@ssword',1),(5,'Шпион',_binary '','tr_company_5','p@@ssword',6),(6,'Elastic',_binary '','tr_company_6','p@@ssword',10),(7,'Деньги',_binary '','tr_company_7','p@@ssword',1),(8,'Два ствола',_binary '','tr_company_8','p@@ssword',2),(9,'Ecolines',_binary '','tr_company_9','p@@ssword',10),(10,'Профессионал',_binary '','tr_company_10','p@@ssword',6),(11,'Ecmalines',_binary '\0','tr_comp','p@@ssword',10);
/*!40000 ALTER TABLE `transport_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `id_user` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) NOT NULL,
  `patronymic` varchar(50) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `street` varchar(50) DEFAULT NULL,
  `house` varchar(10) DEFAULT NULL,
  `apartment` varchar(10) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `id_warehouse_company` bigint(20) unsigned DEFAULT NULL,
  `id_warehouse` bigint(20) unsigned DEFAULT NULL,
  `deleted` date DEFAULT NULL,
  `preset` smallint(5) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  KEY `user_ibfk_1` (`id_warehouse_company`),
  KEY `user_ibfk_2` (`id_warehouse`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_ibfk_2` FOREIGN KEY (`id_warehouse`) REFERENCES `warehouse` (`id_warehouse`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Кирилл','Зюсько','Дмитриевич','1996-10-02','Минск','Коласа','28b','401a','zyusko.kirill@mail.ru','admin','admin',NULL,NULL,NULL,1),(2,'Александр','Гилимович','','1993-08-12','Минск','Космонавтов','34a','64','a.gilimovich1990@gmail.com','supervisor','supervisor',10,10,NULL,1),(3,'Кирик','Зюсько','','1997-12-12','Минск','Независимости','54','19','zyusko.kirik@gmail.com','dispatcher','dispatcher',10,10,NULL,1),(4,'Владислав','Бондарь','','1997-11-21','Минск','Коласа','28b','608a','vlad.bondar@tut.by','manager','manager',10,10,NULL,1),(5,'Александр','Гилимович','','1996-11-21','Минск','Леонида Беды','10','910','anna.pinchuk@onliner.by','controller','controller',10,10,NULL,1),(6,'Алексей','Соловьёв','Александрович','1990-10-21','Минск','Независимости','10','50','','owner','owner',10,NULL,NULL,1),(7,'Валентин','Урбанович','Геннадьевич','1980-03-21','Минск','В.Хоружей','13','33','','dispatcher2','dispatcher2',10,10,NULL,1),(8,'Антон','Никитин','Петрович','1965-05-15','Минск','Плеханова','3','333','','controller2','controller2',10,10,NULL,1),(9,'Ангелина','Яцевич','Ивановна','1980-09-12','Минск','Руссиянова','30','7','stal129@tut.by','manager2','manager2',10,10,NULL,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_role` (
  `id_user_role` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_user` bigint(20) unsigned NOT NULL,
  `id_role` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id_user_role`),
  KEY `user_role_ibfk_1` (`id_user`),
  KEY `user_role_ibfk_2` (`id_role`),
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`id_role`) REFERENCES `role` (`id_role`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,1),(2,2,2),(3,3,3),(4,4,4),(5,5,5),(6,6,6),(7,7,3),(8,8,5),(9,9,4);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `warehouse` (
  `id_warehouse` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `status` bit(1) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `id_warehouse_company` bigint(20) unsigned NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_warehouse`),
  KEY `warehouse_ibfk_1` (`id_warehouse_company`),
  CONSTRAINT `warehouse_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse`
--

LOCK TABLES `warehouse` WRITE;
/*!40000 ALTER TABLE `warehouse` DISABLE KEYS */;
INSERT INTO `warehouse` VALUES (1,'Уникальный',_binary '',48.152,15.135,1,'2018-06-28 19:56:48'),(2,'Идеальный',_binary '',53.8883,27.5444,1,'2018-04-26 19:56:48'),(3,'Элегантный',_binary '',45.7756,1.92999,1,'2018-07-21 19:56:48'),(4,'Запорожье',_binary '',47.974,4.11779,2,'2018-01-26 19:56:48'),(5,'Юрмала',_binary '',50.7196,9.34352,3,'2018-09-13 19:56:48'),(6,'Склад Витька',_binary '',53.2106,12.155,3,'2018-07-09 19:56:48'),(7,'Престижный',_binary '',53.034,15.135,3,'2017-11-18 19:56:48'),(8,'Европа',_binary '',53.034,19.4982,4,'2019-05-31 20:32:18'),(9,'Рига',_binary '',47.5385,18.2671,4,'2018-04-26 19:56:48'),(10,'Подземка',_binary '',45.1119,10.1809,10,'2018-03-03 19:56:48'),(11,'Ангар',_binary '',50.4209,21.7615,10,'2019-02-18 19:56:48'),(12,'Ваш Склад',_binary '',50.1563,25.6926,10,'2018-03-08 19:56:48'),(13,'Складовичок',_binary '',51.1563,27.6926,10,'2018-02-02 19:56:48'),(14,'Подпольный',_binary '',53.1563,24.6926,10,'2017-12-12 19:56:48'),(15,'Нереальный',_binary '',51.1563,23.6926,10,'2018-03-18 19:56:48');
/*!40000 ALTER TABLE `warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_company`
--

DROP TABLE IF EXISTS `warehouse_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `warehouse_company` (
  `id_warehouse_company` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `status` bit(1) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  PRIMARY KEY (`id_warehouse_company`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_company`
--

LOCK TABLES `warehouse_company` WRITE;
/*!40000 ALTER TABLE `warehouse_company` DISABLE KEYS */;
INSERT INTO `warehouse_company` VALUES (1,'Ваш Запас',_binary '',48.152,15.135),(2,'ОАО \'Солнышко\'',_binary '',53.8883,27.5444),(3,'Меново',_binary '',45.7756,1.92999),(4,'Чистый Дом',_binary '',47.974,4.11779),(5,'Умный выбор',_binary '',50.7196,9.34352),(6,'Рога и копыта',_binary '',53.2106,12.155),(7,'Ваш выбор',_binary '',53.034,15.135),(8,'На складе',_binary '',53.034,19.4982),(9,'Простор',_binary '',47.5385,18.2671),(10,'Соседи',_binary '',45.1119,10.1809);
/*!40000 ALTER TABLE `warehouse_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_company_status`
--

DROP TABLE IF EXISTS `warehouse_company_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `warehouse_company_status` (
  `id_warehouse_company_status` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `status` bit(1) NOT NULL,
  `start_date` date NOT NULL,
  `due_date` date DEFAULT NULL,
  `id_warehouse_company` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_warehouse_company_status`),
  KEY `warehouse_company_status_ibfk_1` (`id_warehouse_company`),
  CONSTRAINT `warehouse_company_status_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_company_status`
--

LOCK TABLES `warehouse_company_status` WRITE;
/*!40000 ALTER TABLE `warehouse_company_status` DISABLE KEYS */;
INSERT INTO `warehouse_company_status` VALUES (1,_binary '','2017-01-01','2017-02-01',2),(2,_binary '\0','2017-02-02',NULL,2),(4,_binary '','2016-12-12','2017-01-20',8),(6,_binary '\0','2017-01-21','2017-03-02',3),(7,_binary '','2017-01-20',NULL,1);
/*!40000 ALTER TABLE `warehouse_company_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_customer_company`
--

DROP TABLE IF EXISTS `warehouse_customer_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `warehouse_customer_company` (
  `id_warehouse_customer_company` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `x` float NOT NULL,
  `y` float NOT NULL,
  `id_warehouse_company` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_warehouse_customer_company`),
  KEY `warehouse_customer_company_ibfk_1` (`id_warehouse_company`),
  CONSTRAINT `warehouse_customer_company_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_customer_company`
--

LOCK TABLES `warehouse_customer_company` WRITE;
/*!40000 ALTER TABLE `warehouse_customer_company` DISABLE KEYS */;
INSERT INTO `warehouse_customer_company` VALUES (1,'Dreamland',53.8883,27.5444,1),(2,'Левоправо',48.152,15.135,2),(3,'Smith Bakery',53.8883,27.5444,1),(4,'Аптека',54.8883,23.5444,2),(5,'Крипто',33.8883,26.5444,1),(6,'Школа #2',55.8883,25.5444,10),(7,'Кореньстоп',56.8883,23.5444,1),(8,'SpaceX',58.8883,29.5444,1),(9,'StoneRose',49.8883,29.5444,5),(10,'November',43.8883,29.5444,5),(11,'ОАО Евроопт',43.8883,25.5444,10),(12,'21vek.by',43.8883,29.5444,10);
/*!40000 ALTER TABLE `warehouse_customer_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weight_unit`
--

DROP TABLE IF EXISTS `weight_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `weight_unit` (
  `id_weight_unit` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id_weight_unit`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weight_unit`
--

LOCK TABLES `weight_unit` WRITE;
/*!40000 ALTER TABLE `weight_unit` DISABLE KEYS */;
INSERT INTO `weight_unit` VALUES (1,'кг'),(2,'т');
/*!40000 ALTER TABLE `weight_unit` ENABLE KEYS */;
UNLOCK TABLES;

