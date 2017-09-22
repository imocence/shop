package net.shopxx.interceptor;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shopxx.controller.common.LanguageController;
import net.shopxx.entity.Language;
import net.shopxx.entity.Member;
import net.shopxx.service.LanguageService;
import net.shopxx.service.UserService;
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
	@Inject
	private UserService userService;
	
	/**
	 * 请求前处理,设置languageCode到session中
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
			// 默认获取该用户的语言,session获取不到则从cookie获取，处理退出登录时session失效的场景
			Language language = null;
			code = WebUtils.getCookie(request, LanguageController.CODE);
			if (null != code) {
				language = languageService.findByCode(code);
			}
			if (null == language) {
				Member currentUser = userService.getCurrent(Member.class);
				if (currentUser != null && currentUser.getLanguage() != null) {
					language = currentUser.getLanguage();
				}
			}
			languageService.setLanguage(language);
		}
		return super.preHandle(request, response, handler);
	}
}