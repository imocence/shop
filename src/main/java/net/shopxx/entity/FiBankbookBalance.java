package net.shopxx.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - 存折
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Entity
@Table(name="fi_bankbook_balance")
public class FiBankbookBalance extends BaseEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 类型
	 */
	public enum Type {
		/**
		 * 余额账户
		 */
		balance,

		/**
		 * 购物券账户
		 */
		coupon
	}
	
	/**
	 * 会员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_code", referencedColumnName="usercode")
	private Member member;
	
	/**
	 * 支付手续费
	 */
	@Column(nullable = false, precision = 21, scale = 6)
	private BigDecimal balance;
	
	/**
	 * 类型，type=0为余额账户 ，type=1购物券账户
	 */
	@JsonView(BaseView.class)
	@Column(nullable = false, updatable = false)
	private Type type;
	
	/**
	 * 国家
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="country", referencedColumnName="name_cn")
	private Country country;
	
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
