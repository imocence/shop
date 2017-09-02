/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class Grade extends OrderedEntity<Long> {

    private static final long serialVersionUID = 2673602067029665976L;

    /**
     * 等级名称
     */
    @JsonView(BaseView.class)
    @NotEmpty
    @Length(max = 200)
    @Column(nullable = false)
    private String            name;

    /**
     * 国家
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(nullable = true)
    private Country           country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}