/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.StockLogDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.StockLog;
import net.shopxx.service.StockLogService;

/**
 * Service - 库存记录
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class StockLogServiceImpl extends BaseServiceImpl<StockLog, Long> implements StockLogService {

	@Inject
	private StockLogDao stockLogDao;
	
	@Override
	public Page<StockLog> findPage(Country country, Pageable pageable) {
		return stockLogDao.findPage(country,pageable);
	}
	
}