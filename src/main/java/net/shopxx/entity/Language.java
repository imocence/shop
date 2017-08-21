package net.shopxx.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

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
	 * 编码
	 */
	@NotEmpty
	@Length(max = 10)
	@Column(nullable = false)
	private String code;

	/**
	 * 名称
	 */
	@NotEmpty
	@Length(max = 60)
	@Column(nullable = false)
	private String name;
	
	/**
	 * 语言
	 */
	@NotEmpty
	@Length(max = 60)
	@Column(nullable = false)
	private String locale;
	
	/**
	 * 国际化key
	 */
	@NotEmpty
	@Length(max = 60)
	@Column(nullable = false)
	private String message;

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
}