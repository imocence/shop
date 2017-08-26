package net.shopxx.controller.admin;

import java.util.Date;

import javax.inject.Inject;

import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournalTemp;
import net.shopxx.service.CountryService;
import net.shopxx.service.FiBankbookJournalTempService;
import net.shopxx.util.StringUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	/**
	 * 记录
	 */
	@GetMapping("/list")
	public String list(String countryName, FiBankbookJournalTemp.Type type, FiBankbookJournalTemp.MoneyType moneyType, FiBankbookJournalTemp.ConfirmStatus confirmStatus, Date beginDate, Date endDate, Pageable pageable, ModelMap model) {
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

}