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
package com.yoosal.mvc.convert.service;

import com.yoosal.common.Assert;
import com.yoosal.mvc.convert.ConversionExecutionException;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.ConversionService;


public class RuntimeBindingConversionExecutor implements ConversionExecutor {

    private Class targetClass;

    private ConversionService conversionService;

    public RuntimeBindingConversionExecutor(Class targetClass, ConversionService conversionService) {
        Assert.notNull(targetClass, "The target class of the conversion is required");
        Assert.notNull(conversionService, "The conversion service is required");
        this.targetClass = targetClass;
        this.conversionService = conversionService;
    }

    public Class getSourceClass() {
        return null;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof RuntimeBindingConversionExecutor)) {
            return false;
        }
        RuntimeBindingConversionExecutor o = (RuntimeBindingConversionExecutor) obj;
        return targetClass.equals(o.targetClass);
    }

    public int hashCode() {
        return targetClass.hashCode();
    }

    public Object execute(Object source) throws ConversionExecutionException {
        return execute(source, null);
    }

    public Object execute(Object source, Object context) throws ConversionExecutionException {
        return conversionService.getConversionExecutor(source.getClass(), targetClass).execute(source);
    }

}