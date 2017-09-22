package net.shopxx.service.impl;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.shopxx.controller.common.LanguageController;
import net.shopxx.dao.LanguageDao;
import net.shopxx.entity.Language;
import net.shopxx.entity.Member;
import net.shopxx.service.LanguageService;
import net.shopxx.util.PropertyUtil;
import net.shopxx.util.SpringUtils;
import net.shopxx.util.WebUtils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Service - 地区
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Service
public class LanguageServiceImpl extends BaseServiceImpl<Language, Long> implements LanguageService {

	@Inject
	private CacheManager cacheManager;
	@Inject
	private LanguageDao languageDao;

	@Transactional(readOnly = true)
	public List<Language> find() {
		Ehcache cache = cacheManager.getEhcache(Language.LANGUAGE_CACHE_NAME);
		String key = "list";
		Element element = cache.get(key);
		if (element != null) {
			return (List<Language>)element.getObjectValue();
		} else {
			List<Language> list = languageDao.find();
			cache.put(new Element(key, list));
			return list;
		}
	}
	
	public Language findByCode(String code){
		Ehcache cache = cacheManager.getEhcache(Language.LANGUAGE_CACHE_NAME);
		Element element = cache.get(code);
		if (element != null) {
			return (Language)element.getObjectValue();
		} else {
			Language language = languageDao.findByCode(code);
			cache.put(new Element(code, language));
			return language;
		}
	}
	
	public Language findByLocale(String locale){
		Ehcache cache = cacheManager.getEhcache(Language.LANGUAGE_CACHE_NAME);
		String key = "locale_" + locale;
		Element element = cache.get(key);
		if (element != null) {
			return (Language)element.getObjectValue();
		} else {
			Language language = languageDao.findByLocale(locale);
			cache.put(new Element(key, language));
			return language;
		}
	}
	
	@Override
	public Language getDefaultLanguage(){
		Subject s  = SecurityUtils.getSubject();
        Member currentUser = null;
        if (s.isAuthenticated() && s.getPrincipal() instanceof Member) {
            currentUser =  (Member) s.getPrincipal();
        }
		Language language = null;
		if(currentUser == null){
			String languageId =  PropertyUtil.getProperty("default.language.id");
			language = find(Long.valueOf(languageId));
		}else{
			language = currentUser.getLanguage();
		}
		return language;
	}
	
	public Language setLanguage(Language language){
		// 处理语言
		if (null == language) {
			LocaleResolver localeResolver = SpringUtils.getBean("localeResolver", LocaleResolver.class);
			Locale locale = localeResolver.resolveLocale(WebUtils.getRequest());
			if (null  == locale) {
				locale = Locale.getDefault();
			}
			String localeStr = locale.getLanguage() + "_" + locale.getCountry();
			language = findByLocale(localeStr);
			if (null == language) {
				language = findByLocale(Locale.US.toString());
			}
		}
		setLanguageSession(language);
		return language;
	}
	
	public void setLanguageSession(Language language){
		if (null != language) {
			String[] locale = language.getLocale().split("_");
			if (locale.length == 2) {
				WebUtils.getRequest().getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, new Locale(locale[0], locale[1]));
				WebUtils.getRequest().getSession().setAttribute(LanguageController.CODE, language.getCode());
				// session丢失的时候获取cookie的语言code
				WebUtils.addCookie(WebUtils.getRequest(), WebUtils.getResponse(), LanguageController.CODE, language.getCode());
			}
		}
	}
}