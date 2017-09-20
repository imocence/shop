package net.shopxx.service;

import java.math.BigDecimal;
import java.util.Date;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.FiBankbookJournal.Type;
import net.shopxx.entity.Member;

/**
 * Service - 交易记录
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public interface FiBankbookJournalService extends BaseService<FiBankbookJournal, Long> {
	
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
	Page<FiBankbookJournal> findPage(Country country, FiBankbookJournal.Type type, FiBankbookJournal.MoneyType moneyType, Date beginDate, Date endDate, Pageable pageable);

	/**
	 * 获取最近的一条记录
	 * @param member
	 * @type type
	 * @return
	 */
	FiBankbookJournal findLastByMember(Member member, FiBankbookJournal.Type type);
	
	/**
	 * 根据会员编号充值购物券接口
	 * 
	 * @param usercode 会员编号
	 * @param money 资金
	 * @param uniqueCode 交易单号
	 * @param type 0:电子币账户  1:购物券账户
	 * @param dealType 0:存入  1取出
	 * @param moneyType 0:现金  1:Pos刷卡  2:线下转账 3:余额退款  4:财务冲账 5:保证金 6:手续费 7:退单退款  8:订单审核 9:物流费 10:在线充值 11:购物券转入 12:购物券转出
	 * @param notes 摘要
	 * @return
	 * @throws Exception
	 */
	String recharge(String usercode, BigDecimal money, String uniqueCode, FiBankbookJournal.Type type, FiBankbookJournal.DealType dealType, FiBankbookJournal.MoneyType moneyType, String notes) throws Exception;
	/**
	 * 根据会员编号查找充值记录接口
	 * @param type 
	 * 
	 * @param usercode 会员编号
	 * @return
	 * @throws Exception
	 */
	Page<FiBankbookJournal> findPageByMemberId(Type type, Member currentUser, Pageable pageNumber);
	/**
	 * 根据会员和编号模糊查询
	 * @param member
	 * 			会员
	 * @param code
	 * 			编号
	 * @return
	 */
	FiBankbookJournal findByMemberAndCode(Member member, String code);

}
