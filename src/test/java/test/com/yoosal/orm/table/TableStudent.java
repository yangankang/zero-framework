package test.com.yoosal.orm.table;

import com.yoosal.orm.annotation.AutoIncrementStrategy;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.DefaultValue;
import com.yoosal.orm.annotation.Table;

@Table
public enum TableStudent {
    @Column(key = true, index = true, strategy = AutoIncrementStrategy.class)
    idColumn,
    @Column(index = true)
    nameForAccount,
    @Column(type = Integer.class,comment = "学生年龄")
    age,
    @Column(type = Integer.class, allowNull = false, defaultValue = @DefaultValue(intValue = 1))
    sex,
    @Column(comment = "增加字段测试")
    b
}
