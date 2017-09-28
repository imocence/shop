package net.shopxx.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import net.shopxx.entity.BaseEntity.BaseView;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - Sheet 入库制单
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class Sheet extends BaseEntity<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * "订单锁"缓存名称
	 */
	public static final String ORDER_LOCK_CACHE_NAME = "sheetLock";
	/**
	 * 状态
	 */
	public enum Status {

		/**
		 * 等待审核
		 */
		pendingReview,

		/**
		 * 审核通过
		 */
		audited,

		/**
		 * 拒绝
		 */
		denied
	}
	
	/**
	 * 编号
	 */
	@JsonView(BaseView.class)
	@Column(nullable = false, updatable = false, unique = true)
	private String sn;
	
	/**
	 * 创建人
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, updatable = false,name="admin")
	private Admin admin;
	/**
	 * 创建人姓名
	 */
	@JoinColumn(nullable = false, updatable = false,name="create_name")
	private String createName;
	/**
	 * 審核人
	 */
	@JoinColumn(nullable = true, updatable = false,name="auditor_name")
	private String auditor;
	
	/**
	 * 修改人姓名
	 */
	@JoinColumn(nullable = true, updatable = true,name="modify_name")
	private String modifyName;

	/**
	 * 状态
	 */
	@JsonView(BaseView.class)
	@Column(nullable = false)
	private Sheet.Status status;
	/**
	 * 过期时间
	 */
	private Date expire;
	/**
	 * 入库单项
	 */
	@JsonView(BaseView.class)
	@OneToMany(mappedBy = "sheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OrderBy("type asc")
	private List<SheetItem> sheetItems = new ArrayList<>();
	/**
	 * 国家
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="country", referencedColumnName="name_cn")
	private Country country;
	
	/**
	 * 获取入库单号
	 * @return
	 */
	public String getSn() {
		return sn;
	}
	/**
	 * 设置入库单号
	 * @return
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}
	/**
	 * 获取创建人
	 * @return
	 */
	public Admin getAdmin() {
		return admin;
	}
	/**
	 * 设置创建人
	 * @return
	 */
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	/**
	 * 获取審核人
	 * @return
	 */
	public String getAuditor() {
		return auditor;
	}
	/**
	 * 設置審核人
	 * @return
	 */
	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}
	/**
	 * 获取创建人姓名
	 * @return
	 */
	public String getCreateName() {
		return createName;
	}
	/**
	 * 设置创建人姓名
	 * @return
	 */
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	/**
	 * 获取修改人姓名
	 * @return
	 */
	public String getModifyName() {
		return modifyName;
	}
	/**
	 * 设置修改人姓名
	 * @return
	 */
	public void setModifyName(String modifyName) {
		this.modifyName = modifyName;
	}
	/**
	 * 获取入库单状态
	 * @return
	 */
	public Sheet.Status getStatus() {
		return status;
	}
	/**
	 * 设置入库单状态
	 * @return
	 */
	public void setStatus(Sheet.Status status) {
		this.status = status;
	}
	/**
	 * 获取过期时间
	 * @return
	 */
	public Date getExpire() {
		return expire;
	}
	/**
	 * 设置过期时间
	 * @return
	 */
	public void setExpire(Date expire) {
		this.expire = expire;
	}
	/**
	 * 获取入库单项
	 * 
	 * @return 入库单项
	 */
	@Transient
	public List<SheetItem> getSheetItems() {
		return sheetItems;
	}
	/**
	 * 设置入库单项
	 * 
	 * @return 入库单项
	 */
	public void setSheetItems(List<SheetItem> sheetItems) {
		this.sheetItems = sheetItems;
	}
	/**
	 * 获取国家
	 * @return
	 */
	public Country getCountry() {
		return country;
	}
	/**
	 * 设置国家
	 * @param country
	 */
	public void setCountry(Country country) {
		this.country = country;
	}
	/**
	 * 获取订单项
	 * 
	 * @param sn
	 *            SKU编号
	 * @return 订单项
	 */
	@Transient
	public SheetItem getSheetItem(String sn) {
		if (StringUtils.isEmpty(sn) || CollectionUtils.isEmpty(getSheetItems())) {
			return null;
		}
		for (SheetItem sheetItem : getSheetItems()) {
			if (sheetItem != null && StringUtils.equalsIgnoreCase(sheetItem.getSn(), sn)) {
				return sheetItem;
			}
		}
		return null;
	}
	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	@JsonView(BaseView.class)
	@Transient
	public boolean hasExpired() {
		return getExpire() != null && !getExpire().after(new Date());
	}
}
