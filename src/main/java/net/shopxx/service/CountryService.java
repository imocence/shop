package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.Country;

/**
 * Service - 国家
 * 
 * @author xcy
 * @version 5.0.3
 */
public interface CountryService extends BaseService<Country, Long> {

	/**
	 * 查找顶级地区
	 * 
	 * @return 顶级地区
	 */
	List<Country> findRoots();
	
	/**
	 * 根据name获取国家
	 * @param name
	 * @return
	 */
	Country findByName(String name);

    Country getDefaultCountry();
}