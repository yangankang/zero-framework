package com.yoosal.mvc.convert.converters;

public class StringToBoolean extends com.yoosal.mvc.convert.converters.StringToObject {

    private static final String VALUE_TRUE = "true";

    private static final String VALUE_FALSE = "false";

    private String trueString;

    private String falseString;

    public StringToBoolean() {
        super(Boolean.class);
    }

    public StringToBoolean(String trueString, String falseString) {
        super(Boolean.class);
        this.trueString = trueString;
        this.falseString = falseString;
    }

    protected Object toObject(String string, Class targetClass) throws Exception {
        if (trueString != null && string.equals(trueString)) {
            return Boolean.TRUE;
        } else if (falseString != null && string.equals(falseString)) {
            return Boolean.FALSE;
        } else if (trueString == null && string.equals(VALUE_TRUE)) {
            return Boolean.TRUE;
        } else if (falseString == null && string.equals(VALUE_FALSE)) {
            return Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("Invalid boolean value [" + string + "]");
        }
    }

    protected String toString(Object object) throws Exception {
        Boolean value = (Boolean) object;
        if (Boolean.TRUE.equals(value)) {
            if (trueString != null) {
                return trueString;
            } else {
                return VALUE_TRUE;
            }
        } else if (Boolean.FALSE.equals(value)) {
            if (falseString != null) {
                return falseString;
            } else {
                return VALUE_FALSE;
            }
        } else {
            throw new IllegalArgumentException("Invalid boolean value [" + value + "]");
        }
    }

}