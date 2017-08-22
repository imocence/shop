package net.shopxx.controller.common;

import java.util.Locale;

import javax.inject.Inject;

import net.shopxx.Message;
import net.shopxx.entity.Language;
import net.shopxx.service.CacheService;
import net.shopxx.service.LanguageService;
import net.shopxx.util.WebUtils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Controller - 语言
 * 
 * @author gaoxiang
 * @version 5.0.3
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
			String[] locale = language.getLocale().split("_");
			if (locale.length == 2) {
				WebUtils.getRequest().getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale(locale[0], locale[1]));
				WebUtils.getRequest().getSession().setAttribute(CODE, language.getCode());
			}
		}
		cacheService.clearPage();
		return msg;
	}
	
	
}