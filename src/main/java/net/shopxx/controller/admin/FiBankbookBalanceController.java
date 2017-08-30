package net.shopxx.controller.admin;

import javax.inject.Inject;

import net.shopxx.Pageable;
import net.shopxx.entity.Country;
import net.shopxx.service.CountryService;
import net.shopxx.service.FiBankbookBalanceService;
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
@Controller("fiBankbookBalanceController")
@RequestMapping("/admin/fiBankbookBalance")
public class FiBankbookBalanceController extends BaseController {

	@Inject
	private FiBankbookBalanceService fiBankbookBalanceService;
	
	@Inject
	private CountryService countryService;
	
	/**
	 * 记录
	 */
	@GetMapping("/list")
	public String list(String countryName, Pageable pageable, ModelMap model) {
		Country country = null;
		if (StringUtil.isNotEmpty(countryName)) {
			country = countryService.findByName(countryName);
		}
		if (country != null) {
			model.addAttribute("page", fiBankbookBalanceService.findPage(null, country, pageable));
		} else {
			model.addAttribute("page", fiBankbookBalanceService.findPage(pageable));
		}
		model.addAttribute("countryName", countryName);
		return "admin/fiBankbookBalance/list";
	}

}