package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - 汇款登记
 * 
 * @author cht
 * @version 1.0.0
 */
@Entity
public class RemittanceLog  extends BaseEntity<Long> {
	private static final long serialVersionUID = -8323452837046981882L;
	
	/**
	 * 核实状态
	 *
	 */
	public enum ConfirmStatus {
		/**
		 * 未核实
		 */
		unconfirmed,
		
		/**
		 * 已核实通过
		 */
		confirmedPass,
		
		/**
		 * 已核实不通过
		 */
		confirmedNoPass;
	}
	
	/**
	 * 会员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	private Member member;
	
	/**
	 * 汇款人姓名
	 */
	@JsonView(BaseView.class)
	@Column(updatable = false)
	private String name;
	
	/**
	 * 汇出账户
	 */
	@JsonView(BaseView.class)
	@Column(name="remittance_account",updatable = false)
	private String remittanceAccount;
	
	/**
	 * 汇款号码
	 */
	@JsonView(BaseView.class)
	@Column(name="remittance_number",updatable = false)
	private String remittanceNumber;
	
	/**
	 * 汇款人电话
	 */
	@JsonView(BaseView.class)
	@Column(updatable = false)
	private String telephone;
	
	/**
	 * 汇款人身份证
	 */
	@JsonView(BaseView.class)
	@Column(name="identity_card",updatable = false)
	private String identityCard;
	
	/**
	 * 汇款金额
	 */
	@JsonView(BaseView.class)
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	private BigDecimal amount;
	
	/**
	 * 汇款日期
	 */
	@Field(store = Store.YES, index = Index.YES, analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.SECOND)
	@Column(name="remittance_date")
	private Date remittanceDate;
	
	/**
	 * 类型  0:未核实  1:已核实通过 2:已核实不通过
	 */
	@JsonView(BaseView.class)
	@Column(name="confirm_status")
	private RemittanceLog.ConfirmStatus confirmStatus;
	
	/**
	 * 确认日期
	 */
	@Field(store = Store.YES, index = Index.YES, analyze = Analyze.NO)
	@DateBridge(resolution = Resolution.SECOND)
	@Column(name="confirm_date")
	private Date confirmDate;
	
	/**
	 * 管理员-确认人
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	private Admin admin;
	
	/**
	 * 备注
	 */
	@JsonView(BaseView.class)
	@Column(updatable = false)
	private String memo;

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemittanceAccount() {
		return remittanceAccount;
	}

	public void setRemittanceAccount(String remittanceAccount) {
		this.remittanceAccount = remittanceAccount;
	}

	public String getRemittanceNumber() {
		return remittanceNumber;
	}

	public void setRemittanceNumber(String remittanceNumber) {
		this.remittanceNumber = remittanceNumber;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getRemittanceDate() {
		return remittanceDate;
	}

	public void setRemittanceDate(Date remittanceDate) {
		this.remittanceDate = remittanceDate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
