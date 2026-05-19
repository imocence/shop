package net.shopxx.dao.impl;

import org.springframework.stereotype.Repository;

import net.shopxx.dao.UserDao;
import net.shopxx.entity.User;

/**
 * Dao - 用户
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class UserDaoImpl extends BaseDaoImpl<User, Long> implements UserDao {

}