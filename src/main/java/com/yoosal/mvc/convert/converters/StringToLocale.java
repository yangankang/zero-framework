package com.yoosal.mvc.convert.converters;

import com.yoosal.common.StringUtils;
import java.util.Locale;

public class StringToLocale extends StringToObject {

    public StringToLocale() {
        super(Locale.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return StringUtils.parseLocaleString(string);
    }

    public String toString(Object object) throws Exception {
        Locale locale = (Locale) object;
        return locale.toString();
    }

}