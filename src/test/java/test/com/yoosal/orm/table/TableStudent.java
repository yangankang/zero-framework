package test.com.yoosal.orm.table;

import com.yoosal.orm.annotation.AutoIncrementStrategy;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.Table;

@Table
public enum TableStudent {
    @Column(key = true, index = true, strategy = AutoIncrementStrategy.class)
    idColumn,
    @Column(index = true)
    nameForAccount,
    @Column(type = Integer.class)
    age
}
