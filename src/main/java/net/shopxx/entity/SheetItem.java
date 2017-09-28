package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import net.shopxx.entity.BaseEntity.BaseView;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - 入库单关联商品
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class SheetItem extends BaseEntity<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 入库单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sheets", nullable = false, updatable = false)
	private Sheet sheet;
	/**
	 * 商品编号
	 */
	@Column(nullable = false, updatable = false)
	private String sn;
	/**
	 * 名称
	 */
	@JsonView(BaseView.class)
	@Column(nullable = false, updatable = false)
	private String name;
	/**
	 * 数量
	 */
	@Column(nullable = false, updatable = false)
	private Integer quantity;
	/**
	 * 类型
	 */
	@Column(nullable = false, updatable = false)
	private Product.Type type;
	/**
	 * SKU
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private Sku sku;
	
	
	/**
	 * 获取入库单
	 * @return
	 */
	public Sheet getSheet() {
		return sheet;
	}
	/**
	 * 设置入库单
	 * @param sheet
	 */
	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}
	/**
	 * 获取商品编号
	 * @return
	 */
	public String getSn() {
		return sn;
	}
	/**
	 * 设置商品编号
	 * @param sn
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}
	/**
	 * 获取商品名
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置商品名
	 * @return
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 设置数量
	 * @return
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * 设置数量
	 * @return
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	/**
	 *	获取类型
	 * @return
	 */
	public Product.Type getType() {
		return type;
	}
	/**
	 * 设置类型
	 * @return
	 */
	public void setType(Product.Type type) {
		this.type = type;
	}
	/**
	 * 获取库存
	 * @return
	 */
	public Sku getSku() {
		return sku;
	}
	/**
	 * 设置库存
	 * @return
	 */
	public void setSku(Sku sku) {
		this.sku = sku;
	}
}
