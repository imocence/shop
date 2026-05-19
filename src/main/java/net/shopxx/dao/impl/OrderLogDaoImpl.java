package net.shopxx.dao.impl;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.OrderLogDao;
import net.shopxx.entity.OrderLog;

/**
 * Dao - 订单记录
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class OrderLogDaoImpl extends BaseDaoImpl<OrderLog, Long> implements OrderLogDao {

}