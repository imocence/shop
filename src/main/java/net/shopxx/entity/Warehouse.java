package net.shopxx.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity - 仓库
 * 
 * @author cht
 * @version 1.0.0
 */
@Entity
public class Warehouse extends OrderedEntity<Long>{
	private static final long serialVersionUID = 4136507336496569742L;
	
	/**
	 * 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	private String name;

	/**
	 * 编号
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	private String code;
	
	/**
	 * 名称
	 */
	@NotEmpty
	@Length(max = 400)
	@Column(nullable = true)
	private String address;
	
	/**
	 * 备注
	 */
	@Length(max = 200)
	private String memo;
	
	/**
     * 国家
     */
    @ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.REFRESH)
    @JoinColumn(nullable = true)
    private Country country;
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}
}
