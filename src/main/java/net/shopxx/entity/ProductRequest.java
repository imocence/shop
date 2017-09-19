package net.shopxx.entity;


import java.util.Date;

import net.shopxx.util.Excel;

/**
 * create by znr48268 on 2017/6/16
 */
public class ProductRequest  {

    @Excel(name = "Brand.country")
    private String contryName;
    
    @Excel(name = "Product.sn")
    private String  sn;
    
    @Excel(name = "Product.name")
    private String name;
    
    @Excel(name = "Product.productCategory")
    private String productCategory;
    
    @Excel(name = "Product.isMarketable")
    private String isMarketable;
    
    @Excel(name = "Product.isTop")
    private String isTop;
    
    @Excel(name = "admin.common.createdDate")
    private Date createdDate;

    public String getContryName() {
        return contryName;
    }

    public void setContryName(String contryName) {
        this.contryName = contryName;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getIsMarketable() {
        return isMarketable;
    }

    public void setIsMarketable(String isMarketable) {
        this.isMarketable = isMarketable;
    }

    public String getIsTop() {
        return isTop;
    }

    public void setIsTop(String isTop) {
        this.isTop = isTop;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    
  
}
