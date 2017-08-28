/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.member;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Member;
import net.shopxx.entity.Order;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.ConsultationService;
import net.shopxx.service.CouponCodeService;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.MemberService;
import net.shopxx.service.MessageService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.service.OrderService;
import net.shopxx.service.ProductFavoriteService;
import net.shopxx.service.ProductNotifyService;
import net.shopxx.service.ReviewService;
import net.shopxx.util.TimeUtil;

/**
 * Controller - 首页
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("memberIndexController")
@RequestMapping("/member/index")
public class IndexController extends BaseController {

	/**
	 * 最新订单数量
	 */
	private static final int NEW_ORDER_SIZE = 6;

	@Inject
	private OrderService orderService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private MessageService messageService;
	@Inject
	private ProductFavoriteService productFavoriteService;
	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private ReviewService reviewService;
	@Inject
	private ConsultationService consultationService;
	@Value("${url.path}")
	private String urlPath;
	@Value("${url.signature}")
	private String urlSignature;
	@Inject
	private MemberService memberService;
	@Inject
	FiBankbookBalanceService fiBankbookBalanceService;
	@Inject
	NapaStoresService napaStoresService;
	/**
	 * 首页
	 */
	@GetMapping
	public String index(@CurrentUser Member currentUser, ModelMap model) {
		
		model.addAttribute("pendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, currentUser, null, null, null, null, null, null, false));
		model.addAttribute("shippedOrderCount", orderService.count(null, Order.Status.shipped, currentUser, null, null, null, null, null, null, null));
		model.addAttribute("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, currentUser, null, null, null, null, null, null, null));
		model.addAttribute("messageCount", messageService.count(currentUser, false));
		model.addAttribute("couponCodeCount", couponCodeService.count(null, currentUser, null, false, false));
		model.addAttribute("productFavoriteCount", productFavoriteService.count(currentUser));
		model.addAttribute("productNotifyCount", productNotifyService.count(currentUser, null, null, null));
		model.addAttribute("reviewCount", reviewService.count(currentUser, null, null, null));
		model.addAttribute("consultationCount", consultationService.count(currentUser, null, null));
		model.addAttribute("newOrders", orderService.findList(null, null, currentUser, null, null, null, null, null, null, null, NEW_ORDER_SIZE, null, null));

		//会员存折
		model.addAttribute("fiBankbookBalanceList", fiBankbookBalanceService.findList(currentUser,null,null,null));
		return "member/index";
	}

}