package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.WarehouseDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.Warehouse;
import net.shopxx.service.WarehouseService;

/**
 * Service - 仓库
 * 
 * @author cht
 * @version 1.0.0
 */
@Service
public class WarehouseServiceImpl extends BaseServiceImpl<Warehouse, Long> implements WarehouseService{
	@Inject
	private WarehouseDao warehouseDao;

	@Transactional(readOnly = true)
	@Cacheable(value = "warehouse", condition = "#useCache")
	public List<Warehouse> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return warehouseDao.findList(null, count, filters, orders);
	}

	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public Warehouse save(Warehouse warehouse) {
		return super.save(warehouse);
	}

	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public Warehouse update(Warehouse warehouse) {
		return super.update(warehouse);
	}

	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public Warehouse update(Warehouse warehouse, String... ignoreProperties) {
		return super.update(warehouse, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "warehouse", allEntries = true)
	public void delete(Warehouse warehouse) {
		super.delete(warehouse);
	}

	@Override
	public Page<Warehouse> findPage(Country country, Pageable pageable) {
		return warehouseDao.findPage(country,pageable);
	}
}
