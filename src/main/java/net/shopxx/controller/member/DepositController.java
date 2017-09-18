/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.member;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonView;

import net.shopxx.Pageable;
import net.shopxx.Results;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.FiBankbookJournal.Type;
import net.shopxx.entity.Member;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.MemberService;
import net.shopxx.service.PluginService;
import net.shopxx.util.TimeUtil;
import net.shopxx.util.WebUtils;

/**
 * Controller - 预存款
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("memberDepositController")
@RequestMapping("/member/deposit")
public class DepositController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private PluginService pluginService;
	@Inject
	FiBankbookJournalService fiBankbookJournalService;
	@Inject
	FiBankbookBalanceService fiBankbookBalanceService;
	@Inject
	MemberService memberService;
	/**
	 * 计算支付手续费
	 */
	@PostMapping("/calculate_fee")
	public ResponseEntity<?> calculateFee(String paymentPluginId, BigDecimal rechargeAmount) {
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null) {
			return Results.NOT_FOUND;
		}
		if (!paymentPlugin.getIsEnabled() || rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) < 0) {
			return Results.UNPROCESSABLE_ENTITY;
		}
		Map<String, Object> data = new HashMap<>();
		data.put("fee", paymentPlugin.calculateFee(rechargeAmount));
		return ResponseEntity.ok(data);
	}

	/**
	 * 检查余额
	 */
	@PostMapping("/check_balance")
	public ResponseEntity<?> checkBalance(@CurrentUser Member currentUser) {
		Map<String, Object> data = new HashMap<>();
		//data.put("balance", currentUser.getBalance());
		data.put("balance", fiBankbookBalanceService.find(currentUser, FiBankbookBalance.Type.balance).getBalance());
		return ResponseEntity.ok(data);
	}
	/**
	 * 检查余额
	 */
	@PostMapping("/check_coupon")
	public ResponseEntity<?> check_coupon(@CurrentUser Member currentUser) {
		Map<String, Object> data = new HashMap<>();
		//data.put("balance", currentUser.getBalance());
		data.put("coupon", fiBankbookBalanceService.find(currentUser, FiBankbookBalance.Type.coupon).getBalance());
		return ResponseEntity.ok(data);
	}
	/**
	 * 充值
	 */
	@GetMapping("/recharge")
	public String recharge(ModelMap model) {
		List<PaymentPlugin> paymentPlugins = pluginService.getActivePaymentPlugins(WebUtils.getRequest());
		if (!paymentPlugins.isEmpty()) {
			model.addAttribute("defaultPaymentPlugin", paymentPlugins.get(0));
			model.addAttribute("paymentPlugins", paymentPlugins);
		}
		return "member/deposit/recharge";
	}
	/**
	 * 转券
	 */
	@GetMapping("/gift")
	public String gift(ModelMap model,@CurrentUser Member currentUser) {
		model.addAttribute("coupon", fiBankbookBalanceService.find(currentUser, FiBankbookBalance.Type.coupon));
		return "member/deposit/gift";
	}
	/**
	 * 转券执行
	 * @throws Exception 
	 */
	@PostMapping("/gift_do")
	public String gift_do(String giftMemberCode,String name, BigDecimal giftAmount,@CurrentUser Member currentUser, RedirectAttributes redirectAttributes)  {
		if (!isValid(Member.class, "usercode", giftMemberCode) || !isValid(Member.class, "username", giftMemberCode) || !isValid(FiBankbookBalance.class, "balance", giftAmount)) {
			return "redirect:gift";
		}
		Member member = memberService.findByUsercode(StringUtils.upperCase(giftMemberCode.replace(" ", "")));
		if (giftMemberCode == null || member == null || !name.equals(member.getName())) {
			addFlashMessage(redirectAttributes, "member.deposit.sendSuccess");
			return "redirect:gift";
		}
		if(!member.getCountry().equals(currentUser.getCountry())){
			addFlashMessage(redirectAttributes, "member.deposit.notInSomeCountry");
			return "redirect:gift";
		}
		if (giftAmount != null && giftAmount.compareTo(BigDecimal.ZERO) < 0) {
			return "redirect:gift";
		}
		if (giftAmount != null && giftAmount.compareTo(fiBankbookBalanceService.find(currentUser,FiBankbookBalance.Type.coupon).getBalance()) > 0) {
			return "redirect:gift";
		}
		String outCoupon = "OUT"+currentUser.getUsercode().substring(4, 10)+TimeUtil.getFormatNowTime("yyyyMMddHHmmss");
		String inCoupon =  "IN"+member.getUsercode().substring(4, 10)+TimeUtil.getFormatNowTime("yyyyMMddHHmmss");
		//当前会员券减
		String outNotes = "用户编号[" + currentUser.getUsercode() + "] 操作编号[" + outCoupon + "] 赠送券" + giftAmount;
		String inNotes = "用户编号[" + member.getUsercode() + "] 操作编号[" + inCoupon + "] 获取赠送券" + giftAmount;
		try {
			fiBankbookJournalService.recharge(currentUser.getUsercode(), giftAmount, outCoupon, 
					FiBankbookJournal.Type.coupon, FiBankbookJournal.DealType.takeout, FiBankbookJournal.MoneyType.couponOut, outNotes);
			fiBankbookJournalService.recharge(member.getUsercode(), giftAmount, inCoupon, 
					FiBankbookJournal.Type.coupon, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.couponIn, inNotes);
		} catch (Exception e) {
			addFlashMessage(redirectAttributes, ERROR_MESSAGE);
			return "redirect:gift";
		}
		
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:log?type=1";	
	}
	/**
	 * 记录
	 */
	@GetMapping("/log")
	public String log(Integer pageNumber, @CurrentUser Member currentUser, ModelMap model,String type) {
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		//Page<DepositLog> depositLog = depositLogService.findPage(currentUser, pageable);
		//model.addAttribute("page", depositLogService.findPage(currentUser, pageable));
		//Page<FiBankbookJournal> FiBankbookJournal = fiBankbookJournalService.findPageByMemberId(currentUser, pageable);
		Type typeF = FiBankbookJournal.Type.balance;
		if("1".equals(type)){
			typeF = FiBankbookJournal.Type.coupon;
		}
		model.addAttribute("page", fiBankbookJournalService.findPageByMemberId(typeF,currentUser, pageable));
		model.addAttribute("type", type);
		if("1".equals(type)){
			return "member/deposit/coupon";
		}else{
			return "member/deposit/log";
		}
		
	}

	/**
	 * 记录
	 */
	@GetMapping(path = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
	@JsonView(BaseEntity.BaseView.class)
	public ResponseEntity<?> log(Integer pageNumber, @CurrentUser Member currentUser,String type) {
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		//List<DepositLog> depositLog = depositLogService.findPage(currentUser, pageable).getContent();
		//return ResponseEntity.ok(depositLog);
		Type typeF = FiBankbookJournal.Type.balance;
		if("1".equals(type)){
			typeF = FiBankbookJournal.Type.coupon;
		}
		List<FiBankbookJournal> fiBankbookJournal = fiBankbookJournalService.findPageByMemberId(typeF,currentUser, pageable).getContent();
		return ResponseEntity.ok(fiBankbookJournal);
	}

}