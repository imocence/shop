/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Member;

/**
 * Dao - 存折
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public interface FiBankbookBalanceDao extends BaseDao<FiBankbookBalance, Long> {
	/**
	 * 根据会员编号查找存折
	 * @param usercode
	 * 		会员编号
	 * @param count
	 * 		数量
	 * @param filters
	 * 		筛选
	 * @param orders
	 * 		排序
	 * @return
	 */
	List<FiBankbookBalance> findList(Member member,Integer count,List<Filter> filters, List<Order> orders);

}