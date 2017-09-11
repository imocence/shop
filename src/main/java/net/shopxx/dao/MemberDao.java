/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.Date;
import java.util.List;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;

/**
 * Dao - 会员
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface MemberDao extends BaseDao<Member, Long> {

	/**
	 * 查找会员分页
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param pageable
	 *            分页信息
	 * @return 会员分页
	 */
	Page<Member> findPage(Member.RankingType rankingType, Pageable pageable);

	/**
	 * 查询会员注册数
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 会员注册数
	 */
	Long registerMemberCount(Date beginDate, Date endDate);

	/**
	 * 清除会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 */
	void clearAttributeValue(MemberAttribute memberAttribute);
	
	/**
	 * 根据编号和名称查找会员
	 * @param keyword
	 * @param country
	 * @param count
	 * @return
	 */
	List<Member> search(String keyword, Country country, Integer count);
	
	/**
	 * 根据usercode查找会员
	 * 
	 * @param usercode
	 *            用户编号
	 * @return 会员，若不存在则返回null
	 */
	Member findByUsercode(String usercode);

}