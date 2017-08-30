/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Filter;
import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FriendLink;
import net.shopxx.service.CountryService;
import net.shopxx.service.FriendLinkService;
import net.shopxx.util.StringUtil;

/**
 * Controller - 友情链接
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminFriendLinkController")
@RequestMapping("/admin/friend_link")
public class FriendLinkController extends BaseController {

	@Inject
	private FriendLinkService friendLinkService;
	@Inject
	private CountryService countryService;
	
	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		model.addAttribute("types", FriendLink.Type.values());
		return "admin/friend_link/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(FriendLink friendLink, String countryName, RedirectAttributes redirectAttributes) {
		if (!isValid(friendLink)) {
			return ERROR_VIEW;
		}
		if (FriendLink.Type.text.equals(friendLink.getType())) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			return ERROR_VIEW;
		}
		friendLink.setCountry(countryService.findByName(countryName));
		friendLinkService.save(friendLink);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("types", FriendLink.Type.values());
		model.addAttribute("friendLink", friendLinkService.find(id));
		return "admin/friend_link/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(FriendLink friendLink, String countryName, RedirectAttributes redirectAttributes) {
		if (!isValid(friendLink)) {
			return ERROR_VIEW;
		}
		if (FriendLink.Type.text.equals(friendLink.getType())) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			return ERROR_VIEW;
		}
		friendLink.setCountry(countryService.findByName(countryName));
		friendLinkService.update(friendLink);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(String countryName, Pageable pageable, ModelMap model) {
		Country country = null;
		if (StringUtil.isNotEmpty(countryName)) {
			country = countryService.findByName(countryName);
		}
		model.addAttribute("countryName", countryName);
		if (null != country) {
			Filter filter = new Filter();
			filter.setProperty("country");
			filter.setValue(country);
			filter.setOperator(Filter.Operator.eq);
			pageable.getFilters().add(filter);
		}
		model.addAttribute("page", friendLinkService.findPage(pageable));
		return "admin/friend_link/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		friendLinkService.delete(ids);
		return Message.success(SUCCESS_MESSAGE);
	}

}