/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.entity.Country;
import net.shopxx.entity.PaymentMethod;
import net.shopxx.service.PaymentMethodService;
import net.shopxx.util.StringConstant;

/**
 * Service - 支付方式
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class PaymentMethodServiceImpl extends BaseServiceImpl<PaymentMethod, Long> implements PaymentMethodService {
	/**
	 * 根据国家获取支付方式
	 * @param country
	 * @return
	 */
	public List<PaymentMethod> findAll(Country country){
		List<Filter> filters = new ArrayList<Filter>();
		Filter filter = new Filter(StringConstant.COUNTRY, Filter.Operator.eq, country);
		filters.add(filter);
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(StringConstant.ORDER, Order.Direction.asc);
		orders.add(order);
		return super.findList(null, filters, orders);
	}
}