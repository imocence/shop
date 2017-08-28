package net.shopxx.service.impl;

import java.util.Date;

import javax.inject.Inject;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FiBankbookJournalDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.service.FiBankbookJournalService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service - 交易记录
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Service
public class FiBankbookJournalServiceImpl extends BaseServiceImpl<FiBankbookJournal, Long> implements FiBankbookJournalService {
	
	@Inject
	private FiBankbookJournalDao fiBankbookJournalDao;
	
	/**
	 * 查找交易记录分页
	 * 
	 * @param country
	 *            国家
	 * @param type
	 *            账户类型
	 * @param moneyType
	 *            资金类型
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param pageable
	 *            分页信息
	 * @return 交易记录分页
	 */
	public Page<FiBankbookJournal> findPage(Country country, FiBankbookJournal.Type type, FiBankbookJournal.MoneyType moneyType, Date beginDate, Date endDate, Pageable pageable){
		return fiBankbookJournalDao.findPage(country, type, moneyType, beginDate, endDate, pageable);
	}
	
	@Override
	@Transactional
	public FiBankbookJournal save(FiBankbookJournal fiBankbookJournal) {
		return super.save(fiBankbookJournal);
	}

	@Override
	@Transactional
	public FiBankbookJournal update(FiBankbookJournal fiBankbookJournal) {
		return super.update(fiBankbookJournal);
	}

	@Override
	@Transactional
	public FiBankbookJournal update(FiBankbookJournal fiBankbookJournal, String... ignoreProperties) {
		return super.update(fiBankbookJournal, ignoreProperties);
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
	public void delete(FiBankbookJournal fiBankbookJournal) {
		super.delete(fiBankbookJournal);
	}

}