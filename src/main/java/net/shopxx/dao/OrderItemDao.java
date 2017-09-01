/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;

/**
 * Dao - 订单项
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface OrderItemDao extends BaseDao<OrderItem, Long> {
	/**
	 * 根据订单id查找商品信息
	 * @param order
	 * @return
	 */
	List<OrderItem> findByOrderId(Order order);

}