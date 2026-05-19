package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;

/**
 * Service - 订单项
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface OrderItemService extends BaseService<OrderItem, Long> {
	/**
	 * 根据订单号查询商品信息
	 * @param order
	 * @return
	 */
	List<OrderItem> findByOrderId(Order order);

}