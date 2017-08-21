package net.shopxx.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.shopxx.entity.Language;
import net.shopxx.service.LanguageService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller - 语言
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Controller("commonLanguageController")
@RequestMapping("/common/language")
public class LanguageController {

	@Inject
	private LanguageService languageService;

	/**
	 * 语言
	 */
	@GetMapping
	public @ResponseBody List<Map<String, Object>> query() {
		List<Map<String, Object>> data = new ArrayList<>();
		List<Language> list = languageService.find();
		if (null != list) {
			for (Language language : list) {
				Map<String, Object> item = new HashMap<>();
				item.put("code", language.getCode());
				item.put("message", language.getMessage());
				data.add(item);
			}
		}
		return data;
	}

}