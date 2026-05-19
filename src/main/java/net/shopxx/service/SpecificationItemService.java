package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.SpecificationItem;

/**
 * Service - 规格项
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface SpecificationItemService {

	/**
	 * 规格项过滤
	 * 
	 * @param specificationItems
	 *            规格项
	 */
	void filter(List<SpecificationItem> specificationItems);

}