/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.dao.NavigationDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.Navigation;
import net.shopxx.entity.Navigation.Position;
import net.shopxx.service.NavigationService;

/**
 * Service - 导航
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class NavigationServiceImpl extends BaseServiceImpl<Navigation, Long> implements NavigationService {

	@Inject
	private NavigationDao navigationDao;

	@Transactional(readOnly = true)
	public List<Navigation> findList(Navigation.Position position) {
		return navigationDao.findList(position);
	}
	
	/**
	 * 查找导航
	 * 
	 * @param position
	 *            位置
	 * @param country
	 *            国家
	 * @return 导航
	 */
	@Transactional(readOnly = true)
	public List<Navigation> findList(Navigation.Position position, Country country){
		return navigationDao.findList(position, country);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "navigation", condition = "#useCache")
	public List<Navigation> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return navigationDao.findList(null, count, filters, orders);
	}
	
	@Transactional(readOnly = true)
	@Cacheable(value = "navigation", condition = "#useCache")
	public List<Navigation> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache, Country country) {
		Filter filter = new Filter();
		filter.setProperty("country");
		filter.setValue(country);
		filter.setOperator(Filter.Operator.eq);
		filters.add(filter);
		return navigationDao.findList(null, count, filters, orders);
	}
	@Transactional(readOnly = true)
	@Cacheable(value = "navigation", condition = "#useCache")
	public List<Navigation> findList(Position position, boolean useCache,Country country){
		return navigationDao.findList(position, country);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "navigation", allEntries = true)
	public Navigation save(Navigation navigation) {
		return super.save(navigation);
	}

	@Override
	@Transactional
	@CacheEvict(value = "navigation", allEntries = true)
	public Navigation update(Navigation navigation) {
		return super.update(navigation);
	}

	@Override
	@Transactional
	@CacheEvict(value = "navigation", allEntries = true)
	public Navigation update(Navigation navigation, String... ignoreProperties) {
		return super.update(navigation, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "navigation", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "navigation", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "navigation", allEntries = true)
	public void delete(Navigation navigation) {
		super.delete(navigation);
	}

}