package net.shopxx.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

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
	 * 会员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	private Member member;
	
	/**
	 * 支付手续费
	 */
	@Column(nullable = false, precision = 21, scale = 6)
	private BigDecimal balance;
	
	/**
	 * 类型，type=1 为余额账户 ，type=2购物券账户
	 */
	@Length(max = 2)
	@Column
	private String type; 
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
