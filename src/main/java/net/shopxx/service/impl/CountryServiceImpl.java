/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.dao.CountryDao;
import net.shopxx.entity.Country;
import net.shopxx.service.CountryService;

/**
 * Service - 地区
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class CountryServiceImpl extends BaseServiceImpl<Country, Long> implements CountryService {

	@Inject
	private CountryDao areaDao;

	@Transactional(readOnly = true)
	public List<Country> findRoots() {
		return areaDao.findRoots(null);
	}
	

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public Country save(Country area) {
		Assert.notNull(area);
		return super.save(area);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public Country update(Country area) {
		Assert.notNull(area);
		return super.update(area);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public Country update(Country area, String... ignoreProperties) {
		return super.update(area, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "area", allEntries = true)
	public void delete(Country area) {
		super.delete(area);
	}

	

}