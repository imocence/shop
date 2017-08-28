/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.ParameterDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.Parameter;
import net.shopxx.service.ParameterService;

/**
 * Service - 参数
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class ParameterServiceImpl extends BaseServiceImpl<Parameter, Long> implements ParameterService {

    @Inject
    private ParameterDao parameterDao;
    
    @Override
    public Page<Parameter> findPage(Country country, Pageable pageable) {
        return parameterDao.findPage(country,pageable);
    }

}