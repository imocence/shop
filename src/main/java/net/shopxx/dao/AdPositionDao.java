/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import net.shopxx.entity.AdPosition;
import net.shopxx.entity.Country;

/**
 * Dao - 广告位
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface AdPositionDao extends BaseDao<AdPosition, Long> {
	/**
	 * 查找下级广告位
	 * 
	 * @param country
	 *            国家        
	 * @return 下级广告位
	 */
	List<AdPosition> findChildren(Country country);
	/**
	 	查找广告位
	 * @param orders
	 *            位置    
	 * @param country
	 *            国家        
	 * @return 下级广告位
	 */
	AdPosition find(String orders,Country country);

}