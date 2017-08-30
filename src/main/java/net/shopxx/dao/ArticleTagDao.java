/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.ArticleTag;
import net.shopxx.entity.Country;

/**
 * Dao - 文章标签
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface ArticleTagDao extends BaseDao<ArticleTag, Long> {
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