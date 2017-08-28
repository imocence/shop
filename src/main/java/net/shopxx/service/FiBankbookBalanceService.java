package net.shopxx.service;

import java.util.List;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Member;

/**
 * Service - 存折
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public interface FiBankbookBalanceService extends BaseService<FiBankbookBalance, Long> {
	/**
	 * 根据会员编号查找存折账号
	 * @param userCode
	 * @return
	 */
	List<FiBankbookBalance> findList(Member member, Integer count, List<Filter> filters, List<Order> orders);
	/**
	 * 根据会员编号查找存折账号
	 * @param userCode
	 * 
	 */
	FiBankbookBalance findByKey(String fiBanKey, String fiBanValue);
	
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
}
