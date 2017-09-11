package net.shopxx.dao;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Member;
import net.shopxx.entity.RemittanceLog;

/**
 * Dao - 汇款登记
 * 
 * @author cht
 * @version 1.0.0
 */
public interface RemittanceLogDao extends BaseDao<RemittanceLog, Long>{
	/**
	 * 查找汇款登记分页
	 * 
	 * @param member
	 *            会员，null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 汇款登记分页
	 */
	Page<RemittanceLog> findPage(Member member, Pageable pageable);
}
