package com.yoosal.orm.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";

    Class type() default String.class;

    long length() default 0;

    /**
     * 是否是主键，0表示不是，大于0则是主键，如果有
     * 多个主键则根据数字排序，作用于Query的ID操作时
     * 传入的值得顺序
     *
     * @return
     */
    int key() default 0;

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
