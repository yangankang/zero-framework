package com.yoosal.json.parser.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.yoosal.json.parser.DefaultJSONParser;
import com.yoosal.json.parser.DefaultJSONParser.ResolveTask;
import com.yoosal.json.parser.JSONToken;
import com.yoosal.json.parser.ParseContext;
import com.yoosal.json.parser.ParserConfig;
import com.yoosal.json.util.FieldInfo;

public class DefaultFieldDeserializer extends FieldDeserializer {

    private ObjectDeserializer fieldValueDeserilizer;

    public DefaultFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo){
        super(clazz, fieldInfo);
    }
    
    public ObjectDeserializer getFieldValueDeserilizer(ParserConfig config) {
        if (fieldValueDeserilizer == null) {
            fieldValueDeserilizer = config.getDeserializer(fieldInfo.fieldClass, fieldInfo.fieldType);
        }

        return fieldValueDeserilizer;
    }

    @Override
    public void parseField(DefaultJSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        if (fieldValueDeserilizer == null) {
            fieldValueDeserilizer = parser.getConfig().getDeserializer(fieldInfo);
        }

        Type fieldType = fieldInfo.fieldType;
        if (objectType instanceof ParameterizedType) {
            ParseContext objContext = parser.getContext();
            objContext.type = objectType;
            fieldType= FieldInfo.getFieldType(this.clazz, objectType, fieldType);
        }

        // ContextObjectDeserializer
        Object value;
        if (fieldValueDeserilizer instanceof JavaBeanDeserializer) {
            JavaBeanDeserializer javaBeanDeser = (JavaBeanDeserializer) fieldValueDeserilizer;
            value = javaBeanDeser.deserialze(parser, fieldType, fieldInfo.name, fieldInfo.parserFeatures);
        } else {
            if (this.fieldInfo.format != null
                    && fieldValueDeserilizer instanceof ContextObjectDeserializer
                    ) {
                value = ((ContextObjectDeserializer)fieldValueDeserilizer) //
                        .deserialze(parser, fieldType, fieldInfo.name, fieldInfo.format, fieldInfo.parserFeatures);
            } else {
                value = fieldValueDeserilizer.deserialze(parser, fieldType, fieldInfo.name);
            }
        }
        if (parser.getResolveStatus() == DefaultJSONParser.NeedToResolve) {
            ResolveTask task = parser.getLastResolveTask();
            task.fieldDeserializer = this;
            task.ownerContext = parser.getContext();
            parser.setResolveStatus(DefaultJSONParser.NONE);
        } else {
            if (object == null) {
                fieldValues.put(fieldInfo.name, value);
            } else {
                setValue(object, value);
            }
        }
    }

    public int getFastMatchToken() {
        if (fieldValueDeserilizer != null) {
            return fieldValueDeserilizer.getFastMatchToken();
        }

        return JSONToken.LITERAL_INT;
    }
}
