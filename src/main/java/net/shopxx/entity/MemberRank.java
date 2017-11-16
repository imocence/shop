/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - 会员等级
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class MemberRank extends BaseEntity<Long> {

	private static final long serialVersionUID = 3599029355500655209L;
	/**
	 * 会员等级：0游客，1代理商，2服务中心，3加盟店，4中心店
	 * @author sihao
	 *
	 */
	public enum Type {
		
		/**
		 * 游客
		 */
		tourist,
		/**
		 * 代理商
		 */
		agent,
		/**
		 * 服务中心
		 */
		service,
		/**
		 * 加盟店
		 */
		franchise,
		
		/**
		 * 中心店
		 */
		mainstore,
		/**
		 * 注册用户
		 */
		register;
	}
	/**
	 * 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	private String name;

	/**
	 * 优惠比例
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 3, fraction = 3)
	@Column(nullable = false, precision = 12, scale = 6)
	private Double scale;

	/**
	 * 消费金额
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(precision = 21, scale = 6)
	private BigDecimal amount;
	/**
	 * 消费金额
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(name="first_single",columnDefinition="decimal(21,6) default 0.000000000000")
	private BigDecimal firstSingle;
	/**
	 * 消费金额
	 */
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(name="next_single",columnDefinition="decimal(21,6) default 0.000000000000")
	private BigDecimal nextSingle;

	

	/**
	 * 是否默认
	 */
	@JsonView(BaseView.class)
	@NotNull
	@Column(nullable = false)
	private Boolean isDefault;

	/**
	 * 是否特殊
	 */
	@NotNull
	@Column(nullable = false)
	private Boolean isSpecial;
	
	
	 /**
     * 国家
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(nullable = true)
    private Country  country;
    /**
	 * 类型，type=1,2,3,5：代理商，服务中心，加盟店，中心店
	 */
    @JsonView(BaseView.class)
    @Column(name="type")
	private MemberRank.Type type;
	

	/**
	 * 会员
	 */
	@OneToMany(mappedBy = "memberRank", fetch = FetchType.LAZY)
	private Set<Member> members = new HashSet<>();

	/**
	 * 促销
	 */
	@ManyToMany(mappedBy = "memberRanks", fetch = FetchType.LAZY)
	private Set<Promotion> promotions = new HashSet<>();

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取优惠比例
	 * 
	 * @return 优惠比例
	 */
	public Double getScale() {
		return scale;
	}

	/**
	 * 设置优惠比例
	 * 
	 * @param scale
	 *            优惠比例
	 */
	public void setScale(Double scale) {
		this.scale = scale;
	}

	/**
	 * 获取消费金额
	 * 
	 * @return 消费金额
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * 获取首单最低价
	 * @return
	 */
	public BigDecimal getFirstSingle() {
		return firstSingle;
	}
	/**
	 * 设置首单最低价
	 * @return
	 */
	public void setFirstSingle(BigDecimal firstSingle) {
		this.firstSingle = firstSingle;
	}
	/**
	 * 获取下一单最低价
	 * @return
	 */
	public BigDecimal getNextSingle() {
		return nextSingle;
	}
	/**
	 * 设置下一单最低价
	 * @return
	 */
	public void setNextSingle(BigDecimal nextSingle) {
		this.nextSingle = nextSingle;
	}
	/**
	 * 设置消费金额
	 * 
	 * @param amount
	 *            消费金额
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * 获取是否默认
	 * 
	 * @return 是否默认
	 */
	public Boolean getIsDefault() {
		return isDefault;
	}

	/**
	 * 设置是否默认
	 * 
	 * @param isDefault
	 *            是否默认
	 */
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * 获取是否特殊
	 * 
	 * @return 是否特殊
	 */
	public Boolean getIsSpecial() {
		return isSpecial;
	}

	/**
	 * 设置是否特殊
	 * 
	 * @param isSpecial
	 *            是否特殊
	 */
	public void setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Set<Member> getMembers() {
		return members;
	}

	/**
	 * 设置会员
	 * 
	 * @param members
	 *            会员
	 */
	public void setMembers(Set<Member> members) {
		this.members = members;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public Set<Promotion> getPromotions() {
		return promotions;
	}

	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 删除前处理
	 */
	@PreRemove
	public void preRemove() {
		Set<Promotion> promotions = getPromotions();
		if (promotions != null) {
			for (Promotion promotion : promotions) {
				promotion.getMemberRanks().remove(this);
			}
		}
	}

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
    /**
     * 获取会员类型
     * @return
     */
    public MemberRank.Type getType() {
		return type;
	}
    /**
     * 设置会员类型
     * @return
     */
	public void setType(MemberRank.Type type) {
		this.type = type;
	}

}