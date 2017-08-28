package net.shopxx.dao.impl;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FiBankbookJournalTempDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.FiBankbookJournalTemp;

import org.springframework.stereotype.Repository;

/**
 * Dao - 交易记录
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Repository
public class FiBankbookJournalTempDaoImpl extends BaseDaoImpl<FiBankbookJournalTemp, Long> implements FiBankbookJournalTempDao {
	
	/**
	 * 查找交易记录分页
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
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FiBankbookJournalTemp> criteriaQuery = criteriaBuilder.createQuery(FiBankbookJournalTemp.class);
		Root<FiBankbookJournalTemp> root = criteriaQuery.from(FiBankbookJournalTemp.class);
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
		if (confirmStatus != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("confirmStatus"), confirmStatus));
		}
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("createdDate"), beginDate)));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("createdDate"), endDate)));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
	
}