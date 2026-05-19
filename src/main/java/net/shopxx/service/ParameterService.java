package net.shopxx.service;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.Parameter;

/**
 * Service - 参数
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface ParameterService extends BaseService<Parameter, Long> {

    Page<Parameter> findPage(Country country, Pageable pageable);

}