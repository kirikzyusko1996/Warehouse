CREATE DATABASE  IF NOT EXISTS `warehouse` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `warehouse`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: localhost    Database: warehouse
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
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
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `act` (
  `id_act` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_act_type` smallint(5) unsigned NOT NULL,
  `id_user` bigint(20) unsigned NOT NULL,
  `id_goods` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_act`),
  KEY `id_user` (`id_user`),
  KEY `id_goods` (`id_goods`),
  KEY `id_act_type` (`id_act_type`),
  CONSTRAINT `act_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`),
  CONSTRAINT `act_ibfk_2` FOREIGN KEY (`id_goods`) REFERENCES `goods` (`id_goods`),
  CONSTRAINT `act_ibfk_3` FOREIGN KEY (`id_act_type`) REFERENCES `act_type` (`id_act_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `act`
--

LOCK TABLES `act` WRITE;
/*!40000 ALTER TABLE `act` DISABLE KEYS */;
/*!40000 ALTER TABLE `act` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `act_type`
--

DROP TABLE IF EXISTS `act_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `act_type` (
  `id_act_type` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_act_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `act_type`
--

LOCK TABLES `act_type` WRITE;
/*!40000 ALTER TABLE `act_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `act_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `driver`
--

DROP TABLE IF EXISTS `driver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `driver` (
  `id_driver` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `passport_number` varchar(20) NOT NULL,
  `country_code` varchar(20) NOT NULL,
  `issued_by` varchar(50) NOT NULL,
  `issue_date` date NOT NULL,
  `id_transport_company` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_driver`),
  UNIQUE KEY `passport_number` (`passport_number`),
  KEY `id_transport_company` (`id_transport_company`),
  CONSTRAINT `driver_ibfk_1` FOREIGN KEY (`id_transport_company`) REFERENCES `transport_company` (`id_transport_company`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `driver`
--

LOCK TABLES `driver` WRITE;
/*!40000 ALTER TABLE `driver` DISABLE KEYS */;
/*!40000 ALTER TABLE `driver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goodsList`
--

DROP TABLE IF EXISTS `goodsList`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goods` (
  `id_goods` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `id_storage_type` smallint(5) unsigned DEFAULT NULL,
  `quantity` decimal(10,3) NOT NULL,
  `id_quantity_unit` smallint(5) unsigned DEFAULT NULL,
  `weight` decimal(10,3) NOT NULL,
  `id_weight_unit` smallint(5) unsigned DEFAULT NULL,
  `price` decimal(12,2) NOT NULL,
  `id_price_unit` smallint(5) unsigned DEFAULT NULL,
  `id_incoming_invoice` bigint(20) unsigned DEFAULT NULL,
  `id_outgoing_invoice` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_goods`),
  UNIQUE KEY `name` (`name`),
  KEY `id_storage_type` (`id_storage_type`),
  KEY `id_quantity_unit` (`id_quantity_unit`),
  KEY `id_weight_unit` (`id_weight_unit`),
  KEY `id_price_unit` (`id_price_unit`),
  KEY `id_incoming_invoice` (`id_incoming_invoice`),
  KEY `id_outgoing_invoice` (`id_outgoing_invoice`),
  CONSTRAINT `goods_ibfk_1` FOREIGN KEY (`id_storage_type`) REFERENCES `storage_space_type` (`id_storage_space_type`),
  CONSTRAINT `goods_ibfk_2` FOREIGN KEY (`id_quantity_unit`) REFERENCES `unit` (`id_unit`),
  CONSTRAINT `goods_ibfk_3` FOREIGN KEY (`id_weight_unit`) REFERENCES `unit` (`id_unit`),
  CONSTRAINT `goods_ibfk_4` FOREIGN KEY (`id_price_unit`) REFERENCES `unit` (`id_unit`),
  CONSTRAINT `goods_ibfk_5` FOREIGN KEY (`id_incoming_invoice`) REFERENCES `invoice` (`id_invoice`),
  CONSTRAINT `goods_ibfk_6` FOREIGN KEY (`id_outgoing_invoice`) REFERENCES `invoice` (`id_invoice`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goodsList`
--

LOCK TABLES `goodsList` WRITE;
/*!40000 ALTER TABLE `goodsList` DISABLE KEYS */;
/*!40000 ALTER TABLE `goodsList` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods_status`
--

DROP TABLE IF EXISTS `goods_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goods_status` (
  `id_goods_status` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_goods_status_name` smallint(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_user` bigint(20) unsigned NOT NULL,
  `id_goods` bigint(20) unsigned NOT NULL,
  `note` varchar(100) NOT NULL,
  PRIMARY KEY (`id_goods_status`),
  KEY `id_goods_status_name` (`id_goods_status_name`),
  KEY `id_goods` (`id_goods`),
  KEY `id_user` (`id_user`),
  CONSTRAINT `goods_status_ibfk_1` FOREIGN KEY (`id_goods_status_name`) REFERENCES `goods_status_name` (`id_goods_status_name`),
  CONSTRAINT `goods_status_ibfk_2` FOREIGN KEY (`id_goods`) REFERENCES `goods` (`id_goods`),
  CONSTRAINT `goods_status_ibfk_3` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_status`
--

LOCK TABLES `goods_status` WRITE;
/*!40000 ALTER TABLE `goods_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `goods_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods_status_name`
--

DROP TABLE IF EXISTS `goods_status_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `goods_status_name` (
  `id_goods_status_name` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_goods_status_name`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods_status_name`
--

LOCK TABLES `goods_status_name` WRITE;
/*!40000 ALTER TABLE `goods_status_name` DISABLE KEYS */;
/*!40000 ALTER TABLE `goods_status_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice` (
  `id_invoice` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` varchar(30) NOT NULL,
  `issue_date` date NOT NULL,
  `id_transport_company` bigint(20) unsigned NOT NULL,
  `id_warehouse_company` bigint(20) unsigned NOT NULL,
  `id_supplier_company` bigint(20) unsigned DEFAULT NULL,
  `id_receiver_company` bigint(20) unsigned DEFAULT NULL,
  `transport_number` varchar(10) NOT NULL,
  `transport_name` varchar(20) NOT NULL,
  `id_driver` bigint(20) unsigned DEFAULT NULL,
  `goods_quantity` decimal(10,3) DEFAULT NULL,
  `id_goods_quantity_unit` smallint(5) unsigned DEFAULT NULL,
  `goods_entry_count` int(11) DEFAULT NULL,
  `id_goods_entry_count_unit` smallint(5) unsigned DEFAULT NULL,
  `batch_description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_invoice`),
  UNIQUE KEY `number` (`number`),
  KEY `id_driver` (`id_driver`),
  KEY `id_transport_company` (`id_transport_company`),
  KEY `id_warehouse_company` (`id_warehouse_company`),
  KEY `id_supplier_company` (`id_supplier_company`),
  KEY `id_receiver_company` (`id_receiver_company`),
  KEY `id_goods_quantity_unit` (`id_goods_quantity_unit`),
  KEY `id_goods_entry_count_unit` (`id_goods_entry_count_unit`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`),
  CONSTRAINT `invoice_ibfk_2` FOREIGN KEY (`id_transport_company`) REFERENCES `transport_company` (`id_transport_company`),
  CONSTRAINT `invoice_ibfk_3` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`),
  CONSTRAINT `invoice_ibfk_4` FOREIGN KEY (`id_supplier_company`) REFERENCES `warehouse_customer_company` (`id_warehouse_customer_company`),
  CONSTRAINT `invoice_ibfk_5` FOREIGN KEY (`id_receiver_company`) REFERENCES `warehouse_customer_company` (`id_warehouse_customer_company`),
  CONSTRAINT `invoice_ibfk_6` FOREIGN KEY (`id_goods_quantity_unit`) REFERENCES `unit` (`id_unit`),
  CONSTRAINT `invoice_ibfk_7` FOREIGN KEY (`id_goods_entry_count_unit`) REFERENCES `unit` (`id_unit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_status`
--

DROP TABLE IF EXISTS `invoice_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_status` (
  `id_invoice_status` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_status_name` smallint(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_user` bigint(20) unsigned NOT NULL,
  `id_invoice` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_invoice_status`),
  KEY `id_status_name` (`id_status_name`),
  KEY `id_user` (`id_user`),
  KEY `id_invoice` (`id_invoice`),
  CONSTRAINT `invoice_status_ibfk_1` FOREIGN KEY (`id_status_name`) REFERENCES `invoice_status_name` (`id_invoice_status_name`),
  CONSTRAINT `invoice_status_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`),
  CONSTRAINT `invoice_status_ibfk_3` FOREIGN KEY (`id_invoice`) REFERENCES `invoice` (`id_invoice`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_status`
--

LOCK TABLES `invoice_status` WRITE;
/*!40000 ALTER TABLE `invoice_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_status_name`
--

DROP TABLE IF EXISTS `invoice_status_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invoice_status_name` (
  `id_invoice_status_name` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_invoice_status_name`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_status_name`
--

LOCK TABLES `invoice_status_name` WRITE;
/*!40000 ALTER TABLE `invoice_status_name` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice_status_name` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `price_list`
--

DROP TABLE IF EXISTS `price_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `price_list` (
  `id_price_list` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_storage_space_type` smallint(5) unsigned NOT NULL,
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `daily_price` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_price_list`),
  KEY `id_storage_space_type` (`id_storage_space_type`),
  CONSTRAINT `price_list_ibfk_1` FOREIGN KEY (`id_storage_space_type`) REFERENCES `storage_space_type` (`id_storage_space_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `price_list`
--

LOCK TABLES `price_list` WRITE;
/*!40000 ALTER TABLE `price_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `price_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id_role` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_role`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ROLE_SUPERVISOR');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storage_cell`
--

DROP TABLE IF EXISTS `storage_cell`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_cell` (
  `id_storage_cell` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `number` varchar(10) NOT NULL,
  `id_storage_space` bigint(20) unsigned NOT NULL,
  `id_goods` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_storage_cell`),
  KEY `id_storage_space` (`id_storage_space`),
  KEY `id_goods` (`id_goods`),
  CONSTRAINT `storage_cell_ibfk_1` FOREIGN KEY (`id_storage_space`) REFERENCES `storage_space` (`id_storage_space`),
  CONSTRAINT `storage_cell_ibfk_2` FOREIGN KEY (`id_goods`) REFERENCES `goods` (`id_goods`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_cell`
--

LOCK TABLES `storage_cell` WRITE;
/*!40000 ALTER TABLE `storage_cell` DISABLE KEYS */;
/*!40000 ALTER TABLE `storage_cell` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storage_space`
--

DROP TABLE IF EXISTS `storage_space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_space` (
  `id_storage_space` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_storage_space_type` smallint(5) unsigned NOT NULL,
  `id_warehouse` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_storage_space`),
  KEY `id_storage_space_type` (`id_storage_space_type`),
  KEY `id_warehouse` (`id_warehouse`),
  CONSTRAINT `storage_space_ibfk_1` FOREIGN KEY (`id_storage_space_type`) REFERENCES `storage_space_type` (`id_storage_space_type`),
  CONSTRAINT `storage_space_ibfk_2` FOREIGN KEY (`id_warehouse`) REFERENCES `warehouse` (`id_warehouse`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_space`
--

LOCK TABLES `storage_space` WRITE;
/*!40000 ALTER TABLE `storage_space` DISABLE KEYS */;
/*!40000 ALTER TABLE `storage_space` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `storage_space_type`
--

DROP TABLE IF EXISTS `storage_space_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_space_type` (
  `id_storage_space_type` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`id_storage_space_type`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_space_type`
--

LOCK TABLES `storage_space_type` WRITE;
/*!40000 ALTER TABLE `storage_space_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `storage_space_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transport_company`
--

DROP TABLE IF EXISTS `transport_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transport_company` (
  `id_transport_company` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `is_trusted` bit(1) NOT NULL,
  PRIMARY KEY (`id_transport_company`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transport_company`
--

LOCK TABLES `transport_company` WRITE;
/*!40000 ALTER TABLE `transport_company` DISABLE KEYS */;
/*!40000 ALTER TABLE `transport_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `unit`
--

DROP TABLE IF EXISTS `unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unit` (
  `id_unit` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id_unit`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unit`
--

LOCK TABLES `unit` WRITE;
/*!40000 ALTER TABLE `unit` DISABLE KEYS */;
/*!40000 ALTER TABLE `unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `id_company` bigint(20) unsigned DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `login` varchar(20) NOT NULL,
  `password` varchar(50) NOT NULL,
  `id_warehouse_company` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `login` (`login`),
  UNIQUE KEY `email` (`email`),
  KEY `id_warehouse_company` (`id_warehouse_company`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,NULL,'Гилимович',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'1','1',1),(2,NULL,'Зюсько',NULL,NULL,NULL,NULL,NULL,NULL,1,NULL,'2','2',1),(3,NULL,'Малейко',NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'3','3',2),(4,NULL,'Юондарь',NULL,NULL,NULL,NULL,NULL,NULL,2,NULL,'4','4',NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `id_user_role` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `id_user` bigint(20) unsigned NOT NULL,
  `id_role` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id_user_role`),
  KEY `id_user` (`id_user`),
  KEY `id_role` (`id_role`),
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`),
  CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`id_role`) REFERENCES `role` (`id_role`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,1);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse` (
  `id_warehouse` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `id_warehouse_company` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_warehouse`),
  UNIQUE KEY `name` (`name`),
  KEY `id_warehouse_company` (`id_warehouse_company`),
  CONSTRAINT `warehouse_ibfk_1` FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company` (`id_warehouse_company`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse`
--

LOCK TABLES `warehouse` WRITE;
/*!40000 ALTER TABLE `warehouse` DISABLE KEYS */;
/*!40000 ALTER TABLE `warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_company`
--

DROP TABLE IF EXISTS `warehouse_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse_company` (
  `id_warehouse_company` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `status` bit(1) NOT NULL,
  PRIMARY KEY (`id_warehouse_company`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_company`
--

LOCK TABLES `warehouse_company` WRITE;
/*!40000 ALTER TABLE `warehouse_company` DISABLE KEYS */;
INSERT INTO `warehouse_company` VALUES (1,'itechart_1',''),(2,'itechart_2','');
/*!40000 ALTER TABLE `warehouse_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_customer_company`
--

DROP TABLE IF EXISTS `warehouse_customer_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `warehouse_customer_company` (
  `id_warehouse_customer_company` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id_warehouse_customer_company`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_customer_company`
--

LOCK TABLES `warehouse_customer_company` WRITE;
/*!40000 ALTER TABLE `warehouse_customer_company` DISABLE KEYS */;
/*!40000 ALTER TABLE `warehouse_customer_company` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-26 15:12:15
