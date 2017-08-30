package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FiBankbookJournalTempDao;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.FiBankbookJournalTemp;
import net.shopxx.entity.Member;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.FiBankbookJournalTempService;
import net.shopxx.service.UserService;
import net.shopxx.util.DateUtil;
import net.shopxx.util.SpringUtils;

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
	
	@Inject
	private FiBankbookJournalService fiBankbookJournalService;
	
	@Inject
	private FiBankbookBalanceService fiBankbookBalanceService;
	
	@Inject
	private UserService userService;
	
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
	public Page<FiBankbookJournalTemp> findPage(Country country, FiBankbookJournal.Type type, FiBankbookJournal.MoneyType moneyType, FiBankbookJournalTemp.ConfirmStatus confirmStatus, Date beginDate, Date endDate, Pageable pageable){
		return fiBankbookJournalTempDao.findPage(country, type, moneyType, confirmStatus, beginDate, endDate, pageable);
	}
	
	/**
	 * 核实实体对象
	 * 
	 * @param ids
	 *            ID
	 */
	@Transactional(rollbackFor = Exception.class)
	public String confirm(Long... ids) throws Exception{
		// 获取当前用户
		Admin currentUser = userService.getCurrent(Admin.class);
		if (null == currentUser) {
			throw new Exception(SpringUtils.getMessage("admin.common.session.lost"));
		}
		
		if (ids != null) {
			for (Long id : ids) {
				// 更新核实状态为核实、处理时间为当前时间
				FiBankbookJournalTemp fiBankbookJournalTemp = fiBankbookJournalTempDao.find(id);
				Member member = fiBankbookJournalTemp.getMember();
				// 交易状态是否已经被核实
				if (fiBankbookJournalTemp.getConfirmStatus() == FiBankbookJournalTemp.ConfirmStatus.confirmed) {
					throw new Exception(SpringUtils.getMessage("admin.fiBankbookJournalTemp.error.confirmed", member.getUsercode(), DateUtil.dateTimeFormat(fiBankbookJournalTemp.getCreatedDate())));
				}
				fiBankbookJournalTemp.setConfirmCode(String.valueOf(currentUser.getId()));
				fiBankbookJournalTemp.setConfirmName(currentUser.getUsername());
				fiBankbookJournalTemp.setConfirmStatus(FiBankbookJournalTemp.ConfirmStatus.confirmed);
				fiBankbookJournalTemp.setDealDate(new Date());
				update(fiBankbookJournalTemp);
				
				// 如果修改的金额为0则无需继续处理
				BigDecimal amount = fiBankbookJournalTemp.getMoney();
				if (amount.compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				
				// 更新fi_bankbook_balance表
				// 获取需要更新的金额  存入是add 取出是subtract
				if (FiBankbookJournal.DealType.takeout == fiBankbookJournalTemp.getDealType()) {
					BigDecimal zero = new BigDecimal(0);
					amount = zero.subtract(amount);
				}
				Country country = fiBankbookJournalTemp.getCountry();
				FiBankbookBalance.Type balanceType = FiBankbookBalance.Type.balance;
				if (FiBankbookJournal.Type.coupon == fiBankbookJournalTemp.getType()) {
					balanceType = FiBankbookBalance.Type.coupon;
				}
				
				// 获取用户余额
				FiBankbookBalance fiBankbookBalance = fiBankbookBalanceService.find(member, balanceType);
				// 获取最近的fiBankbookJournal记录
				FiBankbookJournal lastFiBankbookJournal = fiBankbookJournalService.findLastByMember(member, fiBankbookJournalTemp.getType());
				// 最近一笔交易费用的钱
				BigDecimal lastMoney = null;
				if (null != lastFiBankbookJournal) {
					lastMoney = lastFiBankbookJournal.getMoney();
				}
				// 用户余额为空则新增一条记录
				if (null == fiBankbookBalance) {
					fiBankbookBalance = new FiBankbookBalance();
					fiBankbookBalance.setBalance(amount);
					fiBankbookBalance.setCountry(country);
					fiBankbookBalance.setMember(member);
					fiBankbookBalance.setType(balanceType);
					fiBankbookBalanceService.save(fiBankbookBalance);
				}
				// 更新用户余额
				else {
					// 最近一笔交易记录不存在，则需要交易余额是否为0，为0则可以更新用户余额
					if (null == lastFiBankbookJournal) {
						if (fiBankbookBalance.getBalance().doubleValue() != 0){
							throw new Exception(SpringUtils.getMessage("admin.fiBankbookJournalTemp.error.balance.zero", member.getUsercode()));
						}
						// 更新用户的余额
						fiBankbookBalanceService.addBalance(fiBankbookBalance, amount);
					}
					// 如果最近的一条记录不为空并且用户余额不为空，校验交易记录的最近一条的余额和用户的余额是否一致
					else {
						if (fiBankbookBalance.getBalance().doubleValue() != lastFiBankbookJournal.getBalance().doubleValue()){
							throw new Exception(SpringUtils.getMessage("admin.fiBankbookJournalTemp.error.balance.error", member.getUsercode(), fiBankbookBalance.getBalance(), lastFiBankbookJournal.getBalance()));
						}
						// 更新用户的余额
						fiBankbookBalanceService.addBalance(fiBankbookBalance, amount);
					}
				}
				
				// 新增一条记录到fiBankbookJournal表
				FiBankbookJournal fiBankbookJournal = new FiBankbookJournal();
				fiBankbookJournal.setBalance(fiBankbookBalance.getBalance());
				fiBankbookJournal.setCountry(fiBankbookJournalTemp.getCountry());
				fiBankbookJournal.setCreaterCode(String.valueOf(currentUser.getId()));
				fiBankbookJournal.setCreaterName(currentUser.getUsername());
				fiBankbookJournal.setDealDate(new Date());
				fiBankbookJournal.setDealType(fiBankbookJournalTemp.getDealType());
				fiBankbookJournal.setFiBankbookJournalTemp(fiBankbookJournalTemp);
				fiBankbookJournal.setLastFiBankbookJournal(lastFiBankbookJournal);
				fiBankbookJournal.setLastMoney(lastMoney);
				fiBankbookJournal.setMember(member);
				fiBankbookJournal.setMoney(fiBankbookJournalTemp.getMoney());
				fiBankbookJournal.setMoneyType(fiBankbookJournalTemp.getMoneyType());
				fiBankbookJournal.setNotes(fiBankbookJournalTemp.getNotes());
				fiBankbookJournal.setRemark(fiBankbookJournalTemp.getRemark());
				fiBankbookJournal.setType(fiBankbookJournalTemp.getType());
				fiBankbookJournal.setUniqueCode(fiBankbookJournalTemp.getUniqueCode());
				fiBankbookJournalService.save(fiBankbookJournal);
			}
		}
		return "";
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