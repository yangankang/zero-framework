package com.yoosal.orm.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";

    Class type() default String.class;

    long length() default 0;

    boolean key() default false;

    boolean allowNull() default true;

    DefaultValue defaultValue() default @DefaultValue(enable = false);

    boolean index() default false;

    String indexName() default "";

    /**
     * 主键生成策略，默认是数据库，比如mysql的自增长
     *
     * @return
     */
    Class strategy() default Column.class;

    /**
     * 锁定的值，添加后再也参与修改
     *
     * @return
     */
    boolean lock() default false;
}
