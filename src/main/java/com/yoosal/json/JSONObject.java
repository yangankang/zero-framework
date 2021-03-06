/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yoosal.json;

import static com.yoosal.json.util.TypeUtils.castToBigDecimal;
import static com.yoosal.json.util.TypeUtils.castToBigInteger;
import static com.yoosal.json.util.TypeUtils.castToBoolean;
import static com.yoosal.json.util.TypeUtils.castToByte;
import static com.yoosal.json.util.TypeUtils.castToBytes;
import static com.yoosal.json.util.TypeUtils.castToDate;
import static com.yoosal.json.util.TypeUtils.castToDouble;
import static com.yoosal.json.util.TypeUtils.castToFloat;
import static com.yoosal.json.util.TypeUtils.castToInt;
import static com.yoosal.json.util.TypeUtils.castToLong;
import static com.yoosal.json.util.TypeUtils.castToShort;
import static com.yoosal.json.util.TypeUtils.castToSqlDate;
import static com.yoosal.json.util.TypeUtils.castToTimestamp;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.yoosal.json.annotation.JSONField;
import com.yoosal.json.parser.ParserConfig;
import com.yoosal.json.util.TypeUtils;

/**
 * @author wenshao[szujobs@hotmail.com]
 */
@SuppressWarnings("serial")
public class JSONObject extends JSON implements Map<Object, Object>, Cloneable, Serializable, InvocationHandler {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private final Map<Object, Object> map;

    public JSONObject() {
        this(DEFAULT_INITIAL_CAPACITY, false);
    }

    public JSONObject(Map<Object, Object> map) {
        this.map = map;
    }

    public JSONObject(boolean ordered) {
        this(DEFAULT_INITIAL_CAPACITY, ordered);
    }

    public JSONObject(int initialCapacity) {
        this(initialCapacity, false);
    }

    public JSONObject(int initialCapacity, boolean ordered) {
        if (ordered) {
            map = new LinkedHashMap<Object, Object>(initialCapacity);
        } else {
            map = new HashMap<Object, Object>(initialCapacity);
        }
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(String.valueOf(key));
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(Object key) {
        return map.get(String.valueOf(key));
    }

    public JSONObject getJSONObject(Object key) {
        Object value = map.get(String.valueOf(key));

        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }

        return (JSONObject) toJSON(value);
    }

    public JSONArray getJSONArray(Object key) {
        Object value = map.get(String.valueOf(key));

        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }

        return (JSONArray) toJSON(value);
    }

    public <T> T getObject(Object key, Class<T> clazz) {
        Object obj = map.get(String.valueOf(key));
        return TypeUtils.castToJavaBean(obj, clazz);
    }

    public Boolean getBoolean(String key) {
        Object value = get(key);

        if (value == null) {
            return null;
        }

        return castToBoolean(value);
    }

    public byte[] getBytes(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return null;
        }

        return castToBytes(value);
    }

    public boolean getBooleanValue(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return false;
        }

        return castToBoolean(value).booleanValue();
    }

    public Byte getByte(Object key) {
        Object value = get(String.valueOf(key));

        return castToByte(value);
    }

    public byte getByteValue(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return 0;
        }

        return castToByte(value).byteValue(); // TODO 如果 value 是""、"null"或"NULL"，可能会存在报空指针的情况，是否需要加以处理？ 其他转换也存在类似情况
    }

    public Short getShort(Object key) {
        Object value = get(String.valueOf(key));

        return castToShort(value);
    }

    public short getShortValue(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return 0;
        }

        return castToShort(value).shortValue();
    }

    public Integer getInteger(Object key) {
        Object value = get(String.valueOf(key));

        return castToInt(value);
    }

    public int getIntValue(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return 0;
        }

        return castToInt(value).intValue();
    }

    public Long getLong(Object key) {
        Object value = get(String.valueOf(key));

        return castToLong(value);
    }

    public long getLongValue(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return 0L;
        }

        return castToLong(value).longValue();
    }

    public Float getFloat(Object key) {
        Object value = get(String.valueOf(key));

        return castToFloat(value);
    }

    public float getFloatValue(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return 0F;
        }

        return castToFloat(value).floatValue();
    }

    public Double getDouble(Object key) {
        Object value = get(String.valueOf(key));

        return castToDouble(value);
    }

    public double getDoubleValue(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return 0D;
        }

        return castToDouble(value);
    }

    public BigDecimal getBigDecimal(Object key) {
        Object value = get(String.valueOf(key));

        return castToBigDecimal(value);
    }

    public BigInteger getBigInteger(Object key) {
        Object value = get(String.valueOf(key));

        return castToBigInteger(value);
    }

    public String getString(Object key) {
        Object value = get(String.valueOf(key));

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public Date getDate(Object key) {
        Object value = get(String.valueOf(key));

        return castToDate(value);
    }

    public java.sql.Date getSqlDate(Object key) {
        Object value = get(String.valueOf(key));

        return castToSqlDate(value);
    }

    public java.sql.Timestamp getTimestamp(Object key) {
        Object value = get(String.valueOf(key));

        return castToTimestamp(value);
    }

    public Object put(Object key, Object value) {
        return map.put(String.valueOf(key), value);
    }

    public JSONObject fluentPut(Object key, Object value) {
        map.put(String.valueOf(key), value);
        return this;
    }

    public void putAll(Map<? extends Object, ? extends Object> m) {
        map.putAll(m);
    }

    public JSONObject fluentPutAll(Map<? extends Object, ? extends Object> m) {
        map.putAll(m);
        return this;
    }

    public void clear() {
        map.clear();
    }

    public JSONObject fluentClear() {
        map.clear();
        return this;
    }

    public Object remove(Object key) {
        return map.remove(String.valueOf(key));
    }

    public JSONObject fluentRemove(Object key) {
        map.remove(String.valueOf(key));
        return this;
    }

    public Set<Object> keySet() {
        return map.keySet();
    }

    public Collection<Object> values() {
        return map.values();
    }

    public Set<Entry<Object, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Object clone() {
        return new JSONObject(map instanceof LinkedHashMap //
                ? new LinkedHashMap<Object, Object>(map) //
                : new HashMap<Object, Object>(map)
        );
    }

    public boolean equals(Object obj) {
        return this.map.equals(obj);
    }

    public int hashCode() {
        return this.map.hashCode();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1) {
            if (method.getName().equals("equals")) {
                return this.equals(args[0]);
            }

            Class<?> returnType = method.getReturnType();
            if (returnType != void.class) {
                throw new JSONException("illegal setter");
            }

            String name = null;
            JSONField annotation = method.getAnnotation(JSONField.class);
            if (annotation != null) {
                if (annotation.name().length() != 0) {
                    name = annotation.name();
                }
            }

            if (name == null) {
                name = method.getName();

                if (!name.startsWith("set")) {
                    throw new JSONException("illegal setter");
                }

                name = name.substring(3);
                if (name.length() == 0) {
                    throw new JSONException("illegal setter");
                }
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }

            map.put(name, args[0]);
            return null;
        }

        if (parameterTypes.length == 0) {
            Class<?> returnType = method.getReturnType();
            if (returnType == void.class) {
                throw new JSONException("illegal getter");
            }

            String name = null;
            JSONField annotation = method.getAnnotation(JSONField.class);
            if (annotation != null) {
                if (annotation.name().length() != 0) {
                    name = annotation.name();
                }
            }

            if (name == null) {
                name = method.getName();
                if (name.startsWith("get")) {
                    name = name.substring(3);
                    if (name.length() == 0) {
                        throw new JSONException("illegal getter");
                    }
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                } else if (name.startsWith("is")) {
                    name = name.substring(2);
                    if (name.length() == 0) {
                        throw new JSONException("illegal getter");
                    }
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                } else if (name.startsWith("hashCode")) {
                    return this.hashCode();
                } else if (name.startsWith("toString")) {
                    return this.toString();
                } else {
                    throw new JSONException("illegal getter");
                }
            }

            Object value = map.get(name);
            return TypeUtils.cast(value, method.getGenericReturnType(), ParserConfig.getGlobalInstance());
        }

        throw new UnsupportedOperationException(method.toGenericString());
    }
}
