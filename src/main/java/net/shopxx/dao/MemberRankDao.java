/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.math.BigDecimal;
import java.util.List;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.MemberRank;

/**
 * Dao - 会员等级
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface MemberRankDao extends BaseDao<MemberRank, Long> {

	/**
	 * 查找默认会员等级
	 * 
	 * @return 默认会员等级，若不存在则返回null
	 */
	MemberRank findDefault();

	/**
	 * 根据消费金额查找符合此条件的最高会员等级
	 * 
	 * @param amount
	 *            消费金额
	 * @return 会员等级，不包含特殊会员等级，若不存在则返回null
	 */
	MemberRank findByAmount(BigDecimal amount);

	/**
	 * 清除默认
	 */
	void clearDefault();

	/**
	 * 清除默认
	 * 
	 * @param exclude
	 *            排除会员等级
	 */
	void clearDefault(MemberRank exclude);

    Page<MemberRank> findPage(Country country, Pageable pageable);
    /**
     * 根据国家跟新会员等级
     * @param country
     * @return
     */
	MemberRank findByCountry(Country country,MemberRank.Type type);
	/**
     * 根据会员类型查找
     * @param MemberRank.Type
     * @return
     */
	List<MemberRank> findByType(MemberRank.Type type);

}