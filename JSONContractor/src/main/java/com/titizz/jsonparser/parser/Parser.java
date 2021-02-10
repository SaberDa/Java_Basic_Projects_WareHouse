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
        Token token = tokens.next();
        if (token == null) {
            return new JsonObject();
        } else if (token.getTokenType() == TokenType.BEGIN_OBJECT) {
            return parseJsonObject();
        } else if (token.getTokenType() == TokenType.BEGIN_ARRAY) {
            return parseJsonArray();
        } else {
            throw new JsonParseException("Parse error, invalid token");
        }
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
                case NUMBER:
                    checkExceptionToken(tokenType, exceptToken);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonObject.put(key, Double.valueOf(tokenValue));
                    } else {
                        Long num = Long.valueOf(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonObject.put(key, num);
                        } else {
                            jsonObject.put(key, num.intValue());
                        }
                    }
                    exceptToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case BOOLEAN:
                    checkExceptionToken(tokenType, exceptToken);
                    jsonObject.put(key, Boolean.valueOf(token.getValue()));
                    exceptToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case STRING:
                    checkExceptionToken(tokenType, exceptToken);
                    Token preToken = tokens.peekPrevious();
                    /*
                     * 在 JSON 中，字符串既可以作为键，也可作为值。
                     * 作为键时，只期待下一个 Token 类型为 SEP_COLON。
                     * 作为值时，期待下一个 Token 类型为 SEP_COMMA 或 END_OBJECT
                     */
                    if (preToken.getTokenType() == TokenType.SEP_COLON) {
                        value = token.getValue();
                        jsonObject.put(key, value);
                        exceptToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    } else {
                        key = token.getValue();
                        exceptToken = SEP_COMMA_TOKEN;
                    }
                    break;
                case SEP_COLON:
                    checkExceptionToken(tokenType, exceptToken);
                    exceptToken = NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN |
                            STRING_TOKEN | BEGIN_OBJECT_TOKEN | BEGIN_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExceptionToken(tokenType, exceptToken);
                    exceptToken = STRING_TOKEN;
                    break;
                case END_DOCUMENT:
                    checkExceptionToken(tokenType, exceptToken);
                    return jsonObject;
                default:
                    throw new JsonParseException("Unexpected Token.");
            }
        }
        throw new JsonParseException("Parse error, invalid token");
    }

    private JsonArray parseJsonArray() {
        int expectToken = BEGIN_ARRAY_TOKEN | END_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN |
                NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN;
        JsonArray jsonArray = new JsonArray();
        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.getTokenType();
            String tokenValue = token.getValue();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExceptionToken(tokenType, expectToken);
                    jsonArray.add(parseJsonObject());
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BEGIN_ARRAY:
                    checkExceptionToken(tokenType, expectToken);
                    jsonArray.add(parseJsonArray());
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case END_ARRAY:
                    checkExceptionToken(tokenType, expectToken);
                    return jsonArray;
                case NULL:
                    checkExceptionToken(tokenType, expectToken);
                    jsonArray.add(null);
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case NUMBER:
                    checkExceptionToken(tokenType, expectToken);
                    if (tokenValue.contains(".") || tokenValue.contains("e") || tokenValue.contains("E")) {
                        jsonArray.add(Double.valueOf(tokenValue));
                    } else {
                        Long num = Long.valueOf(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonArray.add(num);
                        } else {
                            jsonArray.add(num.intValue());
                        }
                    }
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BOOLEAN:
                    checkExceptionToken(tokenType, expectToken);
                    jsonArray.add(Boolean.valueOf(tokenValue));
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case STRING:
                    checkExceptionToken(tokenType, expectToken);
                    jsonArray.add(tokenValue);
                    expectToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExceptionToken(tokenType, expectToken);
                    expectToken = STRING_TOKEN | NULL_TOKEN | NUMBER_TOKEN |
                            BOOLEAN_TOKEN | BEGIN_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN;
                    break;
                case END_DOCUMENT:
                    checkExceptionToken(tokenType, expectToken);
                    return jsonArray;
                default:
                    throw new JsonParseException("Unexpected Token.");
            }
        }
        throw new JsonParseException("Parse error, invalid Token");
    }

    private void checkExceptionToken(TokenType tokenType, int exception) {
        if ((tokenType.getTokenCode() & exception) == 0) {
            throw new JsonParseException("Parse error, invalid Token.");
        }
    }
}
