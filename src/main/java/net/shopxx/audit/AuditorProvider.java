package net.shopxx.audit;

/**
 * Audit - 审计者Provider
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface AuditorProvider<T> {

	/**
	 * 获取当前审计者
	 * 
	 * @return 当前审计者
	 */
	T getCurrentAuditor();

}