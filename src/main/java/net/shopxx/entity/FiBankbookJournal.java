package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;

/**
 * Entity - 交易记录表
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
@Entity
@Table(name="fi_bankbook_journal")
public class FiBankbookJournal extends BaseEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 企业编码
	 */
	@Length(max = 4)
	@Column(name="company_code", nullable = false)
	private String companyCode;
	
	/**
	 * 用户编码
	 */
	@Length(max = 20)
	@Column(name="user_code", nullable = false)
	private String userCode;
	
	/**
	 * 处理类型
	 */
	@Length(max = 1)
	@Column(name="deal_type")
	private String dealType;
	
	/**
	 * 处理日期
	 */
	@Field(store = Store.YES, index = Index.YES, analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.SECOND)
	@Column(name="deal_date")
	private Date dealDate;
	
	/**
	 * 费用
	 */
	@Column(precision = 21, scale = 6)
	private BigDecimal money;
	
	/**
	 * 日志
	 */
	@Length(max = 255)
	@Column
	private String notes; 
	
	/**
	 * 余额
	 */
	@Column(precision = 21, scale = 6)
	private BigDecimal balance;
	
	/**
	 * 备注
	 */
	@Length(max = 255)
	@Column
	private String remark; 
	
	/**
	 * 创建人标示
	 */
	@Length(max = 20)
	@Column(name="creater_code")
	private String createrCode; 
	
	/**
	 * 创建人名称
	 */
	@Length(max = 255)
	@Column(name="creater_name")
	private String createrName; 
	
	/**
	 * 资金类型
	 */
	@Length(max = 5)
	@Column(name="money_type")
	private Integer moneyType; 
	
	/**
	 * 唯一标示
	 */
	@Length(max = 200)
	@Column(name="unique_code")
	private String uniqueCode; 
	
	/**
	 * 上一条交易记录ID
	 */
	@Length(max = 20)
	@Column(name="last_id")
	private Long lastId; 
	
	/**
	 * 上一条交易记录的金额
	 */
	@Column(name="last_money", precision = 21, scale = 6)
	private BigDecimal lastMoney;
	
	/**
	 * 类型
	 */
	@Length(max = 2)
	@Column(name="type")
	private String type;

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public Date getDealDate() {
		return dealDate;
	}

	public void setDealDate(Date dealDate) {
		this.dealDate = dealDate;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreaterCode() {
		return createrCode;
	}

	public void setCreaterCode(String createrCode) {
		this.createrCode = createrCode;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public Integer getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(Integer moneyType) {
		this.moneyType = moneyType;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public Long getLastId() {
		return lastId;
	}

	public void setLastId(Long lastId) {
		this.lastId = lastId;
	}

	public BigDecimal getLastMoney() {
		return lastMoney;
	}

	public void setLastMoney(BigDecimal lastMoney) {
		this.lastMoney = lastMoney;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	} 
}
