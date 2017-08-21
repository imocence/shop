package net.shopxx.service.impl;

import java.util.List;

import javax.inject.Inject;

import net.shopxx.dao.LanguageDao;
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
	private LanguageDao languageDao;

	@Transactional(readOnly = true)
	public List<Language> find() {
		return languageDao.find();
	}
}