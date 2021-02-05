package com.titizz.jsonparser.tokenizer;

/**
 * @author: Gengda Li
 * @create_date: 2/4/2021 1:41 PM
 * @desc:
 * @modifier:
 * @modifier_date:
 * @desc:
 */
public class Token {

    private TokenType tokenType;

    private String value;

    public Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public TokenType getTokenType() { return tokenType; }

    public void setTokenType(TokenType tokenType) { this.tokenType = tokenType; }

    public String getValue() { return this.value; }

    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType=" + tokenType +
                ", value ='" + value + '\'' +
                '}';
    }
}
