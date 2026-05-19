package net.shopxx.dao;

import net.shopxx.entity.AuditLog;

/**
 * Dao - 审计日志
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface AuditLogDao extends BaseDao<AuditLog, Long> {

	/**
	 * 删除所有
	 */
	void removeAll();

}