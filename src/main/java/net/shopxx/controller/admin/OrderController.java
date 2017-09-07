/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import net.shopxx.Filter;
import net.shopxx.Message;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.Setting;
import net.shopxx.entity.Admin;
import net.shopxx.entity.Area;
import net.shopxx.entity.Country;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.Invoice;
import net.shopxx.entity.Member;
import net.shopxx.entity.NapaStores;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;
import net.shopxx.entity.OrderPayment;
import net.shopxx.entity.OrderRefunds;
import net.shopxx.entity.OrderReturns;
import net.shopxx.entity.OrderReturnsItem;
import net.shopxx.entity.OrderShipping;
import net.shopxx.entity.OrderShippingItem;
import net.shopxx.entity.PaymentMethod;
import net.shopxx.entity.Product;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.ProductGrade;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.entity.Sku;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.AreaService;
import net.shopxx.service.CountryService;
import net.shopxx.service.DeliveryCorpService;
import net.shopxx.service.MemberService;
import net.shopxx.service.OrderService;
import net.shopxx.service.OrderShippingService;
import net.shopxx.service.PaymentMethodService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.ProductService;
import net.shopxx.service.ShippingMethodService;
import net.shopxx.util.NumberUtil;
import net.shopxx.util.StringUtil;
import net.shopxx.util.SystemUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Controller - 订单
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController extends BaseController {

	@Inject
	private AreaService areaService;
	@Inject
	private OrderService orderService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private DeliveryCorpService deliveryCorpService;
	@Inject
	private OrderShippingService orderShippingService;
	@Inject
	private MemberService memberService;
	@Inject
	private CountryService countryService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private ProductService productService;
	
	/**
	 * 获取订单锁
	 */
	@PostMapping("/acquire_lock")
	public @ResponseBody boolean acquireLock(Long id) {
		Order order = orderService.find(id);
		return order != null && orderService.acquireLock(order);
	}

	/**
	 * 计算
	 */
	@PostMapping("/calculate")
	public @ResponseBody Map<String, Object> calculate(Long id, BigDecimal freight, BigDecimal tax, BigDecimal offsetAmount) {
		Map<String, Object> data = new HashMap<>();
		Order order = orderService.find(id);
		if (order == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("amount", orderService.calculateAmount(order.getPrice(), order.getFee(), freight, tax, order.getPromotionDiscount(), order.getCouponDiscount(), offsetAmount));
		return data;
	}

	/**
	 * 物流动态
	 */
	@GetMapping("/transit_step")
	public @ResponseBody Map<String, Object> transitStep(Long orderShippingId) {
		Map<String, Object> data = new HashMap<>();
		OrderShipping orderShipping = orderShippingService.find(orderShippingId);
		if (orderShipping == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(setting.getKuaidi100Customer()) || StringUtils.isEmpty(orderShipping.getDeliveryCorpCode()) || StringUtils.isEmpty(orderShipping.getTrackingNo())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("transitSteps", orderShippingService.getTransitSteps(orderShipping));
		return data;
	}
	
	/**
	 * 新增
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
//		List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
//		List<ShippingMethod> shippingMethods = shippingMethodService.findAll();
//		PaymentMethod defaultPaymentMethod = CollectionUtils.isNotEmpty(paymentMethods) ? paymentMethods.get(0) : null;
//		ShippingMethod defaultShippingMethod = null;
//		if (defaultPaymentMethod != null) {
//			for (ShippingMethod shippingMethod : shippingMethods) {
//				if (CollectionUtils.isNotEmpty(shippingMethod.getPaymentMethods()) && shippingMethod.getPaymentMethods().contains(defaultPaymentMethod)) {
//					defaultShippingMethod = shippingMethod;
//					break;
//				}
//			}
//		}
//		model.addAttribute("paymentMethods", paymentMethods);
//		model.addAttribute("shippingMethods", shippingMethods);
//		model.addAttribute("defaultPaymentMethod", defaultPaymentMethod);
//		model.addAttribute("defaultShippingMethod", defaultShippingMethod);
		return "admin/order/add";
	}
	
	/**
	 * 添加订单
	 * @param order 订单
	 * @param usercode 会员usercode
	 * @param consignee 收货人
	 * @param phone 收货电话
	 * @param address 收货地址
	 * @param paymentMethod 支付ID
	 * @param shippingMethod 配送ID
	 * @param memo 附言
	 * @param freight 运费
	 * @param balance 订单电子币总额
	 * @param couponbalance 订单购物券总额
	 * @param orderItem 订单项
	 * @param currentUser 当前登录用户
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/save")
	public String save(Order order, String usercode, String consignee, String phone, String address, Long paymentMethodId, Long shippingMethodId, String memo, BigDecimal freight, BigDecimal amount, BigDecimal couponAmount, @CurrentUser Admin currentUser, RedirectAttributes redirectAttributes){
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		Member member = memberService.findByUsercode(usercode);
		if (null == member) {
			addFlashMessage(redirectAttributes, new Message(Message.Type.error, "用户不存在"));
			return "redirect:list";
		}
		if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
			addFlashMessage(redirectAttributes, new Message(Message.Type.error, "订单电子劵金额非法"));
			return "redirect:list";
		}
		if (couponAmount != null && couponAmount.compareTo(BigDecimal.ZERO) < 0) {
			addFlashMessage(redirectAttributes, new Message(Message.Type.error, "订单购物券金额非法"));
			return "redirect:list";
		}
		try {
			orderService.create(order, member, paymentMethod, shippingMethod, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			addFlashMessage(redirectAttributes, new Message(Message.Type.error, e.getMessage()));
			return "redirect:list";
		}
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}
	
	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatus()) && !Order.Status.pendingReview.equals(order.getStatus()))) {
			return ERROR_VIEW;
		}
		model.addAttribute("paymentMethods", paymentMethodService.findAll());
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		model.addAttribute("order", order);
		return "admin/order/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Long id, Long areaId, Long paymentMethodId, Long shippingMethodId, BigDecimal freight, BigDecimal tax, BigDecimal offsetAmount, Long rewardPoint, String consignee, String address, String zipCode, String phone, String invoiceTitle, String memo,
			RedirectAttributes redirectAttributes) {
		Area area = areaService.find(areaId);
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);

		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatus()) && !Order.Status.pendingReview.equals(order.getStatus()))) {
			return ERROR_VIEW;
		}
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		order.setTax(invoice != null ? tax : BigDecimal.ZERO);
		order.setOffsetAmount(offsetAmount);
		order.setRewardPoint(rewardPoint);
		order.setMemo(memo);
		order.setInvoice(invoice);
		order.setPaymentMethod(paymentMethod);
		if (order.getIsDelivery()) {
			order.setFreight(freight);
			order.setConsignee(consignee);
			order.setAddress(address);
			order.setZipCode(zipCode);
			order.setPhone(phone);
			order.setArea(area);
			order.setShippingMethod(shippingMethod);
			if (!isValid(order, Order.Delivery.class)) {
				return ERROR_VIEW;
			}
		} else {
			order.setFreight(BigDecimal.ZERO);
			order.setConsignee(null);
			order.setAreaName(null);
			order.setAddress(null);
			order.setZipCode(null);
			order.setPhone(null);
			order.setShippingMethodName(null);
			order.setArea(null);
			order.setShippingMethod(null);
			if (!isValid(order)) {
				return ERROR_VIEW;
			}
		}
		orderService.modify(order);

		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 查看
	 */
	@GetMapping("/view")
	public String view(Long id, ModelMap model) {
		Setting setting = SystemUtils.getSetting();
		model.addAttribute("orderPaymentMethods", OrderPayment.Method.values());
		model.addAttribute("orderRefundsMethods", OrderRefunds.Method.values());
		model.addAttribute("paymentMethods", paymentMethodService.findAll());
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", deliveryCorpService.findAll());
		model.addAttribute("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()) && StringUtils.isNotEmpty(setting.getKuaidi100Customer()));
		model.addAttribute("order", orderService.find(id));
		return "admin/order/view";
	}

	/**
	 * 审核
	 */
	@PostMapping("/review")
	public String review(Long id, Boolean passed, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || !Order.Status.pendingReview.equals(order.getStatus())) {
			return ERROR_VIEW;
		}
		orderService.review(order, passed);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + id;
	}

	/**
	 * 收款
	 */
	@PostMapping("/payment")
	public String payment(OrderPayment orderPayment, Long orderId, Long paymentMethodId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		orderPayment.setOrder(order);
		orderPayment.setPaymentMethod(paymentMethodService.find(paymentMethodId));
		if (!isValid(orderPayment)) {
			return ERROR_VIEW;
		}
		Member member = order.getMember();
		if (OrderPayment.Method.deposit.equals(orderPayment.getMethod()) && orderPayment.getAmount().compareTo(member.getBalance()) > 0) {
			return ERROR_VIEW;
		}
		orderPayment.setFee(BigDecimal.ZERO);
		try {
			orderService.payment(order, orderPayment);
		} catch (Exception e) {
			e.printStackTrace();
		}
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 退款
	 */
	@PostMapping("/refunds")
	public String refunds(OrderRefunds orderRefunds, Long orderId, Long paymentMethodId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.getRefundableAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return ERROR_VIEW;
		}
		orderRefunds.setOrder(order);
		orderRefunds.setPaymentMethod(paymentMethodService.find(paymentMethodId));
		if (!isValid(orderRefunds)) {
			return ERROR_VIEW;
		}
		orderService.refunds(order, orderRefunds);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 发货
	 */
	@PostMapping("/shipping")
	public String shipping(OrderShipping orderShipping, Long orderId, Long shippingMethodId, Long deliveryCorpId, Long areaId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.getShippableQuantity() <= 0) {
			return ERROR_VIEW;
		}
		boolean isDelivery = false;
		for (Iterator<OrderShippingItem> iterator = orderShipping.getOrderShippingItems().iterator(); iterator.hasNext();) {
			OrderShippingItem orderShippingItem = iterator.next();
			if (orderShippingItem == null || StringUtils.isEmpty(orderShippingItem.getSn()) || orderShippingItem.getQuantity() == null || orderShippingItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(orderShippingItem.getSn());
			if (orderItem == null || orderShippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				return ERROR_VIEW;
			}
			Sku sku = orderItem.getSku();
			if (sku != null && orderShippingItem.getQuantity() > sku.getStock()) {
				return ERROR_VIEW;
			}
			orderShippingItem.setName(orderItem.getName());
			orderShippingItem.setIsDelivery(orderItem.getIsDelivery());
			orderShippingItem.setSku(sku);
			orderShippingItem.setOrderShipping(orderShipping);
			orderShippingItem.setSpecifications(orderItem.getSpecifications());
			if (orderItem.getIsDelivery()) {
				isDelivery = true;
			}
		}
		orderShipping.setOrder(order);
		orderShipping.setShippingMethod(shippingMethodService.find(shippingMethodId));
		orderShipping.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		orderShipping.setArea(areaService.find(areaId));
		if (isDelivery) {
			if (!isValid(orderShipping, OrderShipping.Delivery.class)) {
				return ERROR_VIEW;
			}
		} else {
			orderShipping.setShippingMethod((String) null);
			orderShipping.setDeliveryCorp((String) null);
			orderShipping.setDeliveryCorpUrl(null);
			orderShipping.setDeliveryCorpCode(null);
			orderShipping.setTrackingNo(null);
			orderShipping.setFreight(null);
			orderShipping.setConsignee(null);
			orderShipping.setArea((String) null);
			orderShipping.setAddress(null);
			orderShipping.setZipCode(null);
			orderShipping.setPhone(null);
			if (!isValid(orderShipping)) {
				return ERROR_VIEW;
			}
		}
		orderService.shipping(order, orderShipping);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 退货
	 */
	@PostMapping("/returns")
	public String returns(OrderReturns orderReturns, Long orderId, Long shippingMethodId, Long deliveryCorpId, Long areaId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.getReturnableQuantity() <= 0) {
			return ERROR_VIEW;
		}
		for (Iterator<OrderReturnsItem> iterator = orderReturns.getOrderReturnsItems().iterator(); iterator.hasNext();) {
			OrderReturnsItem orderReturnsItem = iterator.next();
			if (orderReturnsItem == null || StringUtils.isEmpty(orderReturnsItem.getSn()) || orderReturnsItem.getQuantity() == null || orderReturnsItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(orderReturnsItem.getSn());
			if (orderItem == null || orderReturnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				return ERROR_VIEW;
			}
			orderReturnsItem.setName(orderItem.getName());
			orderReturnsItem.setOrderReturns(orderReturns);
			orderReturnsItem.setSpecifications(orderItem.getSpecifications());
		}
		orderReturns.setOrder(order);
		orderReturns.setShippingMethod(shippingMethodService.find(shippingMethodId));
		orderReturns.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		orderReturns.setArea(areaService.find(areaId));
		if (!isValid(orderReturns)) {
			return ERROR_VIEW;
		}
		orderService.returns(order, orderReturns);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 收货
	 */
	@PostMapping("/receive")
	public String receive(Long id, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || !Order.Status.shipped.equals(order.getStatus())) {
			return ERROR_VIEW;
		}
		orderService.receive(order);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + id;
	}

	/**
	 * 完成
	 */
	@PostMapping("/complete")
	public String complete(Long id, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || !Order.Status.received.equals(order.getStatus())) {
			return ERROR_VIEW;
		}
		orderService.complete(order);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + id;
	}

	/**
	 * 失败
	 */
	@PostMapping("/fail")
	public String fail(Long id, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || (!Order.Status.pendingShipment.equals(order.getStatus()) && !Order.Status.shipped.equals(order.getStatus()) && !Order.Status.received.equals(order.getStatus()))) {
			return ERROR_VIEW;
		}
		orderService.fail(order);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:view?id=" + id;
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(String countryName, Order.Type type, Order.Status status, String memberUsername, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable, ModelMap model) {
		model.addAttribute("types", Order.Type.values());
		model.addAttribute("statuses", Order.Status.values());
		model.addAttribute("type", type);
		model.addAttribute("status", status);
		model.addAttribute("memberUsername", memberUsername);
		model.addAttribute("isPendingReceive", isPendingReceive);
		model.addAttribute("isPendingRefunds", isPendingRefunds);
		model.addAttribute("isAllocatedStock", isAllocatedStock);
		model.addAttribute("hasExpired", hasExpired);
		
		Country country = null;
		if (StringUtil.isNotEmpty(countryName)) {
			country = countryService.findByName(countryName);
		}
		model.addAttribute("countryName", countryName);
		if (null != country) {
			Filter filter = new Filter();
			filter.setProperty("country");
			filter.setValue(country);
			filter.setOperator(Filter.Operator.eq);
			pageable.getFilters().add(filter);
		}
		
		Member member = memberService.findByUsername(memberUsername);
		if (StringUtils.isNotEmpty(memberUsername) && member == null) {
			model.addAttribute("page", Page.emptyPage(pageable));
		} else {
			model.addAttribute("page", orderService.findPage(type, status, member, null, isPendingReceive, isPendingRefunds, null, null, isAllocatedStock, hasExpired, pageable));
		}
		return "admin/order/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Order order = orderService.find(id);
				if (order != null && !orderService.acquireLock(order)) {
					return Message.error("admin.order.deleteLockedNotAllowed", order.getSn());
				}
			}
			orderService.delete(ids);
		}
		return Message.success(SUCCESS_MESSAGE);
	}
	
	/**
	 * 会员选择
	 */
	@GetMapping("/member_select")
	public @ResponseBody List<Map<String, Object>> memberSelect(@RequestParam("q") String keyword, @RequestParam("country") String country, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		// 获取国家
		Country countryBean = countryService.findByName(country);
		List<Member> members = memberService.search(keyword, countryBean, count);
		for (Member member : members) {
			// 会员编号、姓名、余额、收货地址、手机号、会员角色
			Map<String, Object> item = new HashMap<>();
			item.put("usercode", member.getUsercode());
			item.put("username", member.getUsername());
			BigDecimal balance = new BigDecimal("0.00");
			BigDecimal couponbalance = new BigDecimal("0.00");
			Set<FiBankbookBalance> set = member.getFiBankbookBalances();
			if (null != set) {
				for (FiBankbookBalance fiBankbookBalance : set) {
					if (FiBankbookBalance.Type.balance == fiBankbookBalance.getType()) {
						balance = fiBankbookBalance.getBalance();
					}else if (FiBankbookBalance.Type.coupon == fiBankbookBalance.getType()) {
						couponbalance = fiBankbookBalance.getBalance();
					}
				}
			}
			item.put("balance", balance);
			item.put("couponbalance", couponbalance);
			String address = "";
			String phone = "";
			int memberrole = 0;
			NapaStores napaStores = member.getNapaStores();
			if (null != napaStores) {
				address = napaStores.getNapaAddress();
				phone = napaStores.getMobile();
				memberrole = napaStores.getType();
			}
			item.put("address", address);
			item.put("phone", phone);
			item.put("memberrole", memberrole);
			data.add(item);
		}
		return data;
	}
	
	/**
	 * 获取分类以及子分类下的商品列表
	 * @param productCategoryId 分类ID
	 */
	@GetMapping("/getProducts")
	public @ResponseBody List<Map<String, Object>> getProducts(String usercode, Long productCategoryId) {
		List<Map<String, Object>> data = new ArrayList<>();
		// key:当前分类ID value:产品集合 
		Map<Long, Product> productMap = new LinkedHashMap<Long, Product>();
		// 获取当前分类下的所有的子分类id
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (null == productCategory) {
			return data;
		}
		Member member = memberService.findByUsercode(usercode);
		Long memberRankId = member.getMemberRank().getId();
		Set<Product> set = productCategory.getProducts();
		if (null != set && !set.isEmpty()) {
			for (Product product : set) {
				// 判断库存，没有的不显示，没有劵价格的不显示
				if (!canSelect(product, memberRankId)){
					continue;
				}
				if (!productMap.containsKey(product.getId())) {
					productMap.put(product.getId(), product);
				}
			}
		}
		List<ProductCategory> childrenCategorys = productCategoryService.findChildren(productCategoryId, true, null, true);
		if (null != childrenCategorys && !childrenCategorys.isEmpty()) {
			for (ProductCategory childCategory : childrenCategorys) {
				set = childCategory.getProducts();
				if (null != set && !set.isEmpty()) {
					for (Product product : set) {
						// 判断库存，没有的不显示，没有劵价格的不显示
						if (!canSelect(product, memberRankId)){
							continue;
						}
						if (!productMap.containsKey(product.getId())) {
							productMap.put(product.getId(), product);
						}
					}
				}
			}
		}
		for (Product product : productMap.values()) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", product.getId());
			item.put("name", product.getName());
			data.add(item);
		}
		return data;
	}
	
	/**
	 * 获取商品详情
	 * @param productId 商品ID
	 */
	@GetMapping("/getProduct")
	public @ResponseBody Map<String, Object> getProduct(String usercode, Long productId) {
		Map<String, Object> data = new HashMap<String, Object>();
		Product product = productService.find(productId);
		if (null == product) {
			return data;
		}
		Member member = memberService.findByUsercode(usercode);
		Long memberRankId = member.getMemberRank().getId();
		Set<ProductGrade> grades = product.getProductGrades();
		if (null != grades) {
			for (ProductGrade productGrade : grades) {
				if (productGrade.getGrade().getId() == memberRankId) {
					data.put("price", productGrade.getPrice());
					data.put("couponPrice", productGrade.getCoupon());
				}
			}
		}
		data.put("sn", product.getSn());
		data.put("name", product.getName());
		data.put("weight", NumberUtil.getInt(product.getWeight()));
		data.put("image", product.getImage());
		data.put("sku", product.getDefaultSku().getAvailableStock());
		return data;
	}
	
	/**
	 * 获取运费
	 * @param productId 商品ID
	 */
	@GetMapping("/getFreight")
	public @ResponseBody BigDecimal getFreight(Long shippingMethodId, Integer weight) {
		if (null == shippingMethodId) {
			return new BigDecimal(0);
		}
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		if (null == shippingMethod) {
			return new BigDecimal(0);
		}
		Area area  = null;
		return shippingMethodService.calculateFreight(shippingMethod, area, weight);
	}
	
	/**
	 * 暂时不用，预留
	 * 获取所有的二级分类以及分类下的商品列表
	 * [{"name":"1级分类名称", products:[{product},{product}]}]
	 */
	@GetMapping("/getProductsBuCountry")
	public @ResponseBody String getProductsBuCountry(String countryName) {
		Country country = null;
		if (StringUtil.isNotEmpty(countryName)) {
			country = countryService.findByName(countryName);
		}
		// 根据国家获取国家下的1级分类
		List<ProductCategory> categorys = productCategoryService.findRoots(country, null);
		// key:下级分类ID value:1级分类ID 
		Map<Long, Long> childrenCategoryIdMap = new HashMap<Long, Long>();
		// key:1级分类ID value:产品集合 
		Map<Long, List<Product>> productMap = new HashMap<Long, List<Product>>();
		// 获取1级分类下的所有的子分类id
		for (ProductCategory productCategory : categorys) {
			List<ProductCategory> childrenCategorys = productCategoryService.findChildren(productCategory, true, null);
			for (ProductCategory childCategory : childrenCategorys) {
				childrenCategoryIdMap.put(childCategory.getId(), productCategory.getId());
			}
		}
		// 根据国家获取国家下的所有的商品，并放入分类下的商品中
		List<Product> products = productService.findList(country);
		for (Product product : products) {
			Long categoryId = product.getProductCategory().getId();
			// 获取1级分类
			Long parentCategoryId = childrenCategoryIdMap.get(categoryId);
			if (null != parentCategoryId) {
				// 1级分类以及所有子分类下的商品集合
				List<Product> productList = productMap.get(parentCategoryId);
				if (null == productList) {
					productList = new ArrayList<Product>();
					productMap.put(parentCategoryId, productList);
				}
				productList.add(product);
			}
		}
		
		// 构建json
		JSONArray array = new JSONArray();
		for (ProductCategory productCategory : categorys) {
			JSONObject categoryObj = new JSONObject();
			Long categoryId = productCategory.getId();
			JSONArray productArray = new JSONArray();
			List<Product> productList = productMap.get(categoryId);
			if (null != productList) {
				for (Product product : productList) {
					JSONObject productObj = new JSONObject();
					productObj.put("sn", product.getSn());
					productObj.put("name", product.getName());
					// 缺少库存参数
					productArray.add(productObj);
				}
			}
			categoryObj.put("name", productCategory.getName());
			categoryObj.put("products", productArray);
//			if (productArray.size() > 0) {
				array.add(categoryObj);
//			}
		}
		return array.toJSONString();
	}
	
	/**
	 * 是否可以选择商品
	 * @param product
	 * @param memberRankId
	 * @return
	 */
	private boolean canSelect(Product product, Long memberRankId){
		// 判断库存，没有的不显示
		Sku sku = product.getDefaultSku();
		if (null == sku || sku.getAvailableStock() <= 0) {
			return false;
		}
		// 没有劵的不显示
		Set<ProductGrade> grades = product.getProductGrades();
		if (null != grades) {
			for (ProductGrade productGrade : grades) {
				if (productGrade.getGrade().getId() == memberRankId) {
					return true;
				}
			}
		}
		return false;
	}
}
