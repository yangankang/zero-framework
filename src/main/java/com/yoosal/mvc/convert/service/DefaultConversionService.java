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

import com.yoosal.common.ClassUtils;
import com.yoosal.mvc.convert.converters.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

public class DefaultConversionService extends GenericConversionService {

    public DefaultConversionService() {
        addDefaultConverters();
        addDefaultAliases();
    }

    protected void addDefaultConverters() {
        addConverter(new StringToByte());
        addConverter(new StringToBoolean());
        addConverter(new StringToCharacter());
        addConverter(new StringToShort());
        addConverter(new StringToInteger());
        addConverter(new StringToLong());
        addConverter(new StringToFloat());
        addConverter(new StringToDouble());
        addConverter(new StringToBigInteger());
        addConverter(new StringToBigDecimal());
        addConverter(new StringToLocale());
        addConverter(new StringToDate());
        addConverter(new NumberToNumber());
        addConverter(new ObjectToCollection(this));
        addConverter(new CollectionToCollection(this));
        if (ClassUtils.isPresent("java.lang.Enum", this.getClass().getClassLoader())) {
            addConverter(new StringToEnum());
        }
    }

    protected void addDefaultAliases() {
        addAlias("string", String.class);
        addAlias("byte", Byte.class);
        addAlias("boolean", Boolean.class);
        addAlias("character", Character.class);
        addAlias("short", Short.class);
        addAlias("integer", Integer.class);
        addAlias("long", Long.class);
        addAlias("float", Float.class);
        addAlias("double", Double.class);
        addAlias("bigInteger", BigInteger.class);
        addAlias("bigDecimal", BigDecimal.class);
        addAlias("locale", Locale.class);
        addAlias("date", Date.class);
    }

}