package com.yoosal.mvc.convert.converters;

import com.yoosal.common.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringToDate extends StringToObject {

    private static Log logger = LogFactory.getLog(StringToDate.class);

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

    private String pattern;

    private Locale locale = Locale.getDefault();

    public StringToDate() {
        super(Date.class);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Object toObject(String string, Class targetClass) throws Exception {
        if (!StringUtils.hasText(string)) {
            return null;
        }
        DateFormat dateFormat = getDateFormat();
        try {
            return dateFormat.parse(string);
        } catch (ParseException e) {
            throw new InvalidFormatException(string, getPattern(dateFormat), e);
        }
    }

    public String toString(Object target) throws Exception {
        Date date = (Date) target;
        if (date == null) {
            return "";
        }
        return getDateFormat().format(date);
    }

    protected DateFormat getDateFormat() {
        Locale locale = determineLocale(this.locale);
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        format.setLenient(false);
        if (format instanceof SimpleDateFormat) {
            String pattern = determinePattern(this.pattern);
            ((SimpleDateFormat) format).applyPattern(pattern);
        } else {
            logger.warn("Unable to apply format pattern '" + pattern
                    + "'; Returned DateFormat is not a SimpleDateFormat");
        }
        return format;
    }

    private String determinePattern(String pattern) {
        return pattern != null ? pattern : DEFAULT_PATTERN;
    }

    private Locale determineLocale(Locale locale) {
        return locale;
    }

    private String getPattern(DateFormat format) {
        if (format instanceof SimpleDateFormat) {
            return ((SimpleDateFormat) format).toPattern();
        } else {
            logger.warn("Pattern string cannot be determined because DateFormat is not a SimpleDateFormat");
            return "defaultDateFormatInstance";
        }
    }

}