package net.shopxx.dao;

import java.util.List;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.Warehouse;

/**
 * Dao - 仓库
 * 
 * @author cht
 * @version 1.0.0
 */
public interface WarehouseDao  extends BaseDao<Warehouse, Long>{

	Page<Warehouse> findPage(Country country, Pageable pageable);

}
