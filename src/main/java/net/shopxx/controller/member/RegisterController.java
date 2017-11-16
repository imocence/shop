/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.member;

import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Results;
import net.shopxx.Setting;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Language;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.entity.MemberRank;
import net.shopxx.entity.NapaStores;
import net.shopxx.entity.FiBankbookBalance.Type;
import net.shopxx.security.UserAuthenticationToken;
import net.shopxx.service.CountryService;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.LanguageService;
import net.shopxx.service.MemberAttributeService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.service.UserService;
import net.shopxx.util.SystemUtils;

/**
 * Controller - 会员注册
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("memberRegisterController")
@RequestMapping("/member/register")
public class RegisterController extends BaseController {

	@Inject
	private UserService userService;
	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private FiBankbookBalanceService fiBankbookBalanceService;
	@Inject
	private MemberAttributeService memberAttributeService;
	@Inject
	private NapaStoresService napaStoresService;
	@Inject
	private LanguageService languageService;
	@Inject
	private CountryService countryService;

	/**
	 * 检查用户名是否存在
	 */
	@GetMapping("/check_username")
	public @ResponseBody boolean checkUsername(String username) {
		return StringUtils.isNotEmpty(username) && !memberService.usernameExists(username);
	}

	/**
	 * 检查E-mail是否存在
	 */
	@GetMapping("/check_email")
	public @ResponseBody boolean checkEmail(String email) {
		return StringUtils.isNotEmpty(email) && !memberService.emailExists(email);
	}

	/**
	 * 检查手机是否存在
	 */
	@GetMapping("/check_mobile")
	public @ResponseBody boolean checkMobile(String mobile) {
		return StringUtils.isNotEmpty(mobile) && !memberService.mobileExists(mobile);
	}

	/**
	 * 注册页面
	 */
	@GetMapping
	public String index(ModelMap model) {
		model.addAttribute("genders", Member.Gender.values());
		return "member/register/index";
	}

	/**
	 * 注册提交
	 */
	@PostMapping("/submit")
	public ResponseEntity<?> submit(String username, String password, String email, String mobile, HttpServletRequest request) {
		Setting setting = SystemUtils.getSetting();
		if (BooleanUtils.isNotTrue(setting.getIsRegisterEnabled())) {
			return Results.unprocessableEntity("member.register.disabled");
		}
		if (!isValid(Member.class, "username", username, BaseEntity.Save.class) || !isValid(Member.class, "password", password, BaseEntity.Save.class) || !isValid(Member.class, "email", email, BaseEntity.Save.class) || !isValid(Member.class, "mobile", mobile, BaseEntity.Save.class)) {
			return Results.UNPROCESSABLE_ENTITY;
		}
		if (memberService.usernameExists(username)) {
			return Results.unprocessableEntity("member.register.usernameExist");
		}
		if (memberService.emailExists(email)) {
			return Results.unprocessableEntity("member.register.emailExist");
		}
		if (StringUtils.isNotEmpty(mobile) && memberService.mobileExists(mobile)) {
			return Results.unprocessableEntity("member.register.mobileExist");
		}

		Member member = new Member();
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				return Results.UNPROCESSABLE_ENTITY;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		
		//区代账号创建
		NapaStores napaStores = new NapaStores();
		napaStores.setMobile(mobile);
		napaStores.setNapaCode(null);
		napaStores.setType(0);
		napaStores.setBalance(BigDecimal.ZERO);
		napaStoresService.save(napaStores);
		member.setNapaStores(napaStores);

		member.setUsername(username);
		member.setUsercode(username);
		
		//设置国籍
		Language language = languageService.getDefaultLanguage();
		member.setLanguage(language);
		
		member.setPassword(password);
		member.setEncodedPassword(DigestUtils.md5Hex(password));
		
		member.setEmail(email);
		member.setIsEnabled(true);
		
		Country country = countryService.getDefaultCountry();
		member.setCountry(country);	
		MemberRank memberRank = memberRankService.findByCountry(country,MemberRank.Type.register);
		member.setMemberRank(memberRank);
		
		member.setMobile(mobile);
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setCouponAmount(BigDecimal.ZERO);
		member.setIsLocked(false);
		member.setLockDate(null);
		member.setLastLoginIp(request.getRemoteAddr());
		member.setLastLoginDate(new Date());
		member.setSafeKey(null);
		//member.setMemberRank(memberRankService.findDefault());
		member.setCart(null);
		member.setOrders(null);
		member.setPaymentTransactions(null);
		member.setDepositLogs(null);
		member.setCouponCodes(null);
		member.setReceivers(null);
		member.setReviews(null);
		member.setConsultations(null);
		member.setProductFavorites(null);
		member.setProductNotifies(null);
		member.setInMessages(null);
		member.setOutMessages(null);
		member.setPointLogs(null);
		userService.register(member);
		
		//创建会员的存折
		FiBankbookBalance balance1 = new FiBankbookBalance();
		balance1.setBalance(BigDecimal.ZERO);
		balance1.setType(Type.balance);
		balance1.setMember(member);
		balance1.setCountry(member.getCountry());
		fiBankbookBalanceService.save(balance1);
		FiBankbookBalance balance2 = new FiBankbookBalance();
		balance2.setBalance(BigDecimal.ZERO);
		balance2.setType(Type.coupon);
		balance2.setMember(member);
		balance2.setCountry(member.getCountry());
		fiBankbookBalanceService.save(balance2);
		
		userService.login(new UserAuthenticationToken(Member.class, username, password, false, request.getRemoteAddr()));
		return Results.ok("member.register.success");
	}

}