package net.shopxx.dao;

import java.util.List;

import net.shopxx.entity.Language;

/**
 * Dao - 语言
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public interface LanguageDao extends BaseDao<Language, Long> {

	/**
	 * 查找所有的语言
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	List<Language> find();
	
}