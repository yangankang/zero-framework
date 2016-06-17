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

import java.util.Set;

public interface ConversionService {

    Object executeConversion(Object source, Class targetClass) throws ConversionException;

    Object executeConversion(String converterId, Object source, Class targetClass);

    ConversionExecutor getConversionExecutor(Class sourceClass, Class targetClass)
            throws ConversionExecutorNotFoundException;

    ConversionExecutor getConversionExecutor(String id, Class sourceClass, Class targetClass)
            throws ConversionExecutorNotFoundException;

    Set getConversionExecutors(Class sourceClass);

    Class getClassForAlias(String alias);

    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    <T> T convert(Object source, Class<T> targetType) throws Exception;

}