/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.Country;
import net.shopxx.entity.PaymentMethod;

/**
 * Service - 支付方式
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface PaymentMethodService extends BaseService<PaymentMethod, Long> {
	/**
	 * 根据国家获取支付方式
	 * @param country
	 * @return
	 */
	List<PaymentMethod> findAll(Country country);
}