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
import net.shopxx.service.LanguageService;

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
}