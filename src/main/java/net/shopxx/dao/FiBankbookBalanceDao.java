/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;
import java.util.List;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;

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


	 /* 查找实体对象分页
	 *
	 * @param member
	 *            会员
	 * @param country
	 *            国家
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	Page<FiBankbookBalance> findPage(Member member, Country country, Pageable pageable);
	
	/**
	 * 根据member和type获取FiBankbookBalance
	 * @param member
	 * @param type
	 * @return
	 */
	FiBankbookBalance find(Member member, FiBankbookBalance.Type type);
}
