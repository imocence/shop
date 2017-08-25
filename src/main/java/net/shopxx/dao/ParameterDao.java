/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.Parameter;

/**
 * Dao - 参数
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface ParameterDao extends BaseDao<Parameter, Long> {

    Page<Parameter> findPage(Country country, Pageable pageable);

}