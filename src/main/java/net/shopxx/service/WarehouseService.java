package net.shopxx.service;

import java.util.List;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.Warehouse;

/**
 * Service - 仓库
 * 
 * @author cht
 * @version 1.0.0
 */
public interface WarehouseService extends BaseService<Warehouse,Long>{
	/**
	 * 查找仓库
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 商品标签
	 */
	List<Warehouse> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache);

	Page<Warehouse> findPage(Country country, Pageable pageable);
}
