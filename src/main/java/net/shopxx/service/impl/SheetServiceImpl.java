/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.SheetDao;
import net.shopxx.dao.SnDao;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal.Type;
import net.shopxx.entity.Product;
import net.shopxx.entity.Sheet;
import net.shopxx.entity.SheetItem;
import net.shopxx.entity.Sku;
import net.shopxx.entity.Sn;
import net.shopxx.entity.StockLog;
import net.shopxx.entity.User;
import net.shopxx.entity.Sheet.Status;
import net.shopxx.service.ProductService;
import net.shopxx.service.SheetService;
import net.shopxx.service.SkuService;
import net.shopxx.service.UserService;
/**
 * Service - 订单
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class SheetServiceImpl extends BaseServiceImpl<Sheet, Long> implements SheetService {
	@Inject
	private SheetDao sheetDao;
	@Inject
	private SnDao snDao;
	@Inject
	private ProductService productService;
	@Inject
	private SkuService skuService;
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
	@Transactional(readOnly = true)
	public Page<Sheet> findPage(Status status, Admin admin, String modifyName,
			Country country, Boolean hasExpired, Pageable pageable){
		return sheetDao.findPage(status, admin, modifyName,
			country, hasExpired, pageable);
	}
	/**
	 * 创建入库单
	 * @param sheet
	 * @param currentUser
	 * 			创建人
	 * @param admin
	 * 			审核人
	 * @param object
	 * 			备用
	 * @return
	 */
	public boolean create(Sheet sheet, Admin currentUser, Admin admin, Object object){
		sheet.setAdmin(currentUser);//创建人
		sheet.setCreateName(currentUser.getUsername());
		sheet.setExpire(new Date());
		sheet.setAuditor(null);
		sheet.setModifyName(null);
		sheet.setSn(snDao.generate(Sn.Type.order));
		
		//设置入库单项
		List<SheetItem> sheetItems = new ArrayList<SheetItem>();
		List<SheetItem> curSheetItems = sheet.getSheetItems();
		if(null != curSheetItems){
			for(SheetItem curSheetItem : curSheetItems){
				if (null == curSheetItem.getQuantity() || curSheetItem.getQuantity() == 0 || null == curSheetItem.getSn()) {
					continue;
				}
				SheetItem sheetItem = new SheetItem();
				String sn = curSheetItem.getSn();
				Product product = productService.findBySn(sn);
				Sku sku = skuService.findBySn(sn);
				sheetItem.setSn(sn);
				sheetItem.setQuantity(curSheetItem.getQuantity());
				sheetItem.setName(product.getName());
				sheetItem.setType(product.getType());
				sheetItem.setSheet(sheet);
				sheetItem.setSku(sku);
				sheetItems.add(sheetItem);
			}
		}
		sheet.setSheetItems(sheetItems);
		sheetDao.persist(sheet);
		return true;
	}
	/**
	 * 更新
	 */
	public void modify(Sheet sheet){
		Assert.notNull(sheet);
		//设置入库单项
		List<SheetItem> sheetItems = new ArrayList<SheetItem>();
		List<SheetItem> curSheetItems = sheet.getSheetItems();
		if(null != curSheetItems){
			for(SheetItem curSheetItem : curSheetItems){
				if (null == curSheetItem.getQuantity() || curSheetItem.getQuantity() == 0 || null == curSheetItem.getSn()) {
					continue;
				}
				SheetItem sheetItem = new SheetItem();
				String sn = curSheetItem.getSn();
				Product product = productService.findBySn(sn);
				Sku sku = skuService.findBySn(sn);
				sheetItem.setSn(sn);
				sheetItem.setQuantity(curSheetItem.getQuantity());
				sheetItem.setName(product.getName());
				sheetItem.setType(product.getType());
				sheetItem.setSheet(sheet);
				sheetItem.setSku(sku);
				sheetItems.add(sheetItem);
			}
		}
		sheet.setSheetItems(sheetItems);
		sheetDao.persist(sheet);
	}
	/**
	 * 入库单入库
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean shippingReview(Set<Sheet> sheets,Admin admin,Sheet.Status status)throws Exception{
		if(sheets.size() > 0){
			for(Sheet sheet : sheets){
				if(status.name().equals("audited")){					
					List<SheetItem> sheetItems = sheet.getSheetItems();
					for(SheetItem sheetItem : sheetItems){
						String sn = sheetItem.getSn();
						Product product = productService.findBySn(sn);
						Set<Sku> skus = product.getSkus();
						for(Sku sku : skus){						
							skuService.addStock(sku, sheetItem.getQuantity(), StockLog.Type.stockIn, "");
						}
					}				
				}
				sheet.setStatus(status);
				sheet.setAuditor(admin.getUsername());
				update(sheet);
			}
		}		
		return true;
	}
}
