package net.shopxx.service.impl;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.persistence.LockModeType;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FiBankbookBalanceDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Member;
import net.shopxx.service.FiBankbookBalanceService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Service - 存折
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Service
public class FiBankbookBalanceServiceImpl extends BaseServiceImpl<FiBankbookBalance, Long> implements FiBankbookBalanceService {
	
	@Inject
	private FiBankbookBalanceDao fiBankbookBalanceDao;
	
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
	public Page<FiBankbookBalance> findPage(Member member, Country country, Pageable pageable){
		return fiBankbookBalanceDao.findPage(member, country, pageable);
	}
	
	/**
	 * 根据member和type获取FiBankbookBalance
	 * @param member
	 * @param type
	 * @return
	 */
	public FiBankbookBalance find(Member member, FiBankbookBalance.Type type){
		return fiBankbookBalanceDao.find(member, type);
	}
	
	/**
	 * 余额更新
	 * @param fiBankbookBalance
	 * @param amount
	 */
	@Transactional
	public void addBalance(FiBankbookBalance fiBankbookBalance, BigDecimal amount){
		if (!LockModeType.PESSIMISTIC_WRITE.equals(fiBankbookBalanceDao.getLockMode(fiBankbookBalance))) {
			fiBankbookBalanceDao.flush();
			fiBankbookBalanceDao.refresh(fiBankbookBalance, LockModeType.PESSIMISTIC_WRITE);
		}
		Assert.notNull(fiBankbookBalance.getBalance());
		fiBankbookBalance.setBalance(fiBankbookBalance.getBalance().add(amount));
		fiBankbookBalanceDao.flush();
	}
	
	@Override
	@Transactional
	public FiBankbookBalance save(FiBankbookBalance fiBankbookBalance) {
		return super.save(fiBankbookBalance);
	}

	@Override
	@Transactional
	public FiBankbookBalance update(FiBankbookBalance fiBankbookBalance) {
		return super.update(fiBankbookBalance);
	}

	@Override
	@Transactional
	public FiBankbookBalance update(FiBankbookBalance fiBankbookBalance, String... ignoreProperties) {
		return super.update(fiBankbookBalance, ignoreProperties);
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
	public void delete(FiBankbookBalance fiBankbookBalance) {
		super.delete(fiBankbookBalance);
	}

}