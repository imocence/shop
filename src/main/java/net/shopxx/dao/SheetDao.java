/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Country;
import net.shopxx.entity.Sheet;
import net.shopxx.entity.Sheet.Status;

/**
 * Dao - 入库单
 * 
 * @author sihao
 * @version 5.0.3
 */
public interface SheetDao extends BaseDao<Sheet, Long> {
	/**
	 * 
	 * @param status
	 * 			状态
	 * @param createName
	 * 			创建人姓名
	 * @param modifyName
	 * 			审核人姓名
	 * @param country
	 * 			国家
	 * @param hasExpired
	 * 			是否过期
	 * @param pageable
	 * 			分页信息
	 * @return
	 */
	Page<Sheet> findPage(Status status, Admin admin, String modifyName,
			Country country, Boolean hasExpired, Pageable pageable);

}