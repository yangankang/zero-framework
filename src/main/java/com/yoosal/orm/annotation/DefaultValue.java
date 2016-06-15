package com.yoosal.orm.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {
    double intValue() default 0;

    String stringValue() default "";

    boolean enable() default true;
}
