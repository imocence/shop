package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.ParameterValue;

/**
 * Service - 参数值
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface ParameterValueService {

	/**
	 * 参数值过滤
	 * 
	 * @param parameterValues
	 *            参数值
	 */
	void filter(List<ParameterValue> parameterValues);

}