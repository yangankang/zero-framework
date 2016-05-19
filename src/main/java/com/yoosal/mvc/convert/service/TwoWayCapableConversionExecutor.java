/*
 * Copyright 2004-2007 the original author or authors.
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

import com.yoosal.mvc.convert.ConversionExecutionException;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.converters.TwoWayConverter;

class TwoWayCapableConversionExecutor implements ConversionExecutor {

    private Class sourceClass;

    private Class targetClass;

    private TwoWayConverter converter;

    public TwoWayCapableConversionExecutor(Class sourceClass, Class targetClass, TwoWayConverter converter) {
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

    public Object execute(Object source) throws ConversionExecutionException {
        if (source == null || converter.getSourceClass().isInstance(source)) {
            try {
                return converter.convertSourceToTargetClass(source, targetClass);
            } catch (Exception e) {
                throw new ConversionExecutionException(source, getSourceClass(), getTargetClass(), e);
            }
        } else if (converter.getTargetClass().isInstance(source)) {
            try {
                return converter.convertTargetToSourceClass(source, sourceClass);
            } catch (Exception e) {
                throw new ConversionExecutionException(source, converter.getTargetClass(), getSourceClass(), e);
            }
        } else {
            throw new ConversionExecutionException(source, getSourceClass(), getTargetClass(), "Source object "
                    + source + " to convert is expected to be an instance of [" + converter.getSourceClass().getName()
                    + "] or [" + converter.getTargetClass().getName() + "]");
        }
    }
}