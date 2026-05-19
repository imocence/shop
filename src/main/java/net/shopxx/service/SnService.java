package net.shopxx.service;

import net.shopxx.entity.Sn;

/**
 * Service - 序列号
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface SnService {

	/**
	 * 生成序列号
	 * 
	 * @param type
	 *            类型
	 * @return 序列号
	 */
	String generate(Sn.Type type);

}