package net.shopxx.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Ad;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournalTemp;
import net.shopxx.entity.Member;
import net.shopxx.entity.Role;
import net.shopxx.entity.Sku;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.CountryService;
import net.shopxx.service.FiBankbookJournalTempService;
import net.shopxx.service.MemberService;
import net.shopxx.util.PermissionUitl;
import net.shopxx.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 充值确认
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Controller("fiBankbookJournalTempController")
@RequestMapping("/admin/fiBankbookJournalTemp")
public class FiBankbookJournalTempController extends BaseController {

	@Inject
	private FiBankbookJournalTempService fiBankbookJournalTempService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private MemberService memberService;
	
	/**
	 * 记录
	 */
	@GetMapping("/list")
	public String list(@CurrentUser Admin currentUser, String countryName, FiBankbookJournalTemp.Type type, FiBankbookJournalTemp.MoneyType moneyType, FiBankbookJournalTemp.ConfirmStatus confirmStatus, Date beginDate, Date endDate, Pageable pageable, ModelMap model) {
		model.addAttribute("types", FiBankbookJournalTemp.Type.values());
		model.addAttribute("moneyTypes", FiBankbookJournalTemp.MoneyType.values());
		model.addAttribute("confirmStatuss", FiBankbookJournalTemp.ConfirmStatus.values());
		model.addAttribute("type", type);
		model.addAttribute("moneyType", moneyType);
		model.addAttribute("confirmStatus", confirmStatus);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		
		Country country = null;
		if (StringUtil.isNotEmpty(countryName)) {
			country = countryService.findByName(countryName);
		}
		model.addAttribute("page", fiBankbookJournalTempService.findPage(country, type, moneyType, confirmStatus, beginDate, endDate, pageable));
		model.addAttribute("countryName", countryName);
		model.addAttribute("isconfirm", PermissionUitl.isPermission(currentUser, PermissionUitl.JOURNAL_TEMP_CONFIRM));
		return "admin/fiBankbookJournalTemp/list";
	}
	
	/**
	 * 新增
	 */
	@GetMapping("/add")
	public String add(String countryName, FiBankbookJournalTemp.Type type, FiBankbookJournalTemp.MoneyType moneyType, FiBankbookJournalTemp.ConfirmStatus confirmStatus, Date beginDate, Date endDate, Pageable pageable, ModelMap model) {
		model.addAttribute("types", FiBankbookJournalTemp.Type.values());
		model.addAttribute("moneyTypes", FiBankbookJournalTemp.MoneyType.values());
		model.addAttribute("dealTypes", FiBankbookJournalTemp.DealType.values());
		return "admin/fiBankbookJournalTemp/add";
	}
	
	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(@CurrentUser Admin currentUser, String country, String usercode, FiBankbookJournalTemp.Type type, FiBankbookJournalTemp.DealType dealType, FiBankbookJournalTemp.MoneyType moneyType, BigDecimal money, String notes, RedirectAttributes redirectAttributes) {
		FiBankbookJournalTemp fiBankbookJournalTemp = new FiBankbookJournalTemp();
		fiBankbookJournalTemp.setType(type);
		fiBankbookJournalTemp.setDealType(dealType);
		fiBankbookJournalTemp.setMoneyType(moneyType);
		fiBankbookJournalTemp.setMoney(money);
		fiBankbookJournalTemp.setNotes(notes);
		fiBankbookJournalTemp.setConfirmStatus(FiBankbookJournalTemp.ConfirmStatus.unconfirmed);
		fiBankbookJournalTemp.setCreaterCode(String.valueOf(currentUser.getId()));
		fiBankbookJournalTemp.setCreaterName(currentUser.getUsername());
		if (!isValid(fiBankbookJournalTemp)) {
			return ERROR_VIEW;
		}
		// 获取member
		Member member = memberService.findByUsercode(usercode);
		if (null == member) {
			return ERROR_VIEW;
		}
		fiBankbookJournalTemp.setMember(member);
		// 获取国家
		Country countryBean = countryService.findByName(country);
		if (null == countryBean) {
			return ERROR_VIEW;
		}
		fiBankbookJournalTemp.setCountry(countryBean);
		fiBankbookJournalTempService.save(fiBankbookJournalTemp);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}
	
	/**
	 * 会员选择
	 */
	@GetMapping("/member_select")
	public @ResponseBody List<Map<String, Object>> memberSelect(@RequestParam("q") String keyword, @RequestParam("country") String country, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		// 获取国家
		Country countryBean = countryService.findByName(country);
		List<Member> members = memberService.search(keyword, countryBean, count);
		for (Member member : members) {
			Map<String, Object> item = new HashMap<>();
			item.put("code", member.getUsercode());
			item.put("name", member.getUsername());
			data.add(item);
		}
		return data;
	}
	
	/**
	 * 核实
	 */
	@PostMapping("/confirm")
	public @ResponseBody Message confirm(Long[] ids) {
		fiBankbookJournalTempService.confirm(ids);
		return Message.success(SUCCESS_MESSAGE);
	}

}