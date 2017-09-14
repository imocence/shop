/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.CountryDao;
import net.shopxx.entity.Country;

/**
 * Dao - 地区
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class CountryDaoImpl extends BaseDaoImpl<Country, Long> implements CountryDao {

	public List<Country> findRoots(Integer count) {
		String jpql = "select country from Country country where country.state = 1 order by country.order asc";
		TypedQuery<Country> query = entityManager.createQuery(jpql, Country.class);
		if (count != null) {
			query.setMaxResults(count);
		}
		return query.getResultList();
	}
	
	/**
	 * 根据name获取国家
	 * @param name
	 * @return
	 */
	public Country findByName(String name){
		String jpql = "select country from Country country WHERE country.state='1' and country.name=:name";
		TypedQuery<Country> query = entityManager.createQuery(jpql, Country.class).setParameter("name", name);
		List<Country> list= query.getResultList();
		if (null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}