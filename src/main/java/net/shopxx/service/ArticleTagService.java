/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import java.util.List;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.ArticleTag;
import net.shopxx.entity.Country;

/**
 * Service - 文章标签
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface ArticleTagService extends BaseService<ArticleTag, Long> {

	/**
	 * 查找文章标签
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 文章标签
	 */
	List<ArticleTag> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache);
	
	/**
	 * 搜索文章标签分页
	 * 
	 * @param country
	 *            国家
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	Page<ArticleTag> findPage(Country country, Pageable pageable);

}