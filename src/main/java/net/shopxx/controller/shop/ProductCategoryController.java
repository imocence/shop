/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.entity.Country;
import net.shopxx.service.CountryService;
import net.shopxx.service.ProductCategoryService;

/**
 * Controller - 商品分类
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("shopProductCategoryController")
@RequestMapping("/product_category")
public class ProductCategoryController extends BaseController {

	@Inject
	private ProductCategoryService productCategoryService;

	@Inject
    private CountryService countryService;
	
	/**
	 * 首页
	 */
	@GetMapping
	public String index(ModelMap model) {
	    Country  country = countryService.getDefaultCountry();
		model.addAttribute("rootProductCategories", productCategoryService.findRoots(country,null));
		return "shop/product_category/index";
	}

}