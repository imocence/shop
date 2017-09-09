/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.StockLogDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.StockLog;

/**
 * Dao - 库存记录
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class StockLogDaoImpl extends BaseDaoImpl<StockLog, Long> implements StockLogDao {

	
	@Override
	public Page<StockLog> findPage(Country country, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StockLog> criteriaQuery = criteriaBuilder.createQuery(StockLog.class);
        Root<StockLog> root = criteriaQuery.from(StockLog.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        if (country != null) {
            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("sku").get("product").get("productCategory").get("country"), country));
        }
        criteriaQuery.where(restrictions);
        return super.findPage(criteriaQuery, pageable);
	}
	
}