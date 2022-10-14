package com.github.apinotice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName com.github.apinotice.Notice
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/10/13 09:27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Notice {
	String author();
	String email();
}
