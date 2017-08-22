package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Entity - 语言
 * 
 * @author gaoxaing
 * @version 5.0.3
 */
@Entity
public class Language extends OrderedEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 点击数缓存名称
	 */
	public static final String LANGUAGE_CACHE_NAME = "language";
	
	/**
	 * 编码
	 */
	@Column
	private String code;

	/**
	 * 名称
	 */
	@Column
	private String name;
	
	/**
	 * 语言
	 */
	@Column
	private String locale;
	
	/**
	 * 国际化key
	 */
	@Column
	private String message;
	
	/**
	 * state
	 */
	@Column
	private String state;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}