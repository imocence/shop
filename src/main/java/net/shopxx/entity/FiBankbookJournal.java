package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - 交易记录表
 * 
 * @author gaoxiang
 * @version 1.0.0
 */
@Entity
@Table(name="fi_bankbook_journal")
public class FiBankbookJournal extends BaseEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 处理类型
	 * @author gaoxiang
	 *
	 */
	public enum DealType {
		/**
		 * 存入
		 */
		deposit,
		
		/**
		 * 取出
		 */
		takeout;
	}
	
	/**
	 * 账户类型
	 * @author gaoxiang
	 *
	 */
	public enum Type {
		/**
		 * 电子币账户
		 */
		balance,
		
		/**
		 * 购物券账户
		 */
		coupon;
	}
	
	/**
	 * 资金类别
	 * @author gaoxiang
	 *
	 */
	public enum MoneyType {
		/**
		 * 现金
		 */
		cash,
		
		/**
		 * 在线充值
		 */
		recharge;
	}
	
	/**
	 * 国家
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="company_code", referencedColumnName="name_cn")
	private Country country;
	
	/**
	 * 会员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_code", referencedColumnName="usercode")
	private Member member;
	
	/**
	 * 处理类型 deal_type  0:存入  1取出
	 */
	@JsonView(BaseView.class)
	@Column(name="deal_type")
	private FiBankbookJournal.DealType dealType;
	
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
	@Column(nullable = false, precision = 21, scale = 6)
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
	 * 资金类型  0:现金  1:在线充值
	 */
	@JsonView(BaseView.class)
	@Column(name="money_type")
	private FiBankbookJournal.MoneyType moneyType;
	
	/**
	 * 唯一标示
	 */
	@Length(max = 200)
	@Column(name="unique_code", unique=true)
	private String uniqueCode; 
	
	/**
	 * 上一条交易记录ID
	 */
	@Length(max = 20)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="last_id", referencedColumnName="id")
	private FiBankbookJournal lastFiBankbookJournal;
	
	/**
	 * 上一条交易记录的金额
	 */
	@Column(name="last_money", precision = 21, scale = 6)
	private BigDecimal lastMoney;
	
	
	/**
	 * 类型  0:电子币账户  1:购物券账户
	 */
	@JsonView(BaseView.class)
	@Column(name="type")
	private FiBankbookJournal.Type type;
	
	/**
	 * 交易记录线下表ID
	 */
	@Length(max = 20)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="journal_temp_id", referencedColumnName="id")
	private FiBankbookJournalTemp fiBankbookJournalTemp;

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

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public BigDecimal getLastMoney() {
		return lastMoney;
	}

	public void setLastMoney(BigDecimal lastMoney) {
		this.lastMoney = lastMoney;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public FiBankbookJournal.DealType getDealType() {
		return dealType;
	}

	public void setDealType(FiBankbookJournal.DealType dealType) {
		this.dealType = dealType;
	}

	public FiBankbookJournal.MoneyType getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(FiBankbookJournal.MoneyType moneyType) {
		this.moneyType = moneyType;
	}

	public FiBankbookJournal.Type getType() {
		return type;
	}

	public void setType(FiBankbookJournal.Type type) {
		this.type = type;
	}

	public FiBankbookJournal getLastFiBankbookJournal() {
		return lastFiBankbookJournal;
	}

	public void setLastFiBankbookJournal(FiBankbookJournal lastFiBankbookJournal) {
		this.lastFiBankbookJournal = lastFiBankbookJournal;
	}

	public FiBankbookJournalTemp getFiBankbookJournalTemp() {
		return fiBankbookJournalTemp;
	}

	public void setFiBankbookJournalTemp(FiBankbookJournalTemp fiBankbookJournalTemp) {
		this.fiBankbookJournalTemp = fiBankbookJournalTemp;
	}
	
}
