package net.shopxx.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.RemittanceLogDao;
import net.shopxx.entity.Member;
import net.shopxx.entity.RemittanceLog;

/**
 * Dao - 汇款登记
 * 
 * @author cht
 * @version 1.0.0
 */
@Repository
public class RemittanceLogDaoImpl  extends BaseDaoImpl<RemittanceLog, Long> implements RemittanceLogDao{
	
	public Page<RemittanceLog> findPage(Member member, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<RemittanceLog> criteriaQuery = criteriaBuilder.createQuery(RemittanceLog.class);
		Root<RemittanceLog> root = criteriaQuery.from(RemittanceLog.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (member != null) {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(criteriaBuilder.and(criteriaBuilder.equal(root.get("member"), member))));
		} else {
			restrictions = criteriaBuilder.and(restrictions,
					criteriaBuilder.or(criteriaBuilder.and(criteriaBuilder.isNull(root.get("member")))));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);

	}
}
