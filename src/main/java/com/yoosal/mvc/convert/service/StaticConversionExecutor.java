package com.yoosal.mvc.convert.service;

import com.yoosal.common.Assert;
import com.yoosal.mvc.convert.ConversionExecutionException;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.converters.Converter;

public class StaticConversionExecutor implements ConversionExecutor {

    private final Class sourceClass;

    private final Class targetClass;

    private final Converter converter;

    public StaticConversionExecutor(Class sourceClass, Class targetClass, Converter converter) {
        Assert.notNull(sourceClass, "The source class is required");
        Assert.notNull(targetClass, "The target class is required");
        Assert.notNull(converter, "The converter is required");
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.converter = converter;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Converter getConverter() {
        return converter;
    }

    public Object execute(Object source) throws ConversionExecutionException {
        if (source != null && !sourceClass.isInstance(source)) {
            throw new ConversionExecutionException(source, getSourceClass(), getTargetClass(), "Source object "
                    + source + " to convert is expected to be an instance of [" + getSourceClass().getName() + "]");
        }
        try {
            return converter.convertSourceToTargetClass(source, targetClass);
        } catch (Exception e) {
            throw new ConversionExecutionException(source, getSourceClass(), getTargetClass(), e);
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof StaticConversionExecutor)) {
            return false;
        }
        StaticConversionExecutor other = (StaticConversionExecutor) o;
        return sourceClass.equals(other.sourceClass) && targetClass.equals(other.targetClass);
    }

    public int hashCode() {
        return sourceClass.hashCode() + targetClass.hashCode();
    }

    public String toString() {
        /*return new ToStringCreator(this).append("sourceClass", sourceClass).append("targetClass", targetClass)
                .toString();*/
        return super.toString();
    }
}