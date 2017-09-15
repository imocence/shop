/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.dao.AdPositionDao;
import net.shopxx.entity.AdPosition;
import net.shopxx.entity.ArticleCategory;
import net.shopxx.entity.Country;

/**
 * Dao - 广告位
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class AdPositionDaoImpl extends BaseDaoImpl<AdPosition, Long> implements AdPositionDao {

	public List<AdPosition> findChildren(Country country){

		String jpql = "select adPosition from AdPosition adPosition where 1=1 ";

		if (country != null) {
			jpql += " and country=:country ";
		}
		jpql += " order by adPosition.createdDate asc";
		TypedQuery<AdPosition> query = entityManager.createQuery(jpql, AdPosition.class);
		
		if (country != null) {
			query.setParameter("country", country);
		}

		List<AdPosition> result = query.getResultList();
		return result;
	}
	public AdPosition find(String orders,Country country){
		String jpql = "select adPosition from AdPosition adPosition where 1=1 ";

		if (country != null) {
			jpql += " and country=:country ";
		}
		jpql += " order by adPosition.createdDate asc";
		TypedQuery<AdPosition> query = entityManager.createQuery(jpql, AdPosition.class);
		
		if (country != null) {
			query.setParameter("country", country);
		}

		List<AdPosition> result = query.getResultList();
		if(result.size() > 0){
			return result.get(0);
		}else{
			return null;
		}
		
	}
}