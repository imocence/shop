package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.dao.FiBankbookBalanceDao;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Member;
import net.shopxx.service.FiBankbookBalanceService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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