/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import net.shopxx.dao.OrderItemDao;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;
import net.shopxx.service.OrderItemService;

/**
 * Service - 订单项
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItem, Long> implements OrderItemService {
	@Inject
	OrderItemDao orderItemDao;
	/**
	 * 根据订单号查找商品信息
	 */
	public List<OrderItem> findByOrderId(Order order){
		return orderItemDao.findByOrderId(order);
	}
}