/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.SheetDao;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Country;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;
import net.shopxx.entity.PaymentMethod;
import net.shopxx.entity.Sheet;
import net.shopxx.entity.Sku;
import net.shopxx.entity.Sheet.Status;

/**
 * Dao - 订单
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class SheetDaoImpl extends BaseDaoImpl<Sheet, Long> implements SheetDao {
	/**
	 * 
	 * @param status
	 * 			状态
	 * @param createName
	 * 			创建人姓名
	 * @param modifyName
	 * 			审核人姓名
	 * @param country
	 * 			国家
	 * @param hasExpired
	 * 			是否过期
	 * @param pageable
	 * 			分页信息
	 * @return
	 */
	public Page<Sheet> findPage(Status status, Admin admin, String modifyName,
			Country country, Boolean hasExpired, Pageable pageable){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Sheet> criteriaQuery = criteriaBuilder.createQuery(Sheet.class);
		Root<Sheet> root = criteriaQuery.from(Sheet.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();

		if (status != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("status"), status));
		}
		if (admin != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("admin"), admin));
		}
		if (modifyName != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("modifyName"), modifyName));
		}
		if (country != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("country"), country));
		}

		if (hasExpired != null) {
			if (hasExpired) {
				restrictions = criteriaBuilder.and(restrictions, root.get("expire").isNotNull(), criteriaBuilder.lessThanOrEqualTo(root.<Date>get("expire"), new Date()));
			} else {
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThan(root.<Date>get("expire"), new Date())));
			}
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
}