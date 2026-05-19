package net.shopxx.controller.shop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.entity.Member;
import net.shopxx.security.CurrentUser;

/**
 * Controller - 首页
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("shopIndexController")
@RequestMapping("/")
public class IndexController extends BaseController {

	/**
	 * 首页
	 */
	@GetMapping
	public String index(@CurrentUser Member currentUser,ModelMap model) {
	    model.addAttribute("currentUser", currentUser);
		return "shop/index";
	}
}