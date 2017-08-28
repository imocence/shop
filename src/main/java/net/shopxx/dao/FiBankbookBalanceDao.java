/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

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
	 * 查找实体对象分页
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