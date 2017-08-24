/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
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

}