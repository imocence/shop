package net.shopxx.dao;

import java.util.Date;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.FiBankbookJournal.Type;
import net.shopxx.entity.Member;

/**
 * Dao - 交易记录
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public interface FiBankbookJournalDao extends BaseDao<FiBankbookJournal, Long> {
	
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
	 * 查找交易记录分页
	 * @param type 
	 * 
	 * @param currentUser
	 *            会员信息
	 * @param pageable
	 *            分页信息
	 * @return 交易记录分页
	 */
	Page<FiBankbookJournal> findPageByMemberId(Type type, Member currentUser,Pageable pageNumber);
}