package net.shopxx.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.WarehouseDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.Warehouse;

/**
 * Dao - 仓库
 * 
 * @author cht
 * @version 1.0.0
 */
@Repository
public class WarehouseDaoImpl  extends BaseDaoImpl<Warehouse, Long> implements WarehouseDao {

	@Override
	public Page<Warehouse> findPage(Country country, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Warehouse> criteriaQuery = criteriaBuilder.createQuery(Warehouse.class);
        Root<Warehouse> root = criteriaQuery.from(Warehouse.class);
        criteriaQuery.select(root);
        Predicate restrictions = criteriaBuilder.conjunction();
        if (country != null) {
            restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("country"), country));
        }
        criteriaQuery.where(restrictions);
        return super.findPage(criteriaQuery, pageable);
	}

}
