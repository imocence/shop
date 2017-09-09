package net.shopxx.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FiBankbookJournalDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.Member;

import org.springframework.stereotype.Repository;

/**
 * Dao - 交易记录
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Repository
public class FiBankbookJournalDaoImpl extends BaseDaoImpl<FiBankbookJournal, Long> implements FiBankbookJournalDao {
	
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
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FiBankbookJournal> criteriaQuery = criteriaBuilder.createQuery(FiBankbookJournal.class);
		Root<FiBankbookJournal> root = criteriaQuery.from(FiBankbookJournal.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (country != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("country"), country));
		}
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (moneyType != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("moneyType"), moneyType));
		}
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("dealDate"), beginDate)));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("dealDate"), endDate)));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	/**
	 * 查找交易记录分页
	 * 
	 * @param currentUser
	 *            会员信息
	 * @param pageable
	 *            分页信息
	 * @return 交易记录分页
	 */
	public Page<FiBankbookJournal> findPageByMemberId(FiBankbookJournal.Type type,Member currentUser,Pageable pageNumber){
		if (currentUser == null) {
			return Page.emptyPage(pageNumber);
		}
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FiBankbookJournal> criteriaQuery = criteriaBuilder.createQuery(FiBankbookJournal.class);
		Root<FiBankbookJournal> root = criteriaQuery.from(FiBankbookJournal.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (currentUser != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), currentUser));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageNumber);
	}
	
	/**
	 * 获取最近的一条记录
	 * @param member
	 * @type type
	 * @return
	 */
	public FiBankbookJournal findLastByMember(Member member, FiBankbookJournal.Type type){
		String jpql = "SELECT fiBankbookJournal FROM FiBankbookJournal fiBankbookJournal WHERE fiBankbookJournal.member=:member AND type=:type order by dealDate desc, id desc";
		TypedQuery<FiBankbookJournal> query = entityManager.createQuery(jpql, FiBankbookJournal.class);
		query.setParameter("member", member);
		query.setParameter("type", type);
		query.setMaxResults(1);
		List<FiBankbookJournal> list= query.getResultList();
		if (null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}