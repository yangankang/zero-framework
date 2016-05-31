package com.yoosal.orm.dialect;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public abstract class StandardSQL implements SQLDialect {
    protected static final Map<Integer, String> types = new HashMap<Integer, String>();
    protected static final Map<Class, String> typesMapping = new HashMap<Class, String>();

    static {
        typesMapping.put(String.class, "VARCHAR");
        typesMapping.put(char.class, "CHAR");

        Field[] fields = Types.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                Integer code = (Integer) field.get(null);
                types.put(code, field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getType(int columnTypeInt) {
        return types.get(columnTypeInt);
    }

}
