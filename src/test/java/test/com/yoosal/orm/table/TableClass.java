package test.com.yoosal.orm.table;

import com.yoosal.orm.annotation.AutoIncrementStrategy;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.Table;

@Table
public enum TableClass {
    @Column(key = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(length = 128)
    className
}
