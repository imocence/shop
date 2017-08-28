/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.ParameterDao;
import net.shopxx.entity.Brand;
import net.shopxx.entity.Country;
import net.shopxx.entity.Parameter;
import net.shopxx.entity.ProductCategory;

/**
 * Dao - 参数
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class ParameterDaoImpl extends BaseDaoImpl<Parameter, Long> implements ParameterDao {

    @Override
    public Page<Parameter> findPage(Country country, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Parameter> criteriaQuery = criteriaBuilder.createQuery(Parameter.class);
        Root<Parameter> root = criteriaQuery.from(Parameter.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        if (country != null) {
            Subquery<ProductCategory> subquery = criteriaQuery.subquery(ProductCategory.class);
            Root<ProductCategory> subqueryRoot = subquery.from(ProductCategory.class);
            subquery.select(subqueryRoot);
            subquery.where(criteriaBuilder.equal(subqueryRoot.get("country"), country));
            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("productCategory")).value(subquery));
        }
        criteriaQuery.where(restrictions);
        return super.findPage(criteriaQuery, pageable);
    }

}