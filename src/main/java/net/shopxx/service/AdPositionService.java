/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.AdPosition;
import net.shopxx.entity.Country;

/**
 * Service - 广告位
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface AdPositionService extends BaseService<AdPosition, Long> {

	/**
	 * 查找广告位
	 * 
	 * @param id
	 *            ID
	 * @param useCache
	 *            是否使用缓存
	 * @return 广告位
	 */
	AdPosition find(Long id, boolean useCache);

	
	/**
	 * 查找广告位分类树
	 * @param country
	 *            国家
	 * @return 文章分类树
	 */
	List<AdPosition> findTree(Country country);

	/**
	 * 
	 * @param orders
	 * 			位置
	 * @param country
	 * 			國家
	 * @param useCache
	 * 			是否使用缓存
	 * @return
	 */
	AdPosition find(String orders, Country country, boolean useCache);

}