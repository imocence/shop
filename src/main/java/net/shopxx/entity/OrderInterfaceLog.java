/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Entity - 订单接口记录
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class OrderInterfaceLog extends BaseEntity<Long> {

	private static final long serialVersionUID = -2704154761295319939L;

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 订单发货
		 */
		shipping,

		/**
		 * 直销发货接口回调
		 */
		shipped
	}

	/**
	 * 类型
	 */
	@Column(nullable = false, updatable = false)
	private OrderInterfaceLog.Type type;

	/**
	 * 返回码
	 */
	@Column(updatable = false)
	private String code;
	
	/**
	 * 详情
	 */
	@Column(columnDefinition="TEXT")
	private String detail;

	/**
	 * 订单编号
	 */
	@Column(updatable = false)
	private String sn;

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public OrderInterfaceLog.Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(OrderInterfaceLog.Type type) {
		this.type = type;
	}

	/**
	 * 获取详情
	 * 
	 * @return 详情
	 */
	public String getDetail() {
		return detail;
	}

	/**
	 * 设置详情
	 * 
	 * @param detail
	 *            详情
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}