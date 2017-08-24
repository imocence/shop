package net.shopxx.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.Length;
@Entity
public class NapaStores extends BaseEntity<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 区代编号
	 */
	@Length(max = 200)
	private String napa_code;
	
	/**
	 * 余额
	 */
	@Column(nullable = true, precision = 27, scale = 12)
	private BigDecimal balance;
	/**
	 * 电话
	 */
	@Length(max = 200)
	private String mobile;
	
	
	/**
	 * 获取区代编号
	 */
	public String getNapaCode() {
		return napa_code;
	}
	/**
	 * set区代编号
	 */
	public void setNapaCode(String napaCode) {
		this.napa_code = napaCode;
	}
	/**
	 * 获取余额
	 */
	public BigDecimal getBalance() {
		return balance;
	}
	/**
	 * 存入余额
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	/**
	 * 电话
	 */
	public String getMobile() {
		return mobile;
	}
	/**
	 * 电话
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
