CREATE TABLE `transaction_log` (


`id` VARCHAR(255) NOT NULL  ,


`type` INT(11) NOT NULL  ,


`amount` DECIMAL(21,6) NULL  ,


`order_id` VARCHAR(255) NULL,


`method` INT(5) NULL  ,


`create_time` TIME NULL,


`memo` LONGTEXT NULL


);





CREATE TABLE `remittance_registration` (


`id` VARCHAR(255) NOT NULL,


`req_user` VARCHAR(255) NULL  ,


`remitter` VARCHAR(255) NULL  ,


`auditor` VARCHAR(255) NULL ,


`remitter_type` INT NULL ,


`req_time` TIME NULL ,


`audit_time` TIME NULL ,


`remitter_time` TIME NULL  ,


`amount` DECIMAL(65,0) NULL,


`audit_state` INT(255) NULL  ,


`memo` VARCHAR(255) NULL  ,


`account` INT NULL  ,


`bank` VARCHAR(255) NULL  ,


`type` INT(5) NULL  


);





CREATE TABLE `product_country` (


`id` VARCHAR(255) NOT NULL,


`product_id` VARCHAR(255) NULL,


`country_id` VARCHAR(255) NULL,


`isMarketable` LONGBLOB NULL,


`price` DECIMAL NULL  ,


`alert_count` INT NULL  ,


`create_by` VARCHAR(255) NULL ,


`create_time` TIME NULL,


`auditor` VARCHAR(255) NULL,


`audit_time` TIME NULL ,


PRIMARY KEY (`id`) 


);





CREATE TABLE `stocklog` (


`id` VARCHAR(255) NOT NULL,


`createdDate` VARCHAR(255) NOT NULL,


`lastModifiedDate` VARCHAR(255) NOT NULL,


`version` VARCHAR(255) NOT NULL,


`inQuantity` INT(11) NOT NULL,


`memo` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`outQuantity` INT(11) NOT NULL,


`stock` INT(11) NOT NULL,


`type` INT(11) NOT NULL,


`sku_id` VARCHAR(255) NOT NULL,


`warehouse_id` VARCHAR(255) NOT NULL,


PRIMARY KEY (`id`) ,


INDEX `ind_StockLog_sku_id` (`sku_id`)


)


ENGINE=INNODB


DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;





CREATE TABLE `warehouse` (


`id` VARCHAR(255) NOT NULL,


`country_id` VARCHAR(255) NULL,


`name` LONGTEXT NULL,


`create_by` VARCHAR(255) NULL,


`create_time` TIME NULL  ,


`code` VARCHAR(255) NULL  ,


PRIMARY KEY (`id`) 


);





CREATE TABLE `translation` (


`id` VARCHAR(255) NOT NULL,


`text_ch` VARCHAR(255) NOT NULL,


`text_en` VARCHAR(255) NULL,


PRIMARY KEY (`text_ch`, `id`) 


);





CREATE TABLE `product_en` (


`id` VARCHAR(255) NOT NULL,


`createdDate` VARCHAR(255) NOT NULL,


`lastModifiedDate` VARCHAR(255) NOT NULL,


`version` VARCHAR(255) NOT NULL,


`attributeValue0` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue1` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue10` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue11` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue12` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue13` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue14` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue15` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue16` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue17` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue18` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue19` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue2` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue3` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue4` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue5` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue6` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue7` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue8` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`attributeValue9` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`caption` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`cost` DECIMAL(21,6) NULL DEFAULT NULL,


`hits` VARCHAR(255) NOT NULL,


`image` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`introduction` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`isDelivery` LONGBLOB NOT NULL,


`isList` LONGBLOB NOT NULL,


`isMarketable` LONGBLOB NOT NULL,


`isTop` LONGBLOB NOT NULL,


`keyword` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`marketPrice` DECIMAL(21,6) NOT NULL,


`memo` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`monthHits` VARCHAR(255) NOT NULL,


`monthHitsDate` VARCHAR(255) NOT NULL,


`monthSales` VARCHAR(255) NOT NULL,


`monthSalesDate` VARCHAR(255) NOT NULL,


`name` VARCHAR(255) CHARACTER SET utf8 NOT NULL,


`parameterValues` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`price` DECIMAL(21,6) NOT NULL,


`productImages` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`sales` VARCHAR(255) NOT NULL,


`score` FLOAT NOT NULL,


`scoreCount` VARCHAR(255) NOT NULL,


`seoDescription` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`seoKeywords` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`seoTitle` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`sn` VARCHAR(255) CHARACTER SET utf8 NOT NULL,


`specificationItems` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`totalScore` VARCHAR(255) NOT NULL,


`type` INT(11) NOT NULL,


`unit` VARCHAR(255) CHARACTER SET utf8 NULL DEFAULT NULL,


`weekHits` VARCHAR(255) NOT NULL,


`weekHitsDate` VARCHAR(255) NOT NULL,


`weekSales` VARCHAR(255) NOT NULL,


`weekSalesDate` VARCHAR(255) NOT NULL,


`weight` INT(11) NULL DEFAULT NULL,


`brand_id` VARCHAR(255) NULL DEFAULT NULL,


`productCategory_id` VARCHAR(255) NOT NULL,


PRIMARY KEY (`id`) ,


UNIQUE INDEX `uni_Product_sn` (`sn`),


INDEX `ind_Product_brand_id` (`brand_id`),


INDEX `ind_Product_productCategory_id` (`productCategory_id`)


)


ENGINE=INNODB


DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;





CREATE TABLE `productcategory_en` (


`id` BIGINT(20) NOT NULL,


`createdDate` DATETIME NOT NULL,


`lastModifiedDate` DATETIME NOT NULL,


`version` BIGINT(20) NOT NULL,


`orders` INT(11) NULL DEFAULT NULL,


`grade` INT(11) NOT NULL,


`name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,


`seoDescription` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,


`seoKeywords` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,


`seoTitle` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,


`treePath` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,


`parent_id` BIGINT(20) NULL DEFAULT NULL,


PRIMARY KEY (`id`) ,


INDEX `ind_ProductCategory_parent_id` (`parent_id`)


)


ENGINE=INNODB


DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;





CREATE TABLE `attribute_en` (


`id` BIGINT(20) NOT NULL,


`createdDate` DATETIME NOT NULL,


`lastModifiedDate` DATETIME NOT NULL,


`version` BIGINT(20) NOT NULL,


`orders` INT(11) NULL DEFAULT NULL,


`name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,


`options` LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,


`propertyIndex` INT(11) NOT NULL,


`productCategory_id` BIGINT(20) NOT NULL,


PRIMARY KEY (`id`) ,


INDEX `ind_Attribute_productCategory_id` (`productCategory_id`)


)


ENGINE=INNODB


DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;





CREATE TABLE `brand_en` (


`id` BIGINT(20) NOT NULL,


`createdDate` DATETIME NOT NULL,


`lastModifiedDate` DATETIME NOT NULL,


`version` BIGINT(20) NOT NULL,


`orders` INT(11) NULL DEFAULT NULL,


`introduction` LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci NULL,


`logo` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,


`name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,


`type` INT(11) NOT NULL,


`url` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,


PRIMARY KEY (`id`) 


)


ENGINE=INNODB


DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;








ALTER TABLE `stocklog` ADD CONSTRAINT `fk_stocklog_stocklog_1` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouse` (`id`);


ALTER TABLE `product_country` ADD CONSTRAINT `fk_product_country_product_country_3` FOREIGN KEY (`product_id`) REFERENCES `product_en` (`id`);


ALTER TABLE `productcategory_en` ADD CONSTRAINT `fk_productcategory Copy 3_productcategory Copy 3_1` FOREIGN KEY (`parent_id`) REFERENCES `productcategory_en` (`id`);


--存折表
CREATE TABLE Fi_BankBook_Balance
(
  user_code       VARCHAR(20) NOT NULL,
  balance         DECIMAL(21,6) NOT NULL,
  TYPE            VARCHAR(2),
  VERSION         INT(11)
);
CREATE INDEX BANKBOOKBALANCE_INDEX ON Fi_BankBook_Balance (USER_CODE, TYPE);
CREATE INDEX IDX_BALANCE_USERCODE ON Fi_BankBook_Balance (USER_CODE);

-- 交易记录表
CREATE TABLE Fi_BankBook_Journal
(
  journal_id      BIGINT(20) NOT NULL,
  company_code    VARCHAR(4) NOT NULL,
  user_code       VARCHAR(20) NOT NULL,
  deal_type       VARCHAR(1),
  deal_date       DATETIME,
  money           DECIMAL(21,6),
  notes           VARCHAR(255),
  balance         DECIMAL(21,6),
  remark          VARCHAR(255),
  creater_code    VARCHAR(20),
  creater_name    VARCHAR(255),
  create_time     DATETIME,
  money_type      INT(5),
  unique_code     VARCHAR(200),
  last_journal_id BIGINT(20),
  last_money      DECIMAL(21,6),
  TYPE            VARCHAR(2)
)
 
 
-- Create/Recreate indexes 
CREATE INDEX BANKBOOKJUNI_INDEX ON Fi_BankBook_Journal (UNIQUE_CODE, TYPE) ;
CREATE INDEX BANKBOOKJUNI_INDEX1 ON Fi_BankBook_Journal (COMPANY_CODE, USER_CODE, TYPE); 
CREATE INDEX BANKBOOKJUNI_INDEX2 ON Fi_BankBook_Journal (TYPE, DEAL_DATE) ;
