package com.yoosal.json.serializer;

import java.io.IOException;

public interface ContextObjectSerializer extends ObjectSerializer {
    void write(JSONSerializer serializer, //
               Object object, //
               BeanContext context) throws IOException;
}
