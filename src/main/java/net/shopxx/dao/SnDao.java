package net.shopxx.dao;

import net.shopxx.entity.Sn;

/**
 * Dao - 序列号
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface SnDao {

	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	String generate(Sn.Type type);

}