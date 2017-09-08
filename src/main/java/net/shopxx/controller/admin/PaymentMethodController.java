/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.PaymentMethod;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.service.CountryService;
import net.shopxx.service.PaymentMethodService;
import net.shopxx.util.StringUtil;

/**
 * Controller - 支付方式
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminPaymentMethodController")
@RequestMapping("/admin/payment_method")
public class PaymentMethodController extends BaseController {

	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private CountryService countryService;
	
	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		model.addAttribute("types", PaymentMethod.Type.values());
		model.addAttribute("methods", PaymentMethod.Method.values());
		return "admin/payment_method/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(PaymentMethod paymentMethod, String countryName, RedirectAttributes redirectAttributes) {
		if (!isValid(paymentMethod)) {
			return ERROR_VIEW;
		}
		paymentMethod.setShippingMethods(null);
		paymentMethod.setOrders(null);
		paymentMethod.setCountry(countryService.findByName(countryName));
		paymentMethodService.save(paymentMethod);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("types", PaymentMethod.Type.values());
		model.addAttribute("methods", PaymentMethod.Method.values());
		model.addAttribute("paymentMethod", paymentMethodService.find(id));
		return "admin/payment_method/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(PaymentMethod paymentMethod, String countryName, RedirectAttributes redirectAttributes) {
		if (!isValid(paymentMethod)) {
			return ERROR_VIEW;
		}
		paymentMethod.setCountry(countryService.findByName(countryName));
		paymentMethodService.update(paymentMethod, "shippingMethods", "orders");
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(String countryName, Pageable pageable, ModelMap model) {
		countrySelect(countryName, pageable, model, countryService);
		model.addAttribute("page", paymentMethodService.findPage(pageable));
		return "admin/payment_method/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids.length >= paymentMethodService.count()) {
			return Message.error("admin.common.deleteAllNotAllowed");
		}
		paymentMethodService.delete(ids);
		return Message.success(SUCCESS_MESSAGE);
	}
	
	/**
	 * 根据国家选择支付方式
	 */
	@GetMapping("/listByCountry")
	public @ResponseBody String listByCountry(String countryName) {
		Country country = null;
		if (StringUtil.isNotEmpty(countryName)) {
			country = countryService.findByName(countryName);
		}
		List<PaymentMethod> list = paymentMethodService.findAll(country);
		JSONArray jsonArray = new JSONArray();
		if (null != list && !list.isEmpty()) {
			for (PaymentMethod bean : list) {
				JSONObject object = new JSONObject();
				object.put("id", bean.getId());
				object.put("name", bean.getName());
				jsonArray.add(object);
			}
		}
		return jsonArray.toJSONString();
	}
	
	/**
	 * 根据支付方式选择配送方式
	 */
	@GetMapping("/getShippingMethodListByPaymentMethod")
	public @ResponseBody JSONArray listByCountry(Long paymentMethodId) {
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		JSONArray jsonArray = new JSONArray();
		if (null != paymentMethod) {
			Set<ShippingMethod> set = paymentMethod.getShippingMethods();
			if (null != set && !set.isEmpty()) {
				for (ShippingMethod bean : set) {
					JSONObject object = new JSONObject();
					object.put("id", bean.getId());
					object.put("name", bean.getName());
					jsonArray.add(object);
				}
			}
		}
		return jsonArray;
	}
}