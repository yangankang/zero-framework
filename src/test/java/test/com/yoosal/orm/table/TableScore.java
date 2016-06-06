package test.com.yoosal.orm.table;

import com.yoosal.orm.annotation.AutoIncrementStrategy;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.Table;

@Table
public enum TableScore {
    @Column(key = true, strategy = AutoIncrementStrategy.class)
    id,
    @Column(type = String.class, length = 128)
    className,
    @Column(type = Integer.class)
    score,
    @Column(type = Integer.class)
    studentId,
    @Column(type = Integer.class)
    classId
}
