package net.shopxx.service;

import java.math.BigDecimal;

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
	 * 根据usercode和type获取FiBankbookBalance
	 * @param usercode
	 * @param type
	 * @return
	 */
	FiBankbookBalance find(Member member, FiBankbookBalance.Type type);
	
	/**
	 * 余额更新
	 * @param fiBankbookBalance
	 * @param amount
	 * @throws Exception 
	 */
	void addBalance(FiBankbookBalance fiBankbookBalance, BigDecimal amount) throws Exception;
}