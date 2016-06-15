package com.yoosal.orm.dialect;

import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ValuesForPrepared {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";
    private String sql;
    private String[] keys;
    private Map<String, Object> values = new HashMap<String, Object>();

    public String getSql() {
        String[] strings = getKeys();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            sql = sql.replaceFirst(":" + s, "?");
        }
        return sql;
    }

    public void setPrepared(PreparedStatement statement) throws SQLException {
        String[] strings = getKeys();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            Object o = values.get(":" + s);
            o = isDate(o);
            statement.setObject(i + 1, o);
        }
    }

    private Object isDate(Object o) {
        if (o.getClass().isAssignableFrom(Date.class)) {

            String dateFormatString = DEFAULT_DATE_FORMAT;
            if (StringUtils.isNotBlank(OperationManager.getDateFormat())) {
                dateFormatString = OperationManager.getDateFormat();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
            return dateFormat.format(o);
        }
        return o;
    }

    public void setPrepared(PreparedStatement statement, ModelObject object) throws SQLException {
        String[] strings = getKeys();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            statement.setObject(i + 1, object.get(s.substring(1, s.length())));
        }
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String[] getKeys() {
        if (keys == null) {
            keys = wildcardString();
        }
        return keys;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void addValue(String key, Object value) {
        values.put(key, value);
    }

    private String[] wildcardString() {
        String[] s1 = sql.split(":");
        List<String> strings = new ArrayList<String>();
        for (String s : s1) {
            if (s1[0] == s) {
                continue;
            }
            String[] s2 = s.split(":");
            for (String s3 : s2) {
                char[] chars = s3.toCharArray();
                int i = 0;
                StringBuilder sb = new StringBuilder();
                while (true) {
                    if (i >= chars.length) {
                        break;
                    }
                    if ((chars[i] >= 'a' && chars[i] <= 'z') ||
                            (chars[i] >= 'A' && chars[i] <= 'Z')
                            || chars[i] == '_' || Character.isDigit(chars[i])) {
                        sb.append(chars[i]);
                    } else {
                        break;
                    }
                    i++;
                }
                strings.add(sb.toString());
            }
        }
        String[] s = new String[strings.size()];
        strings.toArray(s);
        return s;
    }
}
