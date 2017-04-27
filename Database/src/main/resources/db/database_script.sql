CREATE DATABASE  IF NOT EXISTS `warehouse`;
USE `warehouse`;

CREATE TABLE `warehouse_company`(
  `id_warehouse_company` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) UNIQUE NOT NULL,
  -- статус - активный, приостановленный:
  `status` BIT NOT NULL,

  PRIMARY KEY(`id_warehouse_company`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;

CREATE TABLE `warehouse_customer_company`(
  `id_warehouse_customer_company` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) UNIQUE NOT NULL,

  PRIMARY KEY(`id_warehouse_customer_company`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `transport_company`(
  `id_transport_company` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) UNIQUE NOT NULL,
  `is_trusted` BIT NOT NULL,

  PRIMARY KEY(`id_transport_company`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;

CREATE TABLE `user`(
  `id_user` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50),
  `last_name` VARCHAR(50) NOT NULL,
  `patronymic` VARCHAR(50),
  `date_of_birth` DATE,
  `city` VARCHAR(50),
  `street` VARCHAR(50),
  `house` VARCHAR(10),
  `apartment` VARCHAR(10),
  `email` VARCHAR(30) UNIQUE,
  `login` VARCHAR(20) NOT NULL UNIQUE,
  `password` VARCHAR(20) NOT NULL,
  `id_warehouse_company` BIGINT UNSIGNED NOT NULL,

  PRIMARY KEY(`id_user`),
  FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company`(`id_warehouse_company`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `name`(
  `id_role` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) UNIQUE NOT NULL,

  PRIMARY KEY(`id_role`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;

CREATE TABLE `user_role`(
  `id_user_role` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_user` BIGINT UNSIGNED NOT NULL,
  `id_role` SMALLINT UNSIGNED NOT NULL,

  PRIMARY KEY(`id_user_role`),
  FOREIGN KEY (`id_user`) REFERENCES `user`(`id_user`),
  FOREIGN KEY (`id_role`) REFERENCES `name`(`id_role`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


-- CREATE TABLE `transport_type`(
-- `id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
-- `type_name` VARCHAR(20) UNIQUE NOT NULL,

-- PRIMARY KEY(`id`)
-- ) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


-- CREATE TABLE `transport`(
-- `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
-- `type_id` SMALLINT UNSIGNED NOT NULL,
-- `transport_number` VARCHAR(10) NOT NULL,
-- `transport_company_id` BIGINT UNSIGNED NOT NULL,

-- PRIMARY KEY(`id`),
-- FOREIGN KEY (`type_id`) REFERENCES `transport_type`(`id`),
-- FOREIGN KEY (`transport_company_id`) REFERENCES `transport_company`(`id`)
-- ) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `driver`(
  `id_driver` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(100) NOT NULL,
  `passport_number` VARCHAR(20) NOT NULL UNIQUE,
  `country_code` VARCHAR(20) NOT NULL,
  `issued_by` VARCHAR(50) NOT NULL,
  `issue_date` DATE NOT NULL,
  `id_transport_company` BIGINT UNSIGNED NOT NULL,

  PRIMARY KEY(`id_driver`),
  FOREIGN KEY (`id_transport_company`) REFERENCES `transport_company`(`id_transport_company`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `warehouse`(
  `id_warehouse` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) UNIQUE NOT NULL,
  `id_warehouse_company` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY(`id_warehouse`),
  FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company`(`id_warehouse_company`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `storage_space_type`(
  `id_storage_space_type` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) UNIQUE NOT NULL,

  PRIMARY KEY(`id_storage_space_type`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;

CREATE TABLE `price_list`(
  `id_price_list` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_storage_space_type` SMALLINT UNSIGNED NOT NULL,
  `setting_time` TIMESTAMP NOT NULL,
  `daily_price` DECIMAL(12,2) NOT NULL,

  PRIMARY KEY(`id_price_list`),
  FOREIGN KEY (`id_storage_space_type`) REFERENCES `storage_space_type`(`id_storage_space_type`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `storage_space`(
  `id_storage_space` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_storage_space_type` SMALLINT UNSIGNED NOT NULL,
  `id_warehouse`  BIGINT UNSIGNED NOT NULL,

  PRIMARY KEY(`id_storage_space`),
  FOREIGN KEY (`id_storage_space_type`) REFERENCES `storage_space_type`(`id_storage_space_type`),
  FOREIGN KEY (`id_warehouse`) REFERENCES `warehouse`(`id_warehouse`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `unit`(
  `id_unit` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) UNIQUE NOT NULL,

  PRIMARY KEY(`id_unit`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `invoice`(
  `id_invoice` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `number` VARCHAR(30) NOT NULL UNIQUE,
  `issue_date` DATE NOT NULL,
  `id_transport_company` BIGINT UNSIGNED NOT NULL,
  `id_warehouse_company` BIGINT UNSIGNED NOT NULL,
  `id_supplier_company` BIGINT UNSIGNED,
  `id_receiver_company` BIGINT UNSIGNED,
  `transport_number` VARCHAR(10) NOT NULL,
  `transport_name` VARCHAR(20) NOT NULL,
  `id_driver` BIGINT UNSIGNED,
  `goods_quantity` DECIMAL(10,3),
  `id_goods_quantity_unit` SMALLINT UNSIGNED,
  `goods_entry_count` INT,
  `id_goods_entry_count_unit` SMALLINT UNSIGNED,
  `batch_description` VARCHAR(100),

  PRIMARY KEY(`id_invoice`),
  FOREIGN KEY (`id_driver`) REFERENCES `driver`(`id_driver`),
  FOREIGN KEY (`id_transport_company`) REFERENCES `transport_company`(`id_transport_company`),
  FOREIGN KEY (`id_warehouse_company`) REFERENCES `warehouse_company`(`id_warehouse_company`),
  FOREIGN KEY (`id_supplier_company`) REFERENCES `warehouse_customer_company`(`id_warehouse_customer_company`),
  FOREIGN KEY (`id_receiver_company`) REFERENCES `warehouse_customer_company`(`id_warehouse_customer_company`),
  FOREIGN KEY (`id_goods_quantity_unit`) REFERENCES `unit`(`id_unit`),
  FOREIGN KEY (`id_goods_entry_count_unit`) REFERENCES `unit`(`id_unit`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `invoice_status_name`(
  `id_invoice_status_name` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) UNIQUE NOT NULL,

  PRIMARY KEY(`id_invoice_status_name`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `invoice_status`(
  `id_invoice_status` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_status_name` SMALLINT UNSIGNED NOT NULL,
  `date` TIMESTAMP NOT NULL,
  `id_user` BIGINT UNSIGNED NOT NULL,
  `id_invoice` BIGINT UNSIGNED NOT NULL,

  PRIMARY KEY(`id_invoice_status`),
  FOREIGN KEY (`id_status_name`) REFERENCES `invoice_status_name`(`id_invoice_status_name`),
  FOREIGN KEY (`id_user`) REFERENCES `user`(`id_user`),
  FOREIGN KEY (`id_invoice`) REFERENCES `invoice`(`id_invoice`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `goods`(
  `id_goods` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) UNIQUE NOT NULL,
  `id_storage_type` SMALLINT UNSIGNED NOT NULL,
  `quantity` DECIMAL(10,3) NOT NULL,
  `id_quantity_unit` SMALLINT UNSIGNED NOT NULL,
  `weight` DECIMAL(10,3) NOT NULL,
  `id_weight_unit` SMALLINT UNSIGNED NOT NULL,
  `price` DECIMAL(12,2) NOT NULL,
  `id_price_unit` SMALLINT UNSIGNED NOT NULL,
  `id_incoming_invoice` BIGINT UNSIGNED NOT NULL,
  `id_outgoing_invoice` BIGINT UNSIGNED,

  PRIMARY KEY(`id_goods`),
  FOREIGN KEY (`id_storage_type`) REFERENCES `storage_space_type`(`id_storage_space_type`),
  FOREIGN KEY (`id_quantity_unit`) REFERENCES `unit`(`id_unit`),
  FOREIGN KEY (`id_weight_unit`) REFERENCES `unit`(`id_unit`),
  FOREIGN KEY (`id_price_unit`) REFERENCES `unit`(`id_unit`),
  FOREIGN KEY (`id_incoming_invoice`) REFERENCES `invoice`(`id_invoice`),
  FOREIGN KEY (`id_outgoing_invoice`) REFERENCES `invoice`(`id_invoice`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;

CREATE TABLE `act_type`(
  `id_act_type` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,

  PRIMARY KEY(`id_act_type`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;

CREATE TABLE `act`(
  `id_act` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `date` TIMESTAMP NOT NULL,
  `id_act_type` SMALLINT UNSIGNED NOT NULL,
  `id_user` BIGINT UNSIGNED NOT NULL,
  `id_goods` BIGINT UNSIGNED NOT NULL,

  PRIMARY KEY(`id_act`),
  FOREIGN KEY (`id_user`) REFERENCES `user`(`id_user`),
  FOREIGN KEY (`id_goods`) REFERENCES `goods`(`id_goods`),
  FOREIGN KEY (`id_act_type`) REFERENCES `act_type`(`id_act_type`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;



CREATE TABLE `storage_cell`(
  `id_storage_cell` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `number` VARCHAR(10) NOT NULL,
  `id_storage_space` BIGINT UNSIGNED NOT NULL,
  `id_goods` BIGINT UNSIGNED,
  -- `is_occupied`  BIT NOT NULL,

  PRIMARY KEY(`id_storage_cell`),
  FOREIGN KEY (`id_storage_space`) REFERENCES `storage_space`(`id_storage_space`),
  FOREIGN KEY (`id_goods`) REFERENCES `goods`(`id_goods`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `goods_status_name`(
  `id_goods_status_name` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) UNIQUE NOT NULL,

  PRIMARY KEY(`id_goods_status_name`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;


CREATE TABLE `goods_status`(
  `id_goods_status` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_goods_status_name` SMALLINT UNSIGNED NOT NULL,
  `date` TIMESTAMP NOT NULL,
  `id_user`  BIGINT UNSIGNED NOT NULL,
  `id_goods` BIGINT UNSIGNED NOT NULL,
  `note` VARCHAR(100) NOT NULL,

  PRIMARY KEY(`id_goods_status`),
  FOREIGN KEY (`id_goods_status_name`) REFERENCES `goods_status_name`(`id_goods_status_name`),
  FOREIGN KEY (`id_goods`) REFERENCES `goods`(`id_goods`),
  FOREIGN KEY (`id_user`) REFERENCES `user`(`id_user`)
) DEFAULT CHARSET utf8mb4 ENGINE InnoDB;





