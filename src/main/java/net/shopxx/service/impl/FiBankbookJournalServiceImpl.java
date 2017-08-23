package net.shopxx.service.impl;

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