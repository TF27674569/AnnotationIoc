package org.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * description��
 * <p>
 * Created by TIAN FENG on 2017/12/19.
 * QQ��27674569
 * Email: 27674569@qq.com
 * Version��1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface EchoEnable {
    long value() default 200;
}
