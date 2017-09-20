package net.shopxx.util;

import java.lang.annotation.Retention;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel注解，用以生成Excel表格文件
 * 
 * @author Fred.xu
 * @version v0.0.1: Excel.java, v 0.1 2017年6月21日 上午9:55:05 Fred.xu Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Excel {

    //列名
    String name() default "";

    //宽度
    int width() default 20;

    //忽略该字段
    boolean skip() default false;

}