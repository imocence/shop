/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.math.BigDecimal;

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
import net.shopxx.entity.Country;
import net.shopxx.entity.MemberRank;
import net.shopxx.service.CountryService;
import net.shopxx.service.MemberRankService;

/**
 * Controller - 会员等级
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminMemberRankController")
@RequestMapping("/admin/member_rank")
public class MemberRankController extends BaseController {
    
    @Inject
    private CountryService countryService;

	@Inject
	private MemberRankService memberRankService;

	/**
	 * 检查消费金额是否唯一
	 */
	@GetMapping("/check_amount")
	public @ResponseBody boolean checkAmount(Long id, BigDecimal amount) {
		return amount != null && memberRankService.amountUnique(id, amount);
	}

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
	    model.addAttribute("countries", countryService.findRoots());
	    model.addAttribute("types", MemberRank.Type.values());
		return "admin/member_rank/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(MemberRank memberRank, RedirectAttributes redirectAttributes) {
		if (!isValid(memberRank)) {
			return ERROR_VIEW;
		}
		memberRank.setAmount(new BigDecimal("1"));
		if (memberRank.getIsSpecial()) {
		} else if (memberRank.getAmount() == null) {//|| memberRankService.amountExists(memberRank.getAmount())
			return ERROR_VIEW;
		}
		memberRank.setCountry(countryService.find(memberRank.getCountry().getId()));
		memberRank.setMembers(null);
		memberRank.setPromotions(null);
		memberRankService.save(memberRank);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
	    model.addAttribute("countries", countryService.findRoots());
		model.addAttribute("memberRank", memberRankService.find(id));
		model.addAttribute("types", MemberRank.Type.values());
		return "admin/member_rank/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(MemberRank memberRank, Long id, RedirectAttributes redirectAttributes) {
		if (!isValid(memberRank)) {
			return ERROR_VIEW;
		}
		MemberRank pMemberRank = memberRankService.find(id);
		if (pMemberRank == null) {
			return ERROR_VIEW;
		}
		if (pMemberRank.getIsDefault()) {
			memberRank.setIsDefault(true);
		}
		/*if (memberRank.getIsSpecial()) {
			memberRank.setAmount(null);
		} else if (memberRank.getAmount() == null || !memberRankService.amountUnique(id, memberRank.getAmount())) {
			return ERROR_VIEW;
		}*/
		memberRank.setCountry(countryService.find(memberRank.getCountry().getId()));
		memberRankService.update(memberRank, "members", "promotions");
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable,Long countryId, ModelMap model) {
	    model.addAttribute("countries", countryService.findRoots());
        model.addAttribute("countryId", countryId);
        Country country = countryService.find(countryId);
		model.addAttribute("page", memberRankService.findPage(country,pageable));
		return "admin/member_rank/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				MemberRank memberRank = memberRankService.find(id);
				if (memberRank != null && memberRank.getMembers() != null && !memberRank.getMembers().isEmpty()) {
					return Message.error("admin.memberRank.deleteExistNotAllowed", memberRank.getName());
				}
			}
			long totalCount = memberRankService.count();
			if (ids.length >= totalCount) {
				return Message.error("admin.common.deleteAllNotAllowed");
			}
			memberRankService.delete(ids);
		}
		return Message.success(SUCCESS_MESSAGE);
	}

}