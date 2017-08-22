package net.shopxx.interceptor;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shopxx.controller.common.LanguageController;
import net.shopxx.entity.Language;
import net.shopxx.service.LanguageService;
import net.shopxx.util.WebUtils;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor - 语言拦截器
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public class LanguageInterceptor extends HandlerInterceptorAdapter {

	@Inject
	private LanguageService languageService;
	
	/**
	 * 请求前处理
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param handler
	 *            处理器
	 * @return 是否继续执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String code = (String)WebUtils.getRequest().getSession().getAttribute(LanguageController.CODE);
		if (null == code) {
			Locale locale = Locale.getDefault();
			String localeStr = locale.getLanguage() + "_" + locale.getCountry();
			Language language = languageService.findByLocale(localeStr);
			if (null == language) {
				language = languageService.findByLocale(Locale.US.toString());
			}
			if (null != language) {
				WebUtils.getRequest().getSession().setAttribute(LanguageController.CODE, language.getCode());
			}
		}
		return super.preHandle(request, response, handler);
	}
}