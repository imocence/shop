package net.shopxx.dao;

import java.util.List;

import net.shopxx.entity.Area;
import net.shopxx.entity.Country;

/**
 * Dao - 国家
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface CountryDao extends BaseDao<Country, Long> {

	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	List<Country> findRoots(Integer count);

	/**
	 * 根据name获取国家
	 * @param name
	 * @return
	 */
	Country findByName(String name);

}