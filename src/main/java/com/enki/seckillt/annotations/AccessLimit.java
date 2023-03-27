package com.enki.seckillt.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 接口限流
 * @author Enki
 * @Version 1.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
	/**
	 * 限制周期(秒)
	 * @return
	 */
	int seconds();

	/**
	 * 规定周期内限制次数
	 * @return
	 */
	int maxCount();
	boolean needLogin() default true;
}
