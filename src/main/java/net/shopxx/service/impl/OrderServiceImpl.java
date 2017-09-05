/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.shopxx.Filter;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.Setting;
import net.shopxx.dao.CartDao;
import net.shopxx.dao.OrderDao;
import net.shopxx.dao.OrderLogDao;
import net.shopxx.dao.OrderPaymentDao;
import net.shopxx.dao.OrderRefundsDao;
import net.shopxx.dao.OrderReturnsDao;
import net.shopxx.dao.OrderShippingDao;
import net.shopxx.dao.SnDao;
import net.shopxx.entity.Area;
import net.shopxx.entity.Cart;
import net.shopxx.entity.CartItem;
import net.shopxx.entity.Coupon;
import net.shopxx.entity.CouponCode;
import net.shopxx.entity.DepositLog;
import net.shopxx.entity.Invoice;
import net.shopxx.entity.Member;
import net.shopxx.entity.NapaStores;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;
import net.shopxx.entity.OrderLog;
import net.shopxx.entity.OrderPayment;
import net.shopxx.entity.OrderRefunds;
import net.shopxx.entity.OrderReturns;
import net.shopxx.entity.OrderReturnsItem;
import net.shopxx.entity.OrderShipping;
import net.shopxx.entity.OrderShippingItem;
import net.shopxx.entity.PaymentMethod;
import net.shopxx.entity.PointLog;
import net.shopxx.entity.Product;
import net.shopxx.entity.Receiver;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.entity.Sku;
import net.shopxx.entity.Sn;
import net.shopxx.entity.StockLog;
import net.shopxx.entity.User;
import net.shopxx.service.AreaService;
import net.shopxx.service.CouponCodeService;
import net.shopxx.service.MailService;
import net.shopxx.service.MemberService;
import net.shopxx.service.OrderItemService;
import net.shopxx.service.OrderService;
import net.shopxx.service.ProductService;
import net.shopxx.service.ShippingMethodService;
import net.shopxx.service.SkuService;
import net.shopxx.service.SmsService;
import net.shopxx.service.UserService;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.TimeUtil;
import net.shopxx.util.WebUtils;

/**
 * Service - 订单
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class OrderServiceImpl extends BaseServiceImpl<Order, Long> implements OrderService {

	@Inject
	private CacheManager cacheManager;
	@Inject
	private OrderDao orderDao;
	@Inject
	private OrderLogDao orderLogDao;
	@Inject
	private CartDao cartDao;
	@Inject
	private SnDao snDao;
	@Inject
	private OrderPaymentDao orderPaymentDao;
	@Inject
	private OrderRefundsDao orderRefundsDao;
	@Inject
	private OrderShippingDao orderShippingDao;
	@Inject
	private OrderReturnsDao orderReturnsDao;
	@Inject
	private UserService userService;
	@Inject
	private MemberService memberService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private ProductService productService;
	@Inject
	private SkuService skuService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private MailService mailService;
	@Inject
	private SmsService smsService;
	@Inject
	private OrderItemService orderItemService;
	@Inject 
	private AreaService areaService;
	@Value("${url.signature}")
	private String urlSignature;
	@Value("${url.path}")
	private String urlPath;
	@Transactional(readOnly = true)
	public Order findBySn(String sn) {
		return orderDao.find("sn", StringUtils.lowerCase(sn));
	}

	@Transactional(readOnly = true)
	public List<Order> findList(Order.Type type, Order.Status status, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters,
			List<net.shopxx.Order> orders) {
		return orderDao.findList(type, status, member, product, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<Order> findPage(Order.Type type, Order.Status status, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable) {
		return orderDao.findPage(type, status, member, product, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Order.Type type, Order.Status status, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired) {
		return orderDao.count(type, status, member, product, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired);
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateTax(BigDecimal price, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsTaxPriceEnabled()) {
			return BigDecimal.ZERO;
		}
		BigDecimal amount = price;
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		BigDecimal tax = amount.multiply(new BigDecimal(String.valueOf(setting.getTaxRate())));
		return tax.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(tax) : BigDecimal.ZERO;
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateTax(Order order) {
		Assert.notNull(order);

		if (order.getInvoice() == null) {
			return BigDecimal.ZERO;
		}
		return calculateTax(order.getPrice(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateAmount(BigDecimal price, BigDecimal fee, BigDecimal freight, BigDecimal tax, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(fee == null || fee.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(freight == null || freight.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(tax == null || tax.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		BigDecimal amount = price;
		if (fee != null) {
			amount = amount.add(fee);
		}
		if (freight != null) {
			amount = amount.add(freight);
		}
		if (tax != null) {
			amount = amount.add(tax);
		}
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		return amount.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(amount) : BigDecimal.ZERO;
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateAmount(Order order) {
		Assert.notNull(order);

		return calculateAmount(order.getPrice(), order.getFee(), order.getFreight(), order.getTax(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	@Transactional(readOnly = true)
	public boolean acquireLock(Order order, User user) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(user);
		Assert.isTrue(!user.isNew());

		Long orderId = order.getId();
		Ehcache cache = cacheManager.getEhcache(Order.ORDER_LOCK_CACHE_NAME);
		cache.acquireWriteLockOnKey(orderId);
		try {
			Element element = cache.get(orderId);
			if (element != null && !user.getId().equals(element.getObjectValue())) {
				return false;
			}
			cache.put(new Element(orderId, user.getId()));
		} finally {
			cache.releaseWriteLockOnKey(orderId);
		}
		return true;
	}

	@Transactional(readOnly = true)
	public boolean acquireLock(Order order) {
		User currentUser = userService.getCurrent();
		if (currentUser == null) {
			return false;
		}
		return acquireLock(order, currentUser);
	}

	@Transactional(readOnly = true)
	public void releaseLock(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());

		Ehcache cache = cacheManager.getEhcache(Order.ORDER_LOCK_CACHE_NAME);
		cache.remove(order.getId());
	}

	public void undoExpiredUseCouponCode() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, true, null, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoUseCouponCode(order);
				}
				orderDao.flush();
				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	public void undoExpiredExchangePoint() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, true, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoExchangePoint(order);
				}
				orderDao.flush();
				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	public void releaseExpiredAllocatedStock() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, null, true, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					releaseAllocatedStock(order);
				}
				orderDao.flush();
				orderDao.clear();
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}
	/**
	 * 属性字段参考
	 *  usercode 区代编号
		ordersn 订单号
		status 订单状态
		price 订单价格
		jifen 订单购物券
		payTime 支付时间
		realname 收货人姓名
		mobile 收货人电话
		address 联系地址
		province 收货省
		city 收货市
		area 收货地区
		expresscom 快递公司
		expresssn 快递单号
		expressTime 快递时间
		WarehouseName 发货仓库
	 * goods_data
		title 商品名
		unit 计量单位
		optiontitle 规格
		goodssn 商品编码
		price 单价
		jifen 单券
		total 数量
		weight 重量
		volume 体积
	 */
	@Transactional(rollbackFor = Exception.class)
	public String orderInterface(Order order){
		Map<String,Object> parameterMap = new HashMap<>();
		Map<String,Object> goods = new HashMap<String,Object>();
		List<Object> data = new ArrayList<>();
		List<OrderItem> orderItems = orderItemService.findByOrderId(order);
		if(orderItems.size() > 0){
			for(OrderItem orderItem :orderItems){
				goods.put("title", orderItem.getName());
				Sku sku = skuService.find(orderItem.getSku().getId());
				//List<SpecificationValue> specificationValue  = sku.getSpecificationValues();
				goods.put("unit", sku.getUnit());
				goods.put("optiontitle", "");//规格
				goods.put("goodssn", sku.getProduct().getSn());
				goods.put("price", orderItem.getPrice());
				goods.put("total", orderItem.getQuantity());//数量
				goods.put("weight", orderItem.getWeight());
				goods.put("volume", orderItem.getVersion());
				//String jsonObject = JSON.toJSONString(goods);
				data.add(goods);
			}
		}
		
		parameterMap.put("usercode", order.getMember().getNapaStores().getNapaCode());
		parameterMap.put("ordersn", order.getSn());		
		
		if("pendingPayment".equals(order.getStatus())){
			parameterMap.put("status", "等待付款");//等待付款
		}else if("pendingReview".equals(order.getStatus())){
			parameterMap.put("status", "等待审核");//等待审核
		}else if("pendingShipment".equals(order.getStatus())){
			parameterMap.put("status", "等待发货");//等待发货
		}else if("shipped".equals(order.getStatus())){
			parameterMap.put("status", "已发货");//已发货
		}else if("received".equals(order.getStatus())){
			parameterMap.put("status", "已收货");//已收货
		}else if("completed".equals(order.getStatus())){
			parameterMap.put("status", "已完成");//已完成
		}else if("failed".equals(order.getStatus())){
			parameterMap.put("status", "已失败");//已失败
		}else if("canceled".equals(order.getStatus())){
			parameterMap.put("status", "已取消");//已取消
		}else if("denied".equals(order.getStatus())){
			parameterMap.put("status", "已拒绝");//已拒绝
		}

		parameterMap.put("price", calculateAmount(order));//总金额+运费
		parameterMap.put("jifen", calculateAmount(order));//券金额
		parameterMap.put("payTime", TimeUtil.getNowTime());//支付时间
		
		parameterMap.put("realname", "");//收货人
		parameterMap.put("mobile", "");//收货人电话
		parameterMap.put("address", "");//收货人地址
		parameterMap.put("province", "");//收货人省
		parameterMap.put("city", "");//收货人市
		parameterMap.put("area", "");//收货人区
		
		parameterMap.put("expresscom", "");//快递公司
		parameterMap.put("expresssn", "");//快递单号
		parameterMap.put("shipFee", "");//运费
		parameterMap.put("expressTime", "");//发货时间
		parameterMap.put("WarehouseName", "");//发货仓库

		parameterMap.put("goods_data", data);//商品信息
		
		
		parameterMap.put("signature", DigestUtils.md5Hex(TimeUtil.getFormatNowTime("yyyyMMdd")+urlSignature));
		System.out.println(parameterMap.toString());
		String orderMap = WebUtils.postJson(urlPath+"/shopMemberOrderCreate.html",parameterMap);
		System.out.println(orderMap);
		/**
		 * errCode         msg
		 * 0000 		成功：success
		 * 1001 		验签错误
		 * 1002 		会员编号已存在
		 * 1003 		区代编号已存在
		 * 1004 		链接超过有效时间
		 * 1005                           商品编码不存在
		 * 2001 		异常:xxx
		 */
		return orderMap;
	}
	
	@Transactional(readOnly = true)
	public Order generate(Order.Type type, Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String memo) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.state(!cart.isEmpty());

		Setting setting = SystemUtils.getSetting();
		Member member = cart.getMember();

		Order order = new Order();
		order.setType(type);
		order.setPrice(cart.getPrice());
		order.setFee(BigDecimal.ZERO);
		order.setPromotionDiscount(cart.getDiscount());
		order.setOffsetAmount(BigDecimal.ZERO);
		order.setRefundAmount(BigDecimal.ZERO);
		order.setRewardPoint(cart.getEffectiveRewardPoint());
		order.setExchangePoint(cart.getExchangePoint());
		order.setWeight(cart.getTotalWeight());
		order.setQuantity(cart.getTotalQuantity());
		order.setShippedQuantity(0);
		order.setReturnedQuantity(0);
		order.setMemo(memo);
		order.setIsUseCouponCode(false);
		order.setIsExchangePoint(false);
		order.setIsAllocatedStock(false);
		order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
		order.setPaymentMethod(paymentMethod);
		order.setMember(member);
		order.setPromotionNames(cart.getPromotionNames());
		order.setCoupons(new ArrayList<>(cart.getCoupons()));

		if (shippingMethod != null && shippingMethod.isSupported(paymentMethod) && cart.getIsDelivery()) {
			order.setFreight(!cart.isFreeShipping() ? shippingMethodService.calculateFreight(shippingMethod, receiver, cart.getTotalWeight()) : BigDecimal.ZERO);
			order.setShippingMethod(shippingMethod);
		} else {
			order.setFreight(BigDecimal.ZERO);
			order.setShippingMethod(null);
		}

		if (couponCode != null && cart.isCouponAllowed() && cart.isValid(couponCode)) {
			BigDecimal couponDiscount = cart.getEffectivePrice().subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(), cart.getProductQuantity()));
			order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
			order.setCouponCode(couponCode);
		} else {
			order.setCouponDiscount(BigDecimal.ZERO);
			order.setCouponCode(null);
		}

		order.setTax(calculateTax(order));
		order.setAmount(calculateAmount(order));

		if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(member.getBalance()) <= 0 && balance.compareTo(order.getAmount()) <= 0) {
			order.setAmountPaid(balance);
		} else {
			order.setAmountPaid(BigDecimal.ZERO);
		}

		if (cart.getIsDelivery() && receiver != null) {
			order.setConsignee(receiver.getConsignee());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setZipCode(receiver.getZipCode());
			order.setPhone(receiver.getPhone());
			order.setArea(receiver.getArea());
		}

		List<OrderItem> orderItems = order.getOrderItems();
		for (CartItem cartItem : cart) {
			Sku sku = cartItem.getSku();
			if (sku != null) {
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(sku.getSn());
				orderItem.setName(sku.getName());
				orderItem.setType(sku.getType());
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setWeight(sku.getWeight());
				orderItem.setIsDelivery(sku.getIsDelivery());
				orderItem.setThumbnail(sku.getThumbnail());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setSku(cartItem.getSku());
				orderItem.setOrder(order);
				orderItem.setSpecifications(sku.getSpecifications());
				orderItems.add(orderItem);
			}
		}

		for (Sku gift : cart.getGifts()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(gift.getSn());
			orderItem.setName(gift.getName());
			orderItem.setType(gift.getType());
			orderItem.setPrice(BigDecimal.ZERO);
			orderItem.setWeight(gift.getWeight());
			orderItem.setIsDelivery(gift.getIsDelivery());
			orderItem.setThumbnail(gift.getThumbnail());
			orderItem.setQuantity(1);
			orderItem.setShippedQuantity(0);
			orderItem.setReturnedQuantity(0);
			orderItem.setSku(gift);
			orderItem.setOrder(order);
			orderItem.setSpecifications(gift.getSpecifications());
			orderItems.add(orderItem);
		}

		return order;
	}

	public Order create(Order.Type type, Cart cart, Receiver receiver,NapaStores napaStores, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String memo) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(napaStores);
		Assert.notNull(cart.getMember());
		Assert.state(!cart.isEmpty());
		if (cart.getIsDelivery()) {
			//收货地址
			//Assert.notNull(receiver);
			Assert.notNull(shippingMethod);
			Assert.state(shippingMethod.isSupported(paymentMethod));
		} else {
			//收货地址
			//Assert.isNull(receiver);
			Assert.isNull(shippingMethod);
		}

		for (CartItem cartItem : cart) {
			Sku sku = cartItem.getSku();
			if (sku == null || !sku.getIsMarketable() || cartItem.getQuantity() > sku.getAvailableStock()) {
				throw new IllegalArgumentException();
			}
		}

		for (Sku gift : cart.getGifts()) {
			if (!gift.getIsMarketable() || gift.getIsOutOfStock()) {
				throw new IllegalArgumentException();
			}
		}

		Setting setting = SystemUtils.getSetting();
		Member member = cart.getMember();

		Order order = new Order();
		order.setSn(snDao.generate(Sn.Type.order));
		order.setType(type);
		order.setPrice(cart.getPrice());
		order.setFee(BigDecimal.ZERO);
		order.setFreight(cart.getIsDelivery() && !cart.isFreeShipping() ? shippingMethodService.calculateFreight(shippingMethod, receiver, cart.getTotalWeight()) : BigDecimal.ZERO);
		order.setPromotionDiscount(cart.getDiscount());
		order.setOffsetAmount(BigDecimal.ZERO);
		order.setAmountPaid(BigDecimal.ZERO);
		order.setRefundAmount(BigDecimal.ZERO);
		order.setRewardPoint(cart.getEffectiveRewardPoint());
		order.setExchangePoint(cart.getExchangePoint());
		order.setWeight(cart.getTotalWeight());
		order.setQuantity(cart.getTotalQuantity());
		order.setShippedQuantity(0);
		order.setReturnedQuantity(0);
		if (cart.getIsDelivery()) {
			order.setConsignee(member.getName());
			order.setAreaName(napaStores.getNapaAddress());
			order.setAddress(napaStores.getNapaAddress());
			order.setZipCode(null);
			order.setPhone(napaStores.getMobile());
			order.setArea(null);
		}
		order.setMemo(memo);
		order.setIsUseCouponCode(false);
		order.setIsExchangePoint(false);
		order.setIsAllocatedStock(false);
		order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
		order.setShippingMethod(shippingMethod);
		order.setMember(member);
		order.setPromotionNames(cart.getPromotionNames());
		order.setCoupons(new ArrayList<>(cart.getCoupons()));

		if (couponCode != null) {
			if (!cart.isCouponAllowed() || !cart.isValid(couponCode)) {
				throw new IllegalArgumentException();
			}
			BigDecimal couponDiscount = cart.getEffectivePrice().subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(), cart.getProductQuantity()));
			order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
			order.setCouponCode(couponCode);
			useCouponCode(order);
		} else {
			order.setCouponDiscount(BigDecimal.ZERO);
		}

		order.setTax(calculateTax(order));
		order.setAmount(calculateAmount(order));

		if (balance != null && (balance.compareTo(BigDecimal.ZERO) < 0 || balance.compareTo(member.getBalance()) > 0 || balance.compareTo(order.getAmount()) > 0)) {
			throw new IllegalArgumentException();
		}
		BigDecimal amountPayable = balance != null ? order.getAmount().subtract(balance) : order.getAmount();
		if (amountPayable.compareTo(BigDecimal.ZERO) > 0) {
			if (paymentMethod == null) {
				throw new IllegalArgumentException();
			}
			order.setStatus(PaymentMethod.Type.deliveryAgainstPayment.equals(paymentMethod.getType()) ? Order.Status.pendingPayment : Order.Status.pendingReview);
			order.setPaymentMethod(paymentMethod);
			if (paymentMethod.getTimeout() != null && Order.Status.pendingPayment.equals(order.getStatus())) {
				order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()));
			}
		} else {
			order.setStatus(Order.Status.pendingReview);
			order.setPaymentMethod(null);
		}

		List<OrderItem> orderItems = order.getOrderItems();
		for (CartItem cartItem : cart) {
			Sku sku = cartItem.getSku();
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(sku.getSn());
			orderItem.setName(sku.getName());
			orderItem.setType(sku.getType());
			orderItem.setPrice(cartItem.getPrice());
			orderItem.setWeight(sku.getWeight());
			orderItem.setIsDelivery(sku.getIsDelivery());
			orderItem.setThumbnail(sku.getThumbnail());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setShippedQuantity(0);
			orderItem.setReturnedQuantity(0);
			orderItem.setSku(cartItem.getSku());
			orderItem.setOrder(order);
			orderItem.setSpecifications(sku.getSpecifications());
			orderItems.add(orderItem);
		}

		for (Sku gift : cart.getGifts()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(gift.getSn());
			orderItem.setName(gift.getName());
			orderItem.setType(gift.getType());
			orderItem.setPrice(BigDecimal.ZERO);
			orderItem.setWeight(gift.getWeight());
			orderItem.setIsDelivery(gift.getIsDelivery());
			orderItem.setThumbnail(gift.getThumbnail());
			orderItem.setQuantity(1);
			orderItem.setShippedQuantity(0);
			orderItem.setReturnedQuantity(0);
			orderItem.setSku(gift);
			orderItem.setOrder(order);
			orderItem.setSpecifications(gift.getSpecifications());
			orderItems.add(orderItem);
		}

		orderDao.persist(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.create);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		exchangePoint(order);
		if (Setting.StockAllocationTime.order.equals(setting.getStockAllocationTime())
				|| (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime()) && (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 || order.getExchangePoint() > 0 || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0))) {
			allocateStock(order);
		}

		if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment orderPayment = new OrderPayment();
			orderPayment.setMethod(OrderPayment.Method.deposit);
			orderPayment.setAmount(balance);
			orderPayment.setFee(BigDecimal.ZERO);
			orderPayment.setOrder(order);
			payment(order, orderPayment);
		}

		mailService.sendCreateOrderMail(order);
		smsService.sendCreateOrderSms(order);

		if (!cart.isNew()) {
			cartDao.remove(cart);
		}

		return order;
	}

	public void modify(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingPayment.equals(order.getStatus()) || Order.Status.pendingReview.equals(order.getStatus())));

		order.setAmount(calculateAmount(order));
		if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview);
			order.setExpire(null);
		} else {
			if (order.getPaymentMethod() != null && PaymentMethod.Type.deliveryAgainstPayment.equals(order.getPaymentMethod().getType())) {
				order.setStatus(Order.Status.pendingPayment);
			} else {
				order.setStatus(Order.Status.pendingReview);
				order.setExpire(null);
			}
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.modify);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendUpdateOrderMail(order);
		smsService.sendUpdateOrderSms(order);
	}

	public void cancel(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingPayment.equals(order.getStatus()) || Order.Status.pendingReview.equals(order.getStatus())));

		order.setStatus(Order.Status.canceled);
		order.setExpire(null);

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.cancel);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendCancelOrderMail(order);
		smsService.sendCancelOrderSms(order);
	}

	public void review(Order order, boolean passed) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.pendingReview.equals(order.getStatus()));

		if (passed) {
			order.setStatus(Order.Status.pendingShipment);
		} else {
			order.setStatus(Order.Status.denied);

			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.review);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendReviewOrderMail(order);
		smsService.sendReviewOrderSms(order);
	}

	public void payment(Order order, OrderPayment orderPayment) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(orderPayment);
		Assert.isTrue(orderPayment.isNew());
		Assert.notNull(orderPayment.getAmount());
		Assert.state(orderPayment.getAmount().compareTo(BigDecimal.ZERO) > 0);

		orderPayment.setSn(snDao.generate(Sn.Type.orderPayment));
		orderPayment.setOrder(order);
		orderPaymentDao.persist(orderPayment);

		if (order.getMember() != null && OrderPayment.Method.deposit.equals(orderPayment.getMethod())) {
			memberService.addBalance(order.getMember(), orderPayment.getEffectiveAmount().negate(), DepositLog.Type.orderPayment, null);
		}

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		order.setAmountPaid(order.getAmountPaid().add(orderPayment.getEffectiveAmount()));
		order.setFee(order.getFee().add(orderPayment.getFee()));
		if (!order.hasExpired() && Order.Status.pendingPayment.equals(order.getStatus()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview);
			order.setExpire(null);
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.payment);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendPaymentOrderMail(order);
		smsService.sendPaymentOrderSms(order);
	}

	public void refunds(Order order, OrderRefunds orderRefunds) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0);
		Assert.notNull(orderRefunds);
		Assert.isTrue(orderRefunds.isNew());
		Assert.notNull(orderRefunds.getAmount());
		Assert.state(orderRefunds.getAmount().compareTo(BigDecimal.ZERO) > 0 && orderRefunds.getAmount().compareTo(order.getRefundableAmount()) <= 0);

		orderRefunds.setSn(snDao.generate(Sn.Type.orderRefunds));
		orderRefunds.setOrder(order);
		orderRefundsDao.persist(orderRefunds);

		if (OrderRefunds.Method.deposit.equals(orderRefunds.getMethod())) {
			memberService.addBalance(order.getMember(), orderRefunds.getAmount(), DepositLog.Type.orderRefunds, null);
		}

		order.setAmountPaid(order.getAmountPaid().subtract(orderRefunds.getAmount()));
		order.setRefundAmount(order.getRefundAmount().add(orderRefunds.getAmount()));

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.refunds);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendRefundsOrderMail(order);
		smsService.sendRefundsOrderSms(order);
	}

	public void shipping(Order order, OrderShipping orderShipping) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getShippableQuantity() > 0);
		Assert.notNull(orderShipping);
		Assert.isTrue(orderShipping.isNew());
		Assert.notEmpty(orderShipping.getOrderShippingItems());

		orderShipping.setSn(snDao.generate(Sn.Type.orderShipping));
		orderShipping.setOrder(order);
		orderShippingDao.persist(orderShipping);

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.ship.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		for (OrderShippingItem orderShippingItem : orderShipping.getOrderShippingItems()) {
			OrderItem orderItem = order.getOrderItem(orderShippingItem.getSn());
			if (orderItem == null || orderShippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				throw new IllegalArgumentException();
			}
			orderItem.setShippedQuantity(orderItem.getShippedQuantity() + orderShippingItem.getQuantity());
			Sku sku = orderShippingItem.getSku();
			if (sku != null) {
				if (orderShippingItem.getQuantity() > sku.getStock()) {
					throw new IllegalArgumentException();
				}
				skuService.addStock(sku, -orderShippingItem.getQuantity(), StockLog.Type.stockOut, null);
				if (BooleanUtils.isTrue(order.getIsAllocatedStock())) {
					skuService.addAllocatedStock(sku, -orderShippingItem.getQuantity());
				}
			}
		}

		order.setShippedQuantity(order.getShippedQuantity() + orderShipping.getQuantity());
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setStatus(Order.Status.shipped);
			order.setIsAllocatedStock(false);
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.shipping);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendShippingOrderMail(order);
		smsService.sendShippingOrderSms(order);
	}

	public void returns(Order order, OrderReturns orderReturns) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getReturnableQuantity() > 0);
		Assert.notNull(orderReturns);
		Assert.isTrue(orderReturns.isNew());
		Assert.notEmpty(orderReturns.getOrderReturnsItems());

		orderReturns.setSn(snDao.generate(Sn.Type.orderReturns));
		orderReturns.setOrder(order);
		orderReturnsDao.persist(orderReturns);

		for (OrderReturnsItem orderReturnsItem : orderReturns.getOrderReturnsItems()) {
			OrderItem orderItem = order.getOrderItem(orderReturnsItem.getSn());
			if (orderItem == null || orderReturnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				throw new IllegalArgumentException();
			}
			orderItem.setReturnedQuantity(orderItem.getReturnedQuantity() + orderReturnsItem.getQuantity());
		}

		order.setReturnedQuantity(order.getReturnedQuantity() + orderReturns.getQuantity());

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.returns);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendReturnsOrderMail(order);
		smsService.sendReturnsOrderSms(order);
	}

	public void receive(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.shipped.equals(order.getStatus()));

		order.setStatus(Order.Status.received);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.receive);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendReceiveOrderMail(order);
		smsService.sendReceiveOrderSms(order);
	}

	public void complete(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.received.equals(order.getStatus()));

		Member member = order.getMember();
		if (order.getRewardPoint() > 0) {
			memberService.addPoint(member, order.getRewardPoint(), PointLog.Type.reward, null);
		}
		if (CollectionUtils.isNotEmpty(order.getCoupons())) {
			for (Coupon coupon : order.getCoupons()) {
				couponCodeService.generate(coupon, member);
			}
		}
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			memberService.addAmount(member, order.getAmountPaid());
		}
		for (OrderItem orderItem : order.getOrderItems()) {
			Sku sku = orderItem.getSku();
			if (sku != null && sku.getProduct() != null) {
				productService.addSales(sku.getProduct(), orderItem.getQuantity());
			}
		}

		order.setStatus(Order.Status.completed);
		order.setCompleteDate(new Date());

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.complete);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendCompleteOrderMail(order);
		smsService.sendCompleteOrderSms(order);
	}

	public void fail(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingShipment.equals(order.getStatus()) || Order.Status.shipped.equals(order.getStatus()) || Order.Status.received.equals(order.getStatus())));

		order.setStatus(Order.Status.failed);

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.fail);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		mailService.sendFailOrderMail(order);
		smsService.sendFailOrderSms(order);
	}

	@Override
	@Transactional
	public void delete(Order order) {
		if (order != null && !Order.Status.completed.equals(order.getStatus())) {
			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
		}

		super.delete(order);
	}

	/**
	 * 优惠码使用
	 * 
	 * @param order
	 *            订单
	 */
	private void useCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsUseCouponCode()) || order.getCouponCode() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(true);
		couponCode.setUsedDate(new Date());
		order.setIsUseCouponCode(true);
	}

	/**
	 * 优惠码使用撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoUseCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsUseCouponCode()) || order.getCouponCode() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(false);
		couponCode.setUsedDate(null);
		order.setIsUseCouponCode(false);
		order.setCouponCode(null);
	}

	/**
	 * 积分兑换
	 * 
	 * @param order
	 *            订单
	 */
	private void exchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), -order.getExchangePoint(), PointLog.Type.exchange, null);
		order.setIsExchangePoint(true);
	}

	/**
	 * 积分兑换撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoExchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), order.getExchangePoint(), PointLog.Type.undoExchange, null);
		order.setIsExchangePoint(false);
	}

	/**
	 * 分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void allocateStock(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Sku sku = orderItem.getSku();
				if (sku != null) {
					skuService.addAllocatedStock(sku, orderItem.getQuantity() - orderItem.getShippedQuantity());
				}
			}
		}
		order.setIsAllocatedStock(true);
	}

	/**
	 * 释放已分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void releaseAllocatedStock(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Sku sku = orderItem.getSku();
				if (sku != null) {
					skuService.addAllocatedStock(sku, -(orderItem.getQuantity() - orderItem.getShippedQuantity()));
				}
			}
		}
		order.setIsAllocatedStock(false);
	}

}