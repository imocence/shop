package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.FiBankbookBalance;

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
	List<FiBankbookBalance> findByUserCode(String userCode);

}