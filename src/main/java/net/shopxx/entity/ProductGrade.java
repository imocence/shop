/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.NumericField;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.annotation.JsonView;

import net.shopxx.BigDecimalNumericFieldBridge;
import net.shopxx.entity.BaseEntity.BaseView;

/**
 * Entity - 产品等级表
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class ProductGrade extends BaseEntity<Long> {

    private static final long serialVersionUID = 2673602067029665976L;


    /**
     * 国家
     */
    @JsonView(BaseView.class)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(nullable = true)
    private MemberRank           grade;
    
    /**
     * 产品
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(nullable = true)
    private Product           product;
    
    /**
     * 销售价
     */
    @JsonView(BaseView.class)
    @Field(store = Store.YES, index = Index.YES, analyze = Analyze.NO)
    @NumericField
    @FieldBridge(impl = BigDecimalNumericFieldBridge.class)
    @Column(nullable = false, precision = 21, scale = 2)
    private BigDecimal price;
    
    /**
     * 券
     */
    @JsonView(BaseView.class)
    @Field(store = Store.YES, index = Index.YES, analyze = Analyze.NO)
    @NumericField
    @FieldBridge(impl = BigDecimalNumericFieldBridge.class)
    @Column(nullable = false, precision = 21, scale = 2)
    private BigDecimal coupon;
    
    
    /**
     * 是否可购买 1，可以，非0，可以
     */
    @JsonView(BaseView.class)
    @Column(nullable = false)
    private Integer   buy;
    
    /**
     * 是否可查看 1，可以，非0，可以
     */
    @JsonView(BaseView.class)
    @Column(nullable = false)
    private Integer   see;
    


    public MemberRank getGrade() {
        return grade;
    }

    public void setGrade(MemberRank grade) {
        this.grade = grade;
    }

   
   

    public BigDecimal getCoupon() {
        return coupon;
    }

    public void setCoupon(BigDecimal coupon) {
        this.coupon = coupon;
    }

    public Integer getBuy() {
        return buy;
    }

    public void setBuy(Integer buy) {
        this.buy = buy;
    }

    public Integer getSee() {
        return see;
    }

    public void setSee(Integer see) {
        this.see = see;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    

}