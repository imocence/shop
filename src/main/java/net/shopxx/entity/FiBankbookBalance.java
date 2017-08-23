package net.shopxx.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
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
	 * 用户编码
	 */
	@Length(max = 20)
	@Column(name="user_code", nullable = false)
	private String userCode;
	
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
	
	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
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
