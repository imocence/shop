package net.shopxx.dao;

import java.util.Date;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.FiBankbookJournalTemp;

/**
 * Dao - 交易记录临时
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public interface FiBankbookJournalTempDao extends BaseDao<FiBankbookJournalTemp, Long> {
	
	/**
	 * 查找交易记录分页
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
	Page<FiBankbookJournalTemp> findPage(Country country, FiBankbookJournalTemp.Type type, FiBankbookJournalTemp.MoneyType moneyType, FiBankbookJournalTemp.ConfirmStatus confirmStatus, Date beginDate, Date endDate, Pageable pageable);
}