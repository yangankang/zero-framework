package com.yoosal.json.parser.deserializer;

import java.lang.reflect.Type;

import com.yoosal.json.parser.DefaultJSONParser;

public abstract class ContextObjectDeserializer implements ObjectDeserializer {
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return deserialze(parser, type, fieldName, null, 0);
    }
    
    public abstract <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName, String format, int features); 
}
