/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.StockLog;

/**
 * Dao - 库存记录
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface StockLogDao extends BaseDao<StockLog, Long> {

	Page<StockLog> findPage(Country country, Pageable pageable);
}