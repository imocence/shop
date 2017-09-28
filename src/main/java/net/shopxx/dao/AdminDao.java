/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;

import net.shopxx.entity.Admin;

/**
 * Dao - 管理员
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface AdminDao extends BaseDao<Admin, Long> {
	/**
	 * 根据名称查找管理人员
	 * @param keyword
	 * @param count
	 * @return
	 */
	List<Admin> search(String keyword, Integer count);

}