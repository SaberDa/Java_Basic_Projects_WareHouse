package com.titizz.jsonparser.parser;

import com.titizz.jsonparser.exception.JsonParseException;
import com.titizz.jsonparser.model.JsonArray;
import com.titizz.jsonparser.model.JsonObject;
import com.titizz.jsonparser.tokenizer.Token;
import com.titizz.jsonparser.tokenizer.TokenList;
import com.titizz.jsonparser.tokenizer.TokenType;

/**
 * @author: Gengda Li
 * @create_date: 2/5/2021 11:35 AM
 * @desc:
 * @modifier:
 * @modifier_date:
 * @desc:
 */
public class Parser {

    private static final int BEGIN_OBJECT_TOKEN = 1;
    private static final int END_OBJECT_TOKEN = 2;
    private static final int BEGIN_ARRAY_TOKEN = 4;
    private static final int END_ARRAY_TOKEN = 8;
    private static final int NULL_TOKEN = 16;
    private static final int NUMBER_TOKEN = 32;
    private static final int STRING_TOKEN = 64;
    private static final int BOOLEAN_TOKEN = 128;
    private static final int SEP_COLON_TOKEN = 256;
    private static final int SEP_COMMA_TOKEN = 512;

    private TokenList tokens;

    public Object parse(TokenList tokens) {
        this.tokens = tokens;
        return parse();
    }

    private Object parse() {

    }

    private JsonObject parseJsonObject() {
        JsonObject jsonObject = new JsonObject();
        int exceptToken = STRING_TOKEN | END_OBJECT_TOKEN;
        String key = null;
        Object value = null;
        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExceptionToken(tokenType, exceptToken);
                    jsonObject.put(key, parseJsonObject());
                    exceptToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case END_OBJECT:
                    checkExceptionToken(tokenType, exceptToken);
                    return jsonObject;
                case BEGIN_ARRAY:
                    checkExceptionToken(tokenType, exceptToken);
                    jsonObject.put(key, parseJsonArray());
                    exceptToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case NULL:
                    checkExceptionToken(tokenType, exceptToken);
                    jsonObject.put(key, null);
                    exceptToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
            }
        }
    }

    private JsonArray parseJsonArray() {

    }

    private void checkExceptionToken(TokenType tokenType, int exception) {
        if ((tokenType.getTokenCode() & exception) == 0) {
            throw new JsonParseException("Parse error, invalid Token.");
        }
    }
}
