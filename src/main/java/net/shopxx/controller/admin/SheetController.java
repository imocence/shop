package net.shopxx.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import net.shopxx.Filter;
import net.shopxx.Message;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Country;
import net.shopxx.entity.Product;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.Sheet;
import net.shopxx.entity.SheetItem;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.AdminService;
import net.shopxx.service.CountryService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.ProductService;
import net.shopxx.service.SheetService;
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
 * Controller - 入库制单
 * 
 * @author sihao
 * @version 5.0.3
 */
@Controller("adminSheetController")
@RequestMapping("/admin/sheet")
public class SheetController extends BaseController{
	@Inject
	private CountryService countryService;
	@Inject 
	private AdminService adminService;
	@Inject 
	private SheetService sheetService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private ProductService productService;
	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Long id,@CurrentUser Admin currentUser,SheetItem sheetItem,RedirectAttributes redirectAttributes){
		Sheet sheet = sheetService.find(id);
		if(null == sheet|| (!Sheet.Status.pendingReview.equals(sheet.getStatus()))){
			return ERROR_VIEW;
		}
		sheet.setModifyName(currentUser.getUsername());
		
		sheetService.modify(sheet);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "admin/sheet/log";
	}
	/**
	 * 记录
	 */
	@GetMapping("/log")
	public String log(String countryName,Pageable pageable,Sheet.Status statusName,String createName,String modifyName,String auditorName,Boolean hasExpired, ModelMap model) {
		model.addAttribute("statuss", Sheet.Status.values());
		model.addAttribute("status", statusName);
		model.addAttribute("createName", createName);
		model.addAttribute("modifyName", modifyName);
		model.addAttribute("auditorName", auditorName);
		model.addAttribute("hasExpired", hasExpired);
		
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
		Admin admin = adminService.findByUsername(createName);
		if(StringUtils.isNotEmpty(createName) && admin == null){
			model.addAttribute("page", Page.emptyPage(pageable));
		}else{
			model.addAttribute("page", sheetService.findPage(statusName,admin,modifyName,country,hasExpired,pageable));
		}
		return "admin/sheet/log";
	}
	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Sheet sheet = sheetService.find(id);
				if (null == sheet || (!Sheet.Status.pendingReview.equals(sheet.getStatus()))) {
					return Message.error("admin.order.deleteLockedNotAllowed", sheet.getSn());
				}
			}
			sheetService.delete(ids);
		}
		return Message.success(SUCCESS_MESSAGE);
	}
	/**
	 * 管理人员选择
	 */
	@GetMapping("/admin_select")
	public @ResponseBody List<Map<String, Object>> adminSelect(@RequestParam("q") String keyword, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		List<Admin> admins = adminService.search(keyword, count);
		for (Admin admin : admins) {
			Map<String, Object> item = new HashMap<>();
			item.put("adminId", admin.getId());
			item.put("name", admin.getName());
			item.put("username", admin.getUsername());
			data.add(item);
		}
		return data;
	}
	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model,@CurrentUser Admin currentUser) {
		model.addAttribute("admin", currentUser);
		return "admin/sheet/add";
	}
	/**
	 * 获取分类以及子分类下的商品列表
	 * @param productCategoryId 分类ID
	 */
	@GetMapping("/getProducts")
	public @ResponseBody List<Map<String, Object>> getProducts(Long productCategoryId) {
		List<Map<String, Object>> data = new ArrayList<>();
		// key:当前分类ID value:产品集合 
		Map<Long, Product> productMap = new LinkedHashMap<Long, Product>();
		// 获取当前分类下的所有的子分类id
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (null == productCategory) {
			return data;
		}

		Set<Product> set = productCategory.getProducts();
		if (null != set && !set.isEmpty()) {
			for (Product product : set) {
				if (!productMap.containsKey(product.getId())) {
					productMap.put(product.getId(), product);
				}
			}
		}
		List<ProductCategory> childrenCategorys = productCategoryService.findChildren(productCategoryId, true, null, true);
		if (null != childrenCategorys && !childrenCategorys.isEmpty()) {
			for (ProductCategory childCategory : childrenCategorys) {
				set = childCategory.getProducts();
				if (null != set && !set.isEmpty()) {
					for (Product product : set) {
						if (!productMap.containsKey(product.getId())) {
							productMap.put(product.getId(), product);
						}
					}
				}
			}
		}
		for (Product product : productMap.values()) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", product.getId());
			item.put("name", product.getName());
			data.add(item);
		}
		return data;
	}
	/**
	 * 获取商品详情
	 * @param productId 商品ID
	 */
	@GetMapping("/getProduct")
	public @ResponseBody Map<String, Object> getProduct(Long productId) {
		Map<String, Object> data = new HashMap<String, Object>();
		Product product = productService.find(productId);
		if (null == product) {
			return data;
		}
		data.put("sn", product.getSn());
		data.put("name", product.getName());
		data.put("image", product.getImage());
		data.put("sku", product.getDefaultSku().getAvailableStock());
		return data;
	}
	/**
	 * 添加入庫单
	 * @param sheet 入库单
	 * @param countryName 国家
	 * @param currentUser 创建人
	 * @param adminId 审核人id admin表
	 * @param sheetItem 订单项
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/save")
	public String save(Sheet sheet,String countryName,@CurrentUser Admin currentUser,SheetItem sheetItem,RedirectAttributes redirectAttributes){

		try {
			sheet.setCountry(countryService.findByName(countryName));
			sheet.setStatus(Sheet.Status.pendingReview);
			sheetService.create(sheet, currentUser,null,null);
		} catch (Exception e) {
			e.printStackTrace();
			addFlashMessage(redirectAttributes, new Message(Message.Type.error, e.getMessage()));
			return "redirect:log";
		}
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:log";
	}
	/**
	 * 审核或拒绝
	 */	
	@PostMapping("/review")
	public @ResponseBody Message review(Long id,@CurrentUser Admin currentUser,Sheet.Status status) {
		// 待审核状态  状态改为“审核”或者“拒绝”
		Sheet sheet = sheetService.find(id);
		Set<Sheet> sheets = new HashSet<Sheet>();
		sheets.add(sheet);
		try {
			sheetService.shippingReview(sheets,currentUser,status);//将入库单商品入库
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(Message.Type.error, e.getMessage());
		}
		return Message.success(SUCCESS_MESSAGE);
	}
	/**
	 * 审核
	 */
	@PostMapping("/shippingReview")
	public @ResponseBody Message shippingReview(Long[] ids,@CurrentUser Admin currentUser) {
		Set<Sheet> sheets = new HashSet<Sheet>();
		if (ids != null) {
			// 待审核状态  状态改为“审核”
			for (int i = 0;i < ids.length;i++) {
				Sheet sheet = sheetService.find(ids[i]);
				if(Sheet.Status.pendingReview.equals(sheet.getStatus())){
					sheets.add(sheet);
				}
			}
		}
		try {
			sheetService.shippingReview(sheets,currentUser,Sheet.Status.audited);//将入库单商品入库
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(Message.Type.error, e.getMessage());
		}
		return Message.success(SUCCESS_MESSAGE);
	}
	/**
	 * 拒绝
	 */
	@PostMapping("/returnsReview")
	public @ResponseBody Message returnsReview(Long[] ids,@CurrentUser Admin currentUser) {
		Set<Sheet> sheets = new HashSet<Sheet>();
		if (ids != null) {
			// 待审核状态  状态改为“拒绝”
			for (int i = 0;i < ids.length;i++) {
				Sheet sheet = sheetService.find(ids[i]);
				if(Sheet.Status.pendingReview.equals(sheet.getStatus())){
					sheets.add(sheet);
				}
			}
		}
		try {
			sheetService.shippingReview(sheets,currentUser,Sheet.Status.denied);//将入库单商品入库
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(Message.Type.error, e.getMessage());
		}
		return Message.success(SUCCESS_MESSAGE);
	}
	/**
	 * 查看
	 */
	@GetMapping("/view")
	public String view(Long id, ModelMap model) {
		Sheet sheet = sheetService.find(id);
		if (sheet == null) {
			return ERROR_VIEW;
		}
		Country country = sheet.getCountry();
		model.addAttribute("sheet", sheet);
		model.addAttribute("country", country);
		return "admin/sheet/view";
	}
}
