package com.jz.jzpicture.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description:
 * @Author: ASL_ly
 * @Package: com.jz.jzpicture.annotation
 * @Project: jz-picture
 * @Date: 2025/2/6  9:57
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须具有某个角色
     */
    String mustRole() default "";
}


