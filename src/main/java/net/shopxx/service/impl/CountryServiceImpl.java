/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.shopxx.dao.CountryDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.Language;
import net.shopxx.entity.Member;
import net.shopxx.service.CountryService;
import net.shopxx.util.PropertyUtil;

/**
 * Service - 地区
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class CountryServiceImpl extends BaseServiceImpl<Country, Long> implements CountryService {

	@Inject
	private CacheManager cacheManager;
	@Inject
	private CountryDao countryDao;

	@Transactional(readOnly = true)
	public List<Country> findRoots() {
		return countryDao.findRoots(null);
	}
	
	/**
	 * 根据name获取国家
	 * @param name
	 * @return
	 */
	public Country findByName(String name){
		Ehcache cache = cacheManager.getEhcache(Language.LANGUAGE_CACHE_NAME);
		String key = "name_" + name;
		Element element = cache.get(key);
		if (element != null) {
			return (Country)element.getObjectValue();
		} else {
			Country country = countryDao.findByName(name);
			cache.put(new Element(key, country));
			return country;
		}
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

    @Override
    public Country getDefaultCountry() {
        Subject s  = SecurityUtils.getSubject();
        Member currentUser = null;
        if (s.isAuthenticated() && s.getPrincipal() instanceof Member) {
            currentUser =  (Member) s.getPrincipal();
        }
        Country country = null;
        if (currentUser == null) {
           String countryId =  PropertyUtil.getProperty("default.country.id");
           country = find(Long.valueOf(countryId));
        } else {
            country =   currentUser.getCountry();
        }
        return country;
    }

	

}