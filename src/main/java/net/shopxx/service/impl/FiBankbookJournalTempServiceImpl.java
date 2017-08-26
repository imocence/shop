package net.shopxx.service.impl;

import java.util.Date;

import javax.inject.Inject;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FiBankbookJournalTempDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournalTemp;
import net.shopxx.service.FiBankbookJournalTempService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 充值确认
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Service
public class FiBankbookJournalTempServiceImpl extends BaseServiceImpl<FiBankbookJournalTemp, Long> implements FiBankbookJournalTempService {
	
	@Inject
	private FiBankbookJournalTempDao fiBankbookJournalTempDao;
	
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
	public Page<FiBankbookJournalTemp> findPage(Country country, FiBankbookJournalTemp.Type type, FiBankbookJournalTemp.MoneyType moneyType, FiBankbookJournalTemp.ConfirmStatus confirmStatus, Date beginDate, Date endDate, Pageable pageable){
		return fiBankbookJournalTempDao.findPage(country, type, moneyType, confirmStatus, beginDate, endDate, pageable);
	}
	
	@Override
	@Transactional
	public FiBankbookJournalTemp save(FiBankbookJournalTemp fiBankbookJournalTemp) {
		return super.save(fiBankbookJournalTemp);
	}

	@Override
	@Transactional
	public FiBankbookJournalTemp update(FiBankbookJournalTemp fiBankbookJournalTemp) {
		return super.update(fiBankbookJournalTemp);
	}

	@Override
	@Transactional
	public FiBankbookJournalTemp update(FiBankbookJournalTemp fiBankbookJournalTemp, String... ignoreProperties) {
		return super.update(fiBankbookJournalTemp, ignoreProperties);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	public void delete(FiBankbookJournalTemp fiBankbookJournalTemp) {
		super.delete(fiBankbookJournalTemp);
	}

}