package net.shopxx.service;

import org.springframework.transaction.annotation.Transactional;

import net.shopxx.entity.Member;
import net.shopxx.entity.NapaStores;
/**
 * Service - 人员关联的店铺
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface NapaStoresService extends BaseService<NapaStores, Long>{
	/**
	 * 根据区代编号查找区代
	 * 
	 * @param napaCode
	 *            区代编号
	 * @return 区代，若不存在则返回null
	 */
	@Transactional(readOnly = true)
	NapaStores findByNapaCode(String napaCode);
	/**
	 * 根据会员查找会员类型
	 * @param member
	 * @return
	 */
	NapaStores findByMember(Member member);
}
