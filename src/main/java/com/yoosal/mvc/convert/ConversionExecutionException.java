/*
 * Copyright 2004-2008 the original author or authors.
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
package com.yoosal.mvc.convert;

public class ConversionExecutionException extends com.yoosal.mvc.convert.ConversionException {

    private transient Object value;

    private Class sourceClass;

    private Class targetClass;

    public ConversionExecutionException(Object value, Class sourceClass, Class targetClass, Throwable cause) {
        super(defaultMessage(value, sourceClass, targetClass, cause), cause);
        this.value = value;
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    public ConversionExecutionException(Object value, Class sourceClass, Class targetClass, String message) {
        super(message);
        this.value = value;
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    public Object getValue() {
        return value;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    private static String defaultMessage(Object value, Class sourceClass, Class targetClass, Throwable cause) {
        return "Unable to convert value '" + value + "' from type '" + sourceClass.getName()
                + "' to type '" + targetClass.getName() + "'; reason = '" + cause.getMessage() + "'";
    }

}