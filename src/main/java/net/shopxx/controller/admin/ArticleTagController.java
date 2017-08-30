/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.ArticleTag;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Country;
import net.shopxx.service.ArticleTagService;
import net.shopxx.service.CountryService;
import net.shopxx.util.StringUtil;

/**
 * Controller - 文章标签
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminArticleTagController")
@RequestMapping("/admin/article_tag")
public class ArticleTagController extends BaseController {

	@Inject
	private ArticleTagService articleTagService;
	@Inject
	private CountryService countryService;
	
	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		return "admin/article_tag/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(ArticleTag articleTag, String countryName, RedirectAttributes redirectAttributes) {
		if (!isValid(articleTag, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		articleTag.setArticles(null);
		articleTag.setCountry(countryService.findByName(countryName));
		articleTagService.save(articleTag);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("articleTag", articleTagService.find(id));
		return "admin/article_tag/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(ArticleTag articleTag, String countryName, RedirectAttributes redirectAttributes) {
		if (!isValid(articleTag)) {
			return ERROR_VIEW;
		}
		articleTag.setCountry(countryService.findByName(countryName));
		articleTagService.update(articleTag, "articles");
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
		model.addAttribute("page", articleTagService.findPage(country, pageable));
		return "admin/article_tag/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		articleTagService.delete(ids);
		return Message.success(SUCCESS_MESSAGE);
	}

}