package com.yoosal.orm.dialect;

import com.yoosal.orm.ModelObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValuesForPrepared {
    private String sql;
    private String[] keys;
    private Map<String, Object> values = new HashMap<String, Object>();

    public String getSql() {
        String[] strings = getKeys();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            sql.replace(s, "?");
        }
        return sql;
    }

    public void setPrepared(PreparedStatement statement) throws SQLException {
        String[] strings = getKeys();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            statement.setObject(i + 1, values.get(s));
        }
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
            String[] s2 = s.split(":");
            for (String s3 : s2) {
                char[] chars = s3.toCharArray();
                int i = 0;
                StringBuilder sb = new StringBuilder();
                while (true) {
                    if ((chars[i] > 'a' && chars[i] < 'z') ||
                            (chars[i] > 'A' && chars[i] < 'Z') || chars[i] == '_') {
                        sb.append(chars[i]);
                    } else {
                        break;
                    }
                    i++;
                }
                strings.add(sb.toString());
            }
        }
        return (String[]) strings.toArray();
    }
}
