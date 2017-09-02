package net.shopxx.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 国家实体
 * 
 * @author Fred.xu
 * @version v0.0.1: Country.java, v 0.1 2017年8月23日 下午1:51:13 Fred.xu Exp $
 */
@Entity
public class Country extends OrderedEntity<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 名称
     */
    @NotEmpty
    @Length(max = 200)
    @Column(name = "name_cn", nullable = false)
    private String    name;

    /**
     * 本地名称
     */
    @NotEmpty
    @Length(max = 200)
    @Column(name = "name_local", nullable = false)
    private String    nameLocal;

    /**
     * 名称
     */
    @Column(nullable = false)
    private Integer   state;

    /**
     * 下级地区
     */
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    @OrderBy("order asc")
    private Set<Area> areas = new HashSet<>();
    
    /**
     * 下级地区
     */
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    @OrderBy("order asc")
    private Set<ProductCategory> productCategories = new HashSet<>();
    
    
    /**
     * 等级
     */
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    @OrderBy("order asc")
    private Set<Grade> grades = new HashSet<>();
    
    /**
     * 下级地品牌
     */
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    @OrderBy("order asc")
    private Set<Brand> brands = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLocal() {
        return nameLocal;
    }

    public void setNameLocal(String nameLocal) {
        this.nameLocal = nameLocal;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Set<Area> getAreas() {
        return areas;
    }

    public void setAreas(Set<Area> areas) {
        this.areas = areas;
    }

    public Set<Brand> getBrands() {
        return brands;
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    
}
