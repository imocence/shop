package net.shopxx.dao.impl;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.AuditLogDao;
import net.shopxx.entity.AuditLog;

/**
 * Dao - 审计日志
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class AuditLogDaoImpl extends BaseDaoImpl<AuditLog, Long> implements AuditLogDao {

	public void removeAll() {
		String jpql = "delete from AuditLog auditLog";
		entityManager.createQuery(jpql).executeUpdate();
	}

}