package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.shopxx.dao.AreaDao;
import net.shopxx.dao.LanguageDao;
import net.shopxx.entity.Area;
import net.shopxx.entity.Language;
import net.shopxx.entity.Member;
import net.shopxx.service.LanguageService;
import net.shopxx.util.PropertyUtil;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}