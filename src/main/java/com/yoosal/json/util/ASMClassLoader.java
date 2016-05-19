package com.yoosal.json.util;

import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import com.yoosal.json.JSON;
import com.yoosal.json.JSONArray;
import com.yoosal.json.JSONAware;
import com.yoosal.json.JSONException;
import com.yoosal.json.JSONObject;
import com.yoosal.json.JSONPath;
import com.yoosal.json.JSONPathException;
import com.yoosal.json.JSONReader;
import com.yoosal.json.JSONStreamAware;
import com.yoosal.json.JSONWriter;
import com.yoosal.json.TypeReference;
import com.yoosal.json.parser.DefaultJSONParser;
import com.yoosal.json.parser.Feature;
import com.yoosal.json.parser.JSONLexer;
import com.yoosal.json.parser.JSONLexerBase;
import com.yoosal.json.parser.JSONReaderScanner;
import com.yoosal.json.parser.JSONScanner;
import com.yoosal.json.parser.JSONToken;
import com.yoosal.json.parser.ParseContext;
import com.yoosal.json.parser.ParserConfig;
import com.yoosal.json.parser.SymbolTable;
import com.yoosal.json.parser.deserializer.AutowiredObjectDeserializer;
import com.yoosal.json.parser.deserializer.DefaultFieldDeserializer;
import com.yoosal.json.parser.deserializer.ExtraProcessable;
import com.yoosal.json.parser.deserializer.ExtraProcessor;
import com.yoosal.json.parser.deserializer.ExtraTypeProvider;
import com.yoosal.json.parser.deserializer.FieldDeserializer;
import com.yoosal.json.parser.deserializer.JavaBeanDeserializer;
import com.yoosal.json.parser.deserializer.ObjectDeserializer;
import com.yoosal.json.serializer.AfterFilter;
import com.yoosal.json.serializer.BeanContext;
import com.yoosal.json.serializer.BeforeFilter;
import com.yoosal.json.serializer.ContextObjectSerializer;
import com.yoosal.json.serializer.ContextValueFilter;
import com.yoosal.json.serializer.JSONSerializer;
import com.yoosal.json.serializer.JavaBeanSerializer;
import com.yoosal.json.serializer.LabelFilter;
import com.yoosal.json.serializer.Labels;
import com.yoosal.json.serializer.NameFilter;
import com.yoosal.json.serializer.ObjectSerializer;
import com.yoosal.json.serializer.PropertyFilter;
import com.yoosal.json.serializer.PropertyPreFilter;
import com.yoosal.json.serializer.SerialContext;
import com.yoosal.json.serializer.SerializeBeanInfo;
import com.yoosal.json.serializer.SerializeConfig;
import com.yoosal.json.serializer.SerializeFilter;
import com.yoosal.json.serializer.SerializeFilterable;
import com.yoosal.json.serializer.SerializeWriter;
import com.yoosal.json.serializer.SerializerFeature;
import com.yoosal.json.serializer.ValueFilter;

public class ASMClassLoader extends ClassLoader {

    private static java.security.ProtectionDomain DOMAIN;
    
    private static Map<String, Class<?>> classMapping = new HashMap<String, Class<?>>();

    static {
        DOMAIN = (java.security.ProtectionDomain) java.security.AccessController.doPrivileged(new PrivilegedAction<Object>() {

            public Object run() {
                return ASMClassLoader.class.getProtectionDomain();
            }
        });
        
        Class<?>[] jsonClasses = new Class<?>[] {JSON.class,
            JSONObject.class,
            JSONArray.class,
            JSONPath.class,
            JSONAware.class,
            JSONException.class,
            JSONPathException.class,
            JSONReader.class,
            JSONStreamAware.class,
            JSONWriter.class,
            TypeReference.class,
                    
            FieldInfo.class,
            TypeUtils.class,
            IOUtils.class,
            IdentityHashMap.class,
            ParameterizedTypeImpl.class,
            JavaBeanInfo.class,
                    
            ObjectSerializer.class,
            JavaBeanSerializer.class,
            SerializeFilterable.class,
            SerializeBeanInfo.class,
            JSONSerializer.class,
            SerializeWriter.class,
            SerializeFilter.class,
            Labels.class,
            LabelFilter.class,
            ContextValueFilter.class,
            AfterFilter.class,
            BeforeFilter.class,
            NameFilter.class,
            PropertyFilter.class,
            PropertyPreFilter.class,
            ValueFilter.class,
            SerializerFeature.class,
            ContextObjectSerializer.class,
            SerialContext.class,
            SerializeConfig.class,
                    
            JavaBeanDeserializer.class,
            ParserConfig.class,
            DefaultJSONParser.class,
            JSONLexer.class,
            JSONLexerBase.class,
            ParseContext.class,
            JSONToken.class,
            SymbolTable.class,
            Feature.class,
            JSONScanner.class,
            JSONReaderScanner.class,
                    
            AutowiredObjectDeserializer.class,
            ObjectDeserializer.class,
            ExtraProcessor.class,
            ExtraProcessable.class,
            ExtraTypeProvider.class,
            BeanContext.class,
            FieldDeserializer.class,
            DefaultFieldDeserializer.class,
        };
        
        for (Class<?> clazz : jsonClasses) {
            classMapping.put(clazz.getName(), clazz);
        }
    }
    
    public ASMClassLoader(){
        super(getParentClassLoader());
    }

    public ASMClassLoader(ClassLoader parent){
        super (parent);
    }

    static ClassLoader getParentClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            try {
                contextClassLoader.loadClass(JSON.class.getName());
                return contextClassLoader;
            } catch (ClassNotFoundException e) {
                // skip
            }
        }
        return JSON.class.getClassLoader();
    }

    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> mappingClass = classMapping.get(name);
        if (mappingClass != null) {
            return mappingClass;
        }
        
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }

    public Class<?> defineClassPublic(String name, byte[] b, int off, int len) throws ClassFormatError {
        Class<?> clazz = defineClass(name, b, off, len, DOMAIN);

        return clazz;
    }

    public boolean isExternalClass(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();

        if (classLoader == null) {
            return false;
        }

        ClassLoader current = this;
        while (current != null) {
            if (current == classLoader) {
                return false;
            }

            current = current.getParent();
        }

        return true;
    }

}
