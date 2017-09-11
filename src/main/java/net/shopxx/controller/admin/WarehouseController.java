package net.shopxx.controller.admin;

import java.util.List;

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
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Country;
import net.shopxx.entity.Warehouse;
import net.shopxx.service.CountryService;
import net.shopxx.service.WarehouseService;

/**
 * Controller - 仓库
 * 
 * @author cht
 * @version 1.0.0
 */
@Controller("adminWarehouseController")
@RequestMapping("/admin/warehouse")
public class WarehouseController extends BaseController {
	
	@Inject
	private WarehouseService warehouseService;
	
	@Inject
	private CountryService countryService;
	
	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		List<Country> countries = countryService.findRoots();
		model.addAttribute("countries", countries);
		if (countries != null && !countries.isEmpty()) {
	           Long countryId = countries.get(0).getId();
	           model.addAttribute("countryId", countryId);
	        }
		return "admin/warehouse/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(Warehouse warehouse, RedirectAttributes redirectAttributes) {
		if (!isValid(warehouse, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		warehouse.setCountry(countryService.find(warehouse.getCountry().getId()));
		warehouseService.save(warehouse);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("countries", countryService.findRoots());
		model.addAttribute("warehouse", warehouseService.find(id));
		return "admin/warehouse/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Warehouse warehouse, RedirectAttributes redirectAttributes) {
		if (!isValid(warehouse)) {
			return ERROR_VIEW;
		}
		warehouse.setCountry(countryService.find(warehouse.getCountry().getId()));
		warehouseService.update(warehouse);
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
		model.addAttribute("page", warehouseService.findPage(country,pageable));
		return "admin/warehouse/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		warehouseService.delete(ids);
		return Message.success(SUCCESS_MESSAGE);
	}
}
