package net.shopxx.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import net.shopxx.dao.LanguageDao;
import net.shopxx.entity.Language;

import org.springframework.stereotype.Repository;

/**
 * Dao - 语言
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Repository
public class LanguageDaoImpl extends BaseDaoImpl<Language, Long> implements LanguageDao {

	public List<Language> find() {
		String jpql = "SELECT language FROM Language language WHERE language.state='1' ORDER BY language.order";
		TypedQuery<Language> query = entityManager.createQuery(jpql, Language.class);
		return query.getResultList();
	}
	
	/**
	 * 根据code获取语言
	 * @param code
	 * @return
	 */
	public Language findByCode(String code){
		String jpql = "SELECT language FROM Language language WHERE language.state='1' and language.code=:code";
		TypedQuery<Language> query = entityManager.createQuery(jpql, Language.class).setParameter("code", code);
		List<Language> list= query.getResultList();
		if (null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 根据locale获取语言
	 * @param locale
	 * @return
	 */
	public Language findByLocale(String locale){
		String jpql = "SELECT language FROM Language language WHERE language.state='1' and language.locale=:locale";
		TypedQuery<Language> query = entityManager.createQuery(jpql, Language.class).setParameter("locale", locale);
		List<Language> list= query.getResultList();
		if (null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}