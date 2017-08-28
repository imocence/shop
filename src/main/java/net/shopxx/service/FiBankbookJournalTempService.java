package net.shopxx.service;

import java.util.Date;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.FiBankbookJournalTemp;

/**
 * Service - 充值确认
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public interface FiBankbookJournalTempService extends BaseService<FiBankbookJournalTemp, Long> {
	
	/**
	 * 查找充值确认分页
	 * 
	 * @param country
	 *            国家
	 * @param type
	 *            账户类型
	 * @param moneyType
	 *            资金类型
	 * @param confirmStatus 
	 *            核实状态
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param pageable
	 *            分页信息
	 * @return 交易记录分页
	 */
	Page<FiBankbookJournalTemp> findPage(Country country, FiBankbookJournal.Type type, FiBankbookJournal.MoneyType moneyType, FiBankbookJournalTemp.ConfirmStatus confirmStatus, Date beginDate, Date endDate, Pageable pageable);
	
	/**
	 * 核实实体对象
	 * 
	 * @param ids
	 *            ID
	 */
	void confirm(Long... ids);
}