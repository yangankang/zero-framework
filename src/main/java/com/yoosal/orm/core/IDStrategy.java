package com.yoosal.orm.core;

import com.yoosal.orm.mapping.ColumnModel;

public interface IDStrategy {
    String getOne(ColumnModel column);
}
