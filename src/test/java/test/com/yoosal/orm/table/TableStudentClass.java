package test.com.yoosal.orm.table;

import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.Table;

@Table
public enum TableStudentClass {
    @Column(key = true)
    studentId,
    @Column(key = true)
    classId
}
