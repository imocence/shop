package net.shopxx.controller.common;

import javax.inject.Inject;

import net.shopxx.Message;
import net.shopxx.entity.Language;
import net.shopxx.service.CacheService;
import net.shopxx.service.LanguageService;
import net.shopxx.util.SpringUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 语言
 * 
 * @author gaoxiang
 * @version 1.0.0
 */
@Controller("commonLanguageController")
@RequestMapping("/common/language")
public class LanguageController {
	
	/**
	 * "code
	 */
	public static final String CODE = "languageCode";
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private CacheService cacheService;
	
	/**
	 * 语言
	 */
	@RequestMapping(path = "/change")
	public @ResponseBody String change(String code) {
		String msg = Message.success("common.message.success").getContent();
		Language language = languageService.findByCode(code);
		if (null != language && null != language.getLocale()) {
			languageService.setLanguageSession(language);
			cacheService.clearPage();
		}
		return msg;
	}
	
	/**
	 * 语言
	 */
	@RequestMapping(path = "/getMessage")
	public @ResponseBody String getMessage(String code) {
		return SpringUtils.getMessage(code);
	}
	
}