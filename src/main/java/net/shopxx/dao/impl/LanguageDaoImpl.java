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
		String jpql = "SELECT code,name,locale,message FROM `Language` WHERE state='1' ORDER BY orders";
		TypedQuery<Language> query = entityManager.createQuery(jpql, Language.class);
		return query.getResultList();
	}
}