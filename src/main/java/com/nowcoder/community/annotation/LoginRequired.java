package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//声明注解作用的位置是方法上
@Retention(RetentionPolicy.RUNTIME)//声明注解生效的时间是程序运行时
public @interface LoginRequired {



}
