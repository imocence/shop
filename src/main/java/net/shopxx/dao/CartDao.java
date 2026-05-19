package net.shopxx.dao;

import net.shopxx.entity.Cart;

/**
 * Dao - 购物车
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface CartDao extends BaseDao<Cart, Long> {

	/**
	 * 删除过期购物车
	 */
	void deleteExpired();

}