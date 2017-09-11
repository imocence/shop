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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.json.JSONObject;
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
import net.shopxx.entity.Admin;
import net.shopxx.entity.Cart;
import net.shopxx.entity.CartItem;
import net.shopxx.entity.Coupon;
import net.shopxx.entity.CouponCode;
import net.shopxx.entity.DeliveryCorp;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.FiBankbookJournal;
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
import net.shopxx.service.CouponCodeService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.MailService;
import net.shopxx.service.MemberService;
import net.shopxx.service.OrderItemService;
import net.shopxx.service.OrderService;
import net.shopxx.service.ProductService;
import net.shopxx.service.ShippingMethodService;
import net.shopxx.service.SkuService;
import net.shopxx.service.SmsService;
import net.shopxx.service.UserService;
import net.shopxx.util.NumberUtil;
import net.shopxx.util.SpringUtils;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.TimeUtil;
import net.shopxx.util.WebUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
	private FiBankbookJournalService fiBankbookJournalService;
	
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
	/**
	 * 订单总金额
	 */
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
	/**
	 * 订单总金额
	 */
	public BigDecimal calculateCouponAmount(BigDecimal price, BigDecimal offsetAmount) {
		BigDecimal amount = price;
		Setting setting = SystemUtils.getSetting();
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
	public BigDecimal calculateCouponAmount(Order order) {
		Assert.notNull(order);

		return calculateCouponAmount(order.getCouponPrice(), order.getOffsetCouponAmount());
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
	public boolean orderInterface(Order order){
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
		
		
		parameterMap.put("timestamp", TimeUtil.getTimestamp());
		parameterMap.put("signature", DigestUtils.md5Hex(TimeUtil.getTimestamp()+urlSignature));
		String orderMap = WebUtils.postJson(urlPath+"/shopMemberOrderCreate.html",parameterMap);
		System.out.println(orderMap);
		try {
			JSONObject jsStr = JSONObject.fromObject(orderMap); 
			String errKey = jsStr.getString("errCode");
			if("0000".equals(errKey)){
				return true;
			}else{
				System.out.println(jsStr.getString("msg"));
				return false;
			}
		} catch (Exception e) {
			System.out.println("没有返回信息");
			return false;
		}
				
	}
	
	@Transactional(readOnly = true)
	public Order generate(Order.Type type, Cart cart,  NapaStores napaStores,Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String memo) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.state(!cart.isEmpty());

		Setting setting = SystemUtils.getSetting();
		Member member = cart.getMember();

		Order order = new Order();
		order.setType(type);
		order.setPrice(cart.getPrice());
		order.setCouponPrice(cart.getCouponPrice());//券总计
		
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
		System.out.println("券总计"+cart.getCouponPrice());
		order.setCouponAmount(cart.getCouponPrice());//券总计
		if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(member.getBalance()) <= 0 && balance.compareTo(order.getAmount()) <= 0) {
			order.setAmountPaid(balance);
		} else {
			order.setAmountPaid(BigDecimal.ZERO);
		}
		//收货地址
		if (cart.getIsDelivery() && napaStores != null) {
			order.setConsignee(member.getName());
			order.setAreaName(null);
			order.setAddress(napaStores.getNapaAddress());
			order.setZipCode(null);
			order.setPhone(napaStores.getMobile());
			order.setArea(null);
		}
		/*if (cart.getIsDelivery() && receiver != null) {
			order.setConsignee(receiver.getConsignee());
			order.setAreaName(receiver.getAreaName());
			order.setAddress(receiver.getAddress());
			order.setZipCode(receiver.getZipCode());
			order.setPhone(receiver.getPhone());
			order.setArea(receiver.getArea());
		}*/
		//商品
		List<OrderItem> orderItems = order.getOrderItems();
		for (CartItem cartItem : cart) {
			Sku sku = cartItem.getSku();
			if (sku != null) {
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(sku.getSn());
				orderItem.setName(sku.getName());
				orderItem.setType(sku.getType());
				orderItem.setPrice(cartItem.getPrice());//价格
				orderItem.setWeight(sku.getWeight());
				orderItem.setIsDelivery(sku.getIsDelivery());
				orderItem.setThumbnail(sku.getThumbnail());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setSku(cartItem.getSku());
				orderItem.setOrder(order);
				orderItem.setCouponPrice(cartItem.getCouponPrice());//券
				orderItem.setSpecifications(sku.getSpecifications());
				orderItems.add(orderItem);
			}
		}
		//赠品
		for (Sku gift : cart.getGifts()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setSn(gift.getSn());
			orderItem.setName(gift.getName());
			orderItem.setType(gift.getType());
			orderItem.setPrice(BigDecimal.ZERO);//价格
			orderItem.setCouponPrice(BigDecimal.ZERO);//券
			
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

	public Order createNose(Order.Type type, Cart cart, Receiver receiver,NapaStores napaStores, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance,BigDecimal coupon, String memo) {
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
		order.setCountry(member.getCountry());
		order.setSn(snDao.generate(Sn.Type.order));
		order.setType(type);
		order.setPrice(cart.getPrice());
		order.setCouponPrice(cart.getCouponPrice());
		order.setFee(BigDecimal.ZERO);
		order.setFreight(cart.getIsDelivery() && !cart.isFreeShipping() ? shippingMethodService.calculateFreight(shippingMethod, receiver, cart.getTotalWeight()) : BigDecimal.ZERO);
		order.setPromotionDiscount(cart.getDiscount());
		order.setOffsetAmount(BigDecimal.ZERO);
		
		order.setOffsetCouponAmount(BigDecimal.ZERO);
		order.setCouponAmountPaid(BigDecimal.ZERO);
		order.setRefundAmount(BigDecimal.ZERO);
		order.setSource(Order.Source.member);
		order.setCouponRefundAmount(BigDecimal.ZERO);
		order.setRewardPoint(cart.getEffectiveRewardPoint());
		order.setExchangePoint(cart.getExchangePoint());
		order.setWeight(cart.getTotalWeight());
		order.setQuantity(cart.getTotalQuantity());
		order.setShippedQuantity(0);
		order.setReturnedQuantity(0);
		//收货地址
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
		//优惠券抵扣
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
		order.setCouponAmount(cart.getCouponPrice());
		// 获取用户余额
		BigDecimal userBalance = new BigDecimal("0.00");
		BigDecimal userCouponbalance = new BigDecimal("0.00");
		Set<FiBankbookBalance> set = member.getFiBankbookBalances();
		if (null != set) {
			for (FiBankbookBalance fiBankbookBalance : set) {
				if (FiBankbookBalance.Type.balance == fiBankbookBalance.getType()) {
					userBalance = fiBankbookBalance.getBalance();
				}else if (FiBankbookBalance.Type.coupon == fiBankbookBalance.getType()) {
					userCouponbalance = fiBankbookBalance.getBalance();
				}
			}
		}
		// 校验余额
		if (userBalance.compareTo(order.getAmount()) < 0 || userCouponbalance.compareTo(order.getCouponAmount()) < 0) {
			throw new IllegalArgumentException("余额不足");
		}
		BigDecimal amountPayable = balance != null ? order.getAmount().subtract(balance) : order.getAmount();
		if (amountPayable.compareTo(BigDecimal.ZERO) > 0) {
			if (paymentMethod == null) {
				throw new IllegalArgumentException("支付方式不存在");
			}
			order.setStatus(PaymentMethod.Type.deliveryAgainstPayment.equals(paymentMethod.getType()) ? Order.Status.pendingPayment : Order.Status.pendingReview);
			order.setPaymentMethod(paymentMethod);
			if (paymentMethod.getTimeout() != null && Order.Status.pendingPayment.equals(order.getStatus())) {
				order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()));
			}
		} else {
			order.setAmountPaid(balance);
			order.setCouponAmountPaid(coupon);
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
			orderItem.setCouponPrice(cartItem.getCouponPrice());
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
			orderItem.setCouponPrice(BigDecimal.ZERO);
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
		
		try {
			orderDao.persist(order);
		} catch (Exception e) {
			throw new IllegalArgumentException("订单号生成失败或重复，请刷新页面");
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.create);
		orderLog.setOrder(order);
		orderLogDao.persist(orderLog);

		exchangePoint(order);
		if (Setting.StockAllocationTime.order.equals(setting.getStockAllocationTime())
				|| (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime()) && (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 || order.getExchangePoint() > 0 || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0))) {
			allocateStock(order);
		}

		// 余额支付
		OrderPayment orderPayment = new OrderPayment();
		orderPayment.setMethod(OrderPayment.Method.deposit);
		orderPayment.setAmount(order.getAmount());
		orderPayment.setCouponAmount(order.getCouponAmount());
		orderPayment.setFee(BigDecimal.ZERO);
		orderPayment.setOrder(order);
		orderPayment.setCountry(order.getCountry());
		try {
			payment(order, orderPayment);
		} catch (Exception e) {
			throw new IllegalArgumentException("支付出错，订单未完成");
		}

		mailService.sendCreateOrderMail(order);
		smsService.sendCreateOrderSms(order);

		if (!cart.isNew()) {
			cartDao.remove(cart);
		}
		
		return order;
	}
	
	/**
	 * 后台订单创建
	 * 
	 * @param order
	 *            订单
	 * @param paymentMethod
	 *            支付方式
	 * @param shippingMethod
	 *            配送方式
	 * @return 订单
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean create(Order order, Member member, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice) throws Exception{
		// 校验库存
		Setting setting = SystemUtils.getSetting();
		
		order.setSn(snDao.generate(Sn.Type.order));
		order.setType(Order.Type.general);
		order.setFee(BigDecimal.ZERO);
		order.setOffsetAmount(BigDecimal.ZERO);
		order.setOffsetCouponAmount(BigDecimal.ZERO);
		order.setAmountPaid(BigDecimal.ZERO);
		order.setCouponAmountPaid(BigDecimal.ZERO);
		order.setRefundAmount(BigDecimal.ZERO);
		order.setCouponRefundAmount(BigDecimal.ZERO);
		order.setRewardPoint(BigDecimal.ZERO.longValue());
		order.setExchangePoint(BigDecimal.ZERO.longValue());
		order.setShippedQuantity(0);
		order.setReturnedQuantity(0);
//		order.setConsignee(receiver.getConsignee());
//		order.setAreaName(receiver.getAreaName());
//		order.setAddress(receiver.getAddress());
//		order.setZipCode("123456");
//		order.setPhone(receiver.getPhone());
//		order.setArea(receiver.getArea());
		order.setIsUseCouponCode(false);
		order.setIsExchangePoint(false);
		order.setIsAllocatedStock(false);
		order.setInvoice(null);
		order.setShippingMethod(shippingMethod);
		order.setMember(member);
		order.setPromotionNames(null);
		order.setCoupons(null);
		order.setTax(calculateTax(order));
//		order.setAmount(calculateAmount(order));
		order.setSource(Order.Source.system);
		order.setCouponDiscount(BigDecimal.ZERO);
//		order.setCouponPrice(order.getCouponAmount());
//		order.setPrice(order.getAmount());
		order.setPromotionDiscount(BigDecimal.ZERO);
		order.setCouponAmountPaid(BigDecimal.ZERO);
		BigDecimal amount = order.getAmount();
		BigDecimal couponAmount = order.getCouponAmount();
		// 获取用户余额
		BigDecimal userBalance = new BigDecimal("0.00");
		BigDecimal userCouponbalance = new BigDecimal("0.00");
		Set<FiBankbookBalance> set = member.getFiBankbookBalances();
		if (null != set) {
			for (FiBankbookBalance fiBankbookBalance : set) {
				if (FiBankbookBalance.Type.balance == fiBankbookBalance.getType()) {
					userBalance = fiBankbookBalance.getBalance();
				}else if (FiBankbookBalance.Type.coupon == fiBankbookBalance.getType()) {
					userCouponbalance = fiBankbookBalance.getBalance();
				}
			}
		}
		// 校验余额
		if (userBalance.compareTo(order.getAmount()) < 0 || userCouponbalance.compareTo(order.getCouponAmount()) < 0) {
			throw new IllegalArgumentException("余额不足");
		}
		BigDecimal amountPayable = order.getAmount();
		if (amountPayable.compareTo(BigDecimal.ZERO) > 0) {
			if (paymentMethod == null) {
				throw new IllegalArgumentException("支付方式不存在");
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
		
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		List<OrderItem> curOrderItems = order.getOrderItems();
		// 获取重量和数量
		Integer totalQuantity = BigDecimal.ZERO.intValue();
		Integer totalWeight = BigDecimal.ZERO.intValue();
		if (null != curOrderItems) {
			for (OrderItem curOrderItem : curOrderItems) {
				if (null == curOrderItem.getQuantity() || curOrderItem.getQuantity() == 0) {
					continue;
				}
				OrderItem orderItem = new OrderItem();
				String sn = curOrderItem.getSn();
				Product product = productService.findBySn(sn);
				Sku sku = skuService.findBySn(sn);
				orderItem.setSn(curOrderItem.getSn());
				orderItem.setName(product.getName());
				orderItem.setType(product.getType());
				orderItem.setPrice(curOrderItem.getPrice());
				orderItem.setCouponPrice(curOrderItem.getCouponPrice());
				Integer weight = NumberUtil.getInt(product.getWeight()) * curOrderItem.getQuantity();
				totalWeight += weight;
				totalQuantity += curOrderItem.getQuantity();
				orderItem.setWeight(sku.getWeight());
				orderItem.setIsDelivery(sku.getIsDelivery());
				orderItem.setThumbnail(product.getThumbnail());
				orderItem.setQuantity(curOrderItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setSku(sku);
				orderItem.setOrder(order);
				orderItem.setSpecifications(sku.getSpecifications());
				orderItems.add(orderItem);
			}
		}
		order.setOrderItems(orderItems);
		order.setWeight(totalWeight);
		order.setQuantity(totalQuantity);
		if (totalQuantity <= 0) {
			throw new IllegalArgumentException("订单商品数为0");
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
		
		// 余额支付
		OrderPayment orderPayment = new OrderPayment();
		orderPayment.setMethod(OrderPayment.Method.deposit);
		orderPayment.setAmount(amount);
		orderPayment.setCouponAmount(couponAmount);
		orderPayment.setFee(BigDecimal.ZERO);
		orderPayment.setOrder(order);
		orderPayment.setCountry(order.getCountry());
		payment(order, orderPayment);
		
		mailService.sendCreateOrderMail(order);
		smsService.sendCreateOrderSms(order);
		return true;
	}

	public void modify(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingPayment.equals(order.getStatus()) || Order.Status.pendingReview.equals(order.getStatus())));

		order.setAmount(calculateAmount(order));
		order.setCouponAmount(calculateCouponAmount(order));
		if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0 && order.getCouponAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
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
			// 审核通过之后向直销推送订单
//			orderInterface(order);
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

	@Transactional(rollbackFor = Exception.class)
	public void payment(Order order, OrderPayment orderPayment) throws Exception {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(orderPayment);
		Assert.isTrue(orderPayment.isNew());
		Assert.notNull(orderPayment.getAmount());
		Assert.notNull(orderPayment.getCouponAmount());
		Assert.state(orderPayment.getAmount().compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(orderPayment.getCouponAmount().compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(!(orderPayment.getAmount().compareTo(BigDecimal.ZERO) == 0 && orderPayment.getCouponAmount().compareTo(BigDecimal.ZERO) == 0));
		Member member = order.getMember();
		orderPayment.setSn(snDao.generate(Sn.Type.orderPayment));
		orderPayment.setCountry(member.getCountry());
		orderPayment.setOrder(order);
		orderPaymentDao.persist(orderPayment);
//		if (member != null && OrderPayment.Method.deposit.equals(orderPayment.getMethod())) {
		if (member != null) {
//			memberService.addBalance(order.getMember(), orderPayment.getEffectiveAmount().negate(), DepositLog.Type.orderPayment, null);
			String notes = "用户编号[" + member.getUsercode() + "] 订单编号[" + order.getSn() + "] 电子币账户消费" + orderPayment.getAmount();
			if (orderPayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				fiBankbookJournalService.recharge(member.getUsercode(), orderPayment.getAmount(), null, 
						FiBankbookJournal.Type.balance, FiBankbookJournal.DealType.takeout, FiBankbookJournal.MoneyType.orderAudit, notes);
			}
			notes = "用户编号[" + member.getUsercode() + "] 订单编号[" + order.getSn() + "] 购物券账户消费" + orderPayment.getCouponAmount();
			if (orderPayment.getCouponAmount().compareTo(BigDecimal.ZERO) > 0) {
				fiBankbookJournalService.recharge(member.getUsercode(), orderPayment.getCouponAmount(), null, 
						FiBankbookJournal.Type.coupon, FiBankbookJournal.DealType.takeout, FiBankbookJournal.MoneyType.orderAudit, notes);
			}

		}
		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}
		order.setAmountPaid(order.getAmountPaid().add(orderPayment.getEffectiveAmount()));
		order.setCouponAmountPaid(order.getCouponAmountPaid().add(orderPayment.getCouponAmount()));
		order.setFee(order.getFee().add(orderPayment.getFee()));
		if (!order.hasExpired() && Order.Status.pendingPayment.equals(order.getStatus()) && (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0 && order.getCouponAmountPayable().compareTo(BigDecimal.ZERO) <= 0)) {
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
	
	@Transactional(rollbackFor = Exception.class)
	public void refunds(Order order, OrderRefunds orderRefunds) throws Exception {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0);
		Assert.notNull(orderRefunds);
		Assert.isTrue(orderRefunds.isNew());
		Assert.notNull(orderRefunds.getAmount());
		Assert.notNull(orderRefunds.getCouponAmount());
		Assert.state((orderRefunds.getAmount().compareTo(BigDecimal.ZERO) > 0 && orderRefunds.getAmount().compareTo(order.getRefundableAmount()) <= 0)
				|| (orderRefunds.getCouponAmount().compareTo(BigDecimal.ZERO) > 0 && orderRefunds.getCouponAmount().compareTo(order.getRefundableCouponAmount()) <= 0));
		
		Member member = order.getMember();
		orderRefunds.setSn(snDao.generate(Sn.Type.orderRefunds));
		orderRefunds.setOrder(order);
		orderRefunds.setCountry(member.getCountry());
		orderRefundsDao.persist(orderRefunds);

		if (member != null) {
//			memberService.addBalance(order.getMember(), orderRefunds.getAmount(), DepositLog.Type.orderRefunds, null);
			String notes = "用户编号[" + member.getUsercode() + "] 订单编号[" + order.getSn() + "] 电子币账户退款" + orderRefunds.getAmount();
			if (orderRefunds.getAmount().compareTo(BigDecimal.ZERO) > 0) {
				fiBankbookJournalService.recharge(member.getUsercode(), orderRefunds.getAmount(), null, 
						FiBankbookJournal.Type.balance, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.balanceRefund, notes);
			}
			if (orderRefunds.getCouponAmount().compareTo(BigDecimal.ZERO) > 0) {
				notes = "用户编号[" + member.getUsercode() + "] 订单编号[" + order.getSn() + "] 购物券账户退款" + orderRefunds.getCouponAmount();
				fiBankbookJournalService.recharge(member.getUsercode(), orderRefunds.getCouponAmount(), null, 
						FiBankbookJournal.Type.coupon, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.balanceRefund, notes);
			}
		}
		
		order.setAmountPaid(order.getAmountPaid().subtract(orderRefunds.getAmount()));
		order.setCouponAmountPaid(order.getCouponAmountPaid().subtract(orderRefunds.getCouponAmount()));
		order.setRefundAmount(order.getRefundAmount().add(orderRefunds.getAmount()));
		order.setCouponRefundAmount(order.getCouponRefundAmount().add(orderRefunds.getCouponAmount()));
		
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
		orderShipping.setCountry(order.getCountry());
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
		orderReturns.setCountry(order.getCountry());
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
		if ((order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 && order.getCouponAmountPaid().compareTo(BigDecimal.ZERO) >= 0)
			|| (order.getAmountPaid().compareTo(BigDecimal.ZERO) >= 0 && order.getCouponAmountPaid().compareTo(BigDecimal.ZERO) > 0)) {
			memberService.addAmount(member, order.getAmountPaid(), order.getCouponAmountPaid());
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
	
	/**
	 * 发货推单
	 * 状态改为“待发货”
	 * @param order
	 *            订单
	 */
	@Transactional(rollbackFor = Exception.class)
	public String shippingReview(Long... ids) throws Exception{
		// 获取当前用户
		Admin currentUser = userService.getCurrent(Admin.class);
		if (null == currentUser) {
			throw new Exception(SpringUtils.getMessage("admin.common.session.lost"));
		}
		if (ids != null) {
			// 待审核状态  状态改为“待发货”，->推单到直销
			for (Long id : ids) {
				Order order = find(id);
				if (null == order || order.isNew() || !acquireLock(order)) {
					return "";
				}
				review(order, true);
			}
		}
		return "";
	}
	
	/**
	 * 直销发货接口回调
	 * 1：减少实际库存
	 * 2：全部发货完成状态改为“已发货”
	 * @param sn 订单编号
	 * @param shippingMethod 配送方式
	 * @param deliveryCorp 物流名称
	 * @param deliveryCorpCode 物流代码
	 * @param deliveryCorpUrl 物流地址
	 * @param freight 运费
	 * @param trackingNo 运单号
	 * @param items 订单的商品编号和发货数量集合
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public String shippedReview(String sn, String shippingMethod, String deliveryCorp, String deliveryCorpCode, String deliveryCorpUrl, BigDecimal freight, String trackingNo, Map<String, Integer> items) throws Exception{
		Order order = findBySn(sn);
		if (null == order || order.isNew()) {
			return "";
		}
		if (order.getShippableQuantity() <= 0) {
			return "";
		}
		// 只有待发货需要处理
		if (Order.Status.pendingShipment != order.getStatus()) {
			return "";
		}
		// 构建订单发货
		OrderShipping orderShipping = new OrderShipping();
		orderShipping.setOrder(order);
		// 获取配送方式
		orderShipping.setShippingMethod(shippingMethod);
		orderShipping.setDeliveryCorp(deliveryCorp);
		orderShipping.setDeliveryCorpUrl(deliveryCorpUrl);
		orderShipping.setDeliveryCorpCode(deliveryCorpCode);
		orderShipping.setArea(order.getArea());
		
		orderShipping.setTrackingNo(trackingNo);
		orderShipping.setFreight(freight);
		orderShipping.setConsignee(order.getConsignee());
		orderShipping.setAddress(order.getAddress());
		orderShipping.setZipCode(order.getZipCode());
		orderShipping.setPhone(order.getPhone());
		
		orderShipping.setSn(snDao.generate(Sn.Type.orderShipping));
		orderShipping.setOrder(order);
		orderShipping.setCountry(order.getCountry());
		
		// 构建发货单子项
		List<OrderShippingItem> orderShippingItems = new ArrayList<OrderShippingItem>();
		if (null != items && !items.isEmpty()) {
			Iterator<Entry<String, Integer>> iterator = items.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Integer> entry = iterator.next();
				String itemSn = entry.getKey();
				Integer quantity = entry.getValue();
				OrderItem orderItem = order.getOrderItem(itemSn);
				OrderShippingItem orderShippingItem = new OrderShippingItem();
				// 设置 发货项的数量
				orderShippingItem.setQuantity(quantity);
				// 设置编号
				orderShippingItem.setSn(orderItem.getSn());
				
				orderShippingItem.setName(orderItem.getName());
				orderShippingItem.setIsDelivery(orderItem.getIsDelivery());
				Sku sku = orderItem.getSku();
				if (sku != null && orderShippingItem.getQuantity() > sku.getStock()) {
					continue;
				}
				skuService.addStock(sku, -orderShippingItem.getQuantity(), StockLog.Type.stockOut, null);
				if (BooleanUtils.isTrue(order.getIsAllocatedStock())) {
					skuService.addAllocatedStock(sku, -orderShippingItem.getQuantity());
				}
				orderShippingItem.setSku(sku);
				orderShippingItem.setOrderShipping(orderShipping);
				orderShippingItem.setSpecifications(orderItem.getSpecifications());
				// 设置 订单的已发货数量=订单已发货数量+发货项的数量
				orderItem.setShippedQuantity(orderItem.getShippedQuantity() + quantity);
				orderShippingItems.add(orderShippingItem);
			}
		}
		orderShipping.setOrderShippingItems(orderShippingItems);
		orderShippingDao.persist(orderShipping);

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.ship.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
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
		return "";
	}
	
	/**
	 * 退单退款
	 * 
	 * @param order
	 *            订单
	 */
	@Transactional(rollbackFor = Exception.class)
	public String returnsReview(Long... ids) throws Exception{
		// 获取当前用户
		Admin currentUser = userService.getCurrent(Admin.class);
		if (null == currentUser) {
			throw new Exception(SpringUtils.getMessage("admin.common.session.lost"));
		}
		if (ids != null) {
			for (Long id : ids) {
				Order order = find(id);
				if (null == order || order.isNew()) {
					continue;
				}
				// 状态是等待付款、等待审核、等待发货、已发货、已收货可以退货退款
				if (Order.Status.pendingPayment != order.getStatus() && Order.Status.pendingReview != order.getStatus()
						&& Order.Status.pendingShipment != order.getStatus() && Order.Status.shipped != order.getStatus()
						&& Order.Status.received != order.getStatus()) {
					continue;
				}
				// 修改状态为“已失败”，已分配库存和金额回退,减去的库存回退
				// 退款 退款金额和电子币
				if ((order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 && order.getCouponAmountPaid().compareTo(BigDecimal.ZERO) >= 0)
					|| (order.getAmountPaid().compareTo(BigDecimal.ZERO) >= 0 && order.getCouponAmountPaid().compareTo(BigDecimal.ZERO) > 0)) {
					OrderRefunds orderRefunds = new OrderRefunds();
					Member member = order.getMember();
					orderRefunds.setSn(snDao.generate(Sn.Type.orderRefunds));
					orderRefunds.setOrder(order);
					orderRefunds.setCountry(member.getCountry());
					orderRefunds.setAmount(order.getAmountPaid());
					orderRefunds.setCouponAmount(order.getCouponAmountPaid());
					orderRefunds.setMemo("退单退款");
					orderRefunds.setMethod(OrderRefunds.Method.deposit);
					orderRefunds.setPaymentMethod(order.getPaymentMethod());
					orderRefundsDao.persist(orderRefunds);
					
					if (member != null) {
						String notes = "用户编号[" + member.getUsercode() + "] 订单编号[" + order.getSn() + "] 电子币账户退款" + orderRefunds.getAmount();
						if (orderRefunds.getAmount().compareTo(BigDecimal.ZERO) > 0) {
							fiBankbookJournalService.recharge(member.getUsercode(), orderRefunds.getAmount(), null, 
									FiBankbookJournal.Type.balance, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.balanceRefund, notes);
						}
						if (orderRefunds.getCouponAmount().compareTo(BigDecimal.ZERO) > 0) {
							notes = "用户编号[" + member.getUsercode() + "] 订单编号[" + order.getSn() + "] 购物券账户退款" + orderRefunds.getCouponAmount();
							fiBankbookJournalService.recharge(member.getUsercode(), orderRefunds.getCouponAmount(), null, 
									FiBankbookJournal.Type.coupon, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.balanceRefund, notes);
						}
					}
					
					order.setAmountPaid(order.getAmountPaid().subtract(orderRefunds.getAmount()));
					order.setCouponAmountPaid(order.getCouponAmountPaid().subtract(orderRefunds.getCouponAmount()));
					order.setRefundAmount(order.getRefundAmount().add(orderRefunds.getAmount()));
					order.setCouponRefundAmount(order.getCouponRefundAmount().add(orderRefunds.getCouponAmount()));
					
					OrderLog orderLog = new OrderLog();
					orderLog.setType(OrderLog.Type.refunds);
					orderLog.setOrder(order);
					orderLogDao.persist(orderLog);
					
					mailService.sendRefundsOrderMail(order);
					smsService.sendRefundsOrderSms(order);
				}
				// 退货
				if (order.getReturnableQuantity() > 0) {
					OrderReturns orderReturns = new OrderReturns();
					Integer totalWeight = 0;
					// 构建订单退货项
					List<OrderItem> orderItems = order.getOrderItems();
					List<OrderReturnsItem> orderReturnsItems = new ArrayList<OrderReturnsItem>();
					if (null != orderItems) {
						for (OrderItem orderItem : orderItems) {
							OrderReturnsItem orderReturnsItem = new OrderReturnsItem();
							orderReturnsItem.setName(orderItem.getName());
							orderReturnsItem.setOrderReturns(orderReturns);
							orderReturnsItem.setSpecifications(orderItem.getSpecifications());
							orderReturnsItem.setQuantity(orderItem.getReturnableQuantity());
							orderReturnsItem.setSn(orderItem.getSn());
							orderReturnsItems.add(orderReturnsItem);
							totalWeight += NumberUtil.getInt(orderItem.getWeight()) * orderReturnsItem.getQuantity();
						}
					}
					orderReturns.setOrderReturnsItems(orderReturnsItems);
					// 获取配送方式
					ShippingMethod shippingMethod = order.getShippingMethod();
					DeliveryCorp deliveryCorp = null;
					if (null != shippingMethod) {
						deliveryCorp = shippingMethod.getDefaultDeliveryCorp();
					}
					orderReturns.setShippingMethod(shippingMethod);
					orderReturns.setDeliveryCorp(deliveryCorp);
					orderReturns.setArea(order.getArea());
					orderReturns.setSn(snDao.generate(Sn.Type.orderReturns));
					orderReturns.setOrder(order);
					orderReturns.setCountry(order.getCountry());
					orderReturns.setMemo("退单退款");
					orderReturns.setFreight(shippingMethodService.calculateFreight(shippingMethod, order.getArea(), totalWeight));
					orderReturns.setPhone(order.getPhone());
					orderReturns.setShipper(order.getConsignee());
					orderReturns.setTrackingNo(null);
					orderReturns.setZipCode(order.getZipCode());
					orderReturnsDao.persist(orderReturns);
					
					for (OrderReturnsItem orderReturnsItem : orderReturns.getOrderReturnsItems()) {
						OrderItem orderItem = order.getOrderItem(orderReturnsItem.getSn());
						if (orderItem == null || orderReturnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
							throw new IllegalArgumentException();
						}
						orderItem.setReturnedQuantity(orderItem.getReturnedQuantity() + orderReturnsItem.getQuantity());
					}
					order.setReturnedQuantity(order.getReturnedQuantity() + orderReturns.getQuantity());
				}
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
		}
		return "";
	}
	
	/**
	 * 一键完成
	 * 
	 * @param order
	 *            订单
	 */
	@Transactional(rollbackFor = Exception.class)
	public String completeReview(Long... ids) throws Exception{
		// 获取当前用户
		Admin currentUser = userService.getCurrent(Admin.class);
		if (null == currentUser) {
			throw new Exception(SpringUtils.getMessage("admin.common.session.lost"));
		}
		if (ids != null) {
			for (Long id : ids) {
				Order order = find(id);
				if (null == order || order.isNew() || !acquireLock(order)) {
					continue;
				}
				if (order.hasExpired()) {
					continue;
				}
				// 状态是已收货、已发货可以完成
				if (Order.Status.shipped != order.getStatus() && Order.Status.received != order.getStatus()) {
					continue;
				}

				Member member = order.getMember();
				if (order.getRewardPoint() > 0) {
					memberService.addPoint(member, order.getRewardPoint(), PointLog.Type.reward, null);
				}
				if (CollectionUtils.isNotEmpty(order.getCoupons())) {
					for (Coupon coupon : order.getCoupons()) {
						couponCodeService.generate(coupon, member);
					}
				}
				if ((order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 && order.getCouponAmountPaid().compareTo(BigDecimal.ZERO) >= 0)
					|| (order.getAmountPaid().compareTo(BigDecimal.ZERO) >= 0 && order.getCouponAmountPaid().compareTo(BigDecimal.ZERO) > 0)) {
					memberService.addAmount(member, order.getAmountPaid(), order.getCouponAmountPaid());
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
		}
		return "";
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
