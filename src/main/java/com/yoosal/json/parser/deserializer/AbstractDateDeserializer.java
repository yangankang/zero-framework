package com.yoosal.json.parser.deserializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.yoosal.json.JSON;
import com.yoosal.json.JSONException;
import com.yoosal.json.parser.DefaultJSONParser;
import com.yoosal.json.parser.Feature;
import com.yoosal.json.parser.JSONLexer;
import com.yoosal.json.parser.JSONScanner;
import com.yoosal.json.parser.JSONToken;
import com.yoosal.json.util.TypeUtils;

public abstract class AbstractDateDeserializer extends ContextObjectDeserializer implements ObjectDeserializer {

    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        return deserialze(parser, clazz, fieldName, null, 0);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName, String format, int features) {
        JSONLexer lexer = parser.lexer;

        Object val;
        if (lexer.token() == JSONToken.LITERAL_INT) {
            val = lexer.longValue();
            lexer.nextToken(JSONToken.COMMA);
        } else if (lexer.token() == JSONToken.LITERAL_STRING) {
            String strVal = lexer.stringVal();
            
            if (format != null) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                    val = simpleDateFormat.parse(strVal);
                } catch (ParseException ex) {
                    // skip
                    val = null;
                }
            } else {
                val = null;
            }
            
            if (val == null) {
                val = strVal;
                lexer.nextToken(JSONToken.COMMA);
                
                if (lexer.isEnabled(Feature.AllowISO8601DateFormat)) {
                    JSONScanner iso8601Lexer = new JSONScanner(strVal);
                    if (iso8601Lexer.scanISO8601DateIfMatch()) {
                        val = iso8601Lexer.getCalendar().getTime();
                    }
                    iso8601Lexer.close();
                }
            }
        } else if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            val = null;
        } else if (lexer.token() == JSONToken.LBRACE) {
            lexer.nextToken();
            
            String key;
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                key = lexer.stringVal();
                
                if (JSON.DEFAULT_TYPE_KEY.equals(key)) {
                    lexer.nextToken();
                    parser.accept(JSONToken.COLON);
                    
                    String typeName = lexer.stringVal();
                    Class<?> type = TypeUtils.loadClass(typeName, parser.getConfig().getDefaultClassLoader());
                    if (type != null) {
                        clazz = type;
                    }
                    
                    parser.accept(JSONToken.LITERAL_STRING);
                    parser.accept(JSONToken.COMMA);
                }
                
                lexer.nextTokenWithColon(JSONToken.LITERAL_INT);
            } else {
                throw new JSONException("syntax error");
            }
            
            long timeMillis;
            if (lexer.token() == JSONToken.LITERAL_INT) {
                timeMillis = lexer.longValue();
                lexer.nextToken();
            } else {
                throw new JSONException("syntax error : " + lexer.tokenName());
            }
            
            val = timeMillis;
            
            parser.accept(JSONToken.RBRACE);
        } else if (parser.getResolveStatus() == DefaultJSONParser.TypeNameRedirect) {
            parser.setResolveStatus(DefaultJSONParser.NONE);
            parser.accept(JSONToken.COMMA);

            if (lexer.token() == JSONToken.LITERAL_STRING) {
                if (!"val".equals(lexer.stringVal())) {
                    throw new JSONException("syntax error");
                }
                lexer.nextToken();
            } else {
                throw new JSONException("syntax error");
            }

            parser.accept(JSONToken.COLON);

            val = parser.parse();

            parser.accept(JSONToken.RBRACE);
        } else {
            val = parser.parse();
        }

        return (T) cast(parser, clazz, fieldName, val);
    }

    protected abstract <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object value);
}
