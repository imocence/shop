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

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.ArticleTagDao;
import net.shopxx.entity.ArticleTag;
import net.shopxx.entity.Country;

import org.springframework.stereotype.Repository;

/**
 * Dao - 文章标签
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class ArticleTagDaoImpl extends BaseDaoImpl<ArticleTag, Long> implements ArticleTagDao {
	/**
	 * 搜索文章标签分页
	 * 
	 * @param country
	 *            国家
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	public Page<ArticleTag> findPage(Country country, Pageable pageable){
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ArticleTag> criteriaQuery = criteriaBuilder.createQuery(ArticleTag.class);
		Root<ArticleTag> root = criteriaQuery.from(ArticleTag.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (country != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("country"), country));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}
}