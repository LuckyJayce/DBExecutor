package com.shizhefei.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过这个设置字段对应的名字，和默认值
 * 
 * @author 试着飞 Date: 13-11-18
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String column() default "";

	String defaultValue() default "";
}
