package net.shopxx.controller.admin;

import java.util.Date;

import javax.inject.Inject;

import net.shopxx.Order;
import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.service.CountryService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.util.StringUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller - 存折
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Controller("fiBankbookJournalController")
@RequestMapping("/admin/fiBankbookJournal")
public class FiBankbookJournalController extends BaseController {

	@Inject
	private FiBankbookJournalService fiBankbookJournalService;
	
	@Inject
	private CountryService countryService;
	
	/**
	 * 记录
	 */
	@GetMapping("/list")
	public String list(String countryName, FiBankbookJournal.Type type, FiBankbookJournal.MoneyType moneyType, Date beginDate, Date endDate, Pageable pageable, ModelMap model) {
		model.addAttribute("types", FiBankbookJournal.Type.values());
		model.addAttribute("moneyTypes", FiBankbookJournal.MoneyType.values());
		model.addAttribute("type", type);
		model.addAttribute("moneyType", moneyType);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		
		Country country = null;
		if (StringUtil.isNotEmpty(countryName)) {
			country = countryService.findByName(countryName);
		}
		// 增加默认排序
		if (null  == pageable.getOrderProperty()) {
			pageable.setOrderProperty("dealDate");
			pageable.setOrderDirection(Order.Direction.desc);
		}
		model.addAttribute("page", fiBankbookJournalService.findPage(country, type, moneyType, beginDate, endDate, pageable));
		model.addAttribute("countryName", countryName);
		return "admin/fiBankbookJournal/list";
	}

}