package net.shopxx.service.impl;

import java.util.List;
import java.math.BigDecimal;

import javax.inject.Inject;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.dao.FiBankbookBalanceDao;

import javax.persistence.LockModeType;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Member;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.util.SpringUtils;

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
	@Inject
	FiBankbookBalanceService fiBankbookBalanceService;
	
	@Transactional(readOnly = true)
	public List<FiBankbookBalance> findList(Member member,Integer count, List<Filter> filters, List<Order> orders){		
		return fiBankbookBalanceDao.findList(member,count, filters, orders);
	}
	

	@Transactional(readOnly = true)
	public FiBankbookBalance findByKey(String usercodekey,String usercode) {
		return fiBankbookBalanceDao.find(usercodekey, usercode);
	}
	
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
	 * @throws Exception 
	 */
	@Transactional
	public void addBalance(FiBankbookBalance fiBankbookBalance, BigDecimal amount) throws Exception{
		if (!LockModeType.PESSIMISTIC_WRITE.equals(fiBankbookBalanceDao.getLockMode(fiBankbookBalance))) {
			fiBankbookBalanceDao.flush();
			fiBankbookBalanceDao.refresh(fiBankbookBalance, LockModeType.PESSIMISTIC_WRITE);
		}
		Assert.notNull(fiBankbookBalance.getBalance());
		fiBankbookBalance.setBalance(fiBankbookBalance.getBalance().add(amount));
		// 用户余额不能小于0
		if (fiBankbookBalance.getBalance().doubleValue() < 0) {
			throw new Exception(SpringUtils.getMessage("admin.fiBankbookJournalTemp.error.balance.insufficient", fiBankbookBalance.getMember().getUsercode()));
		}
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
