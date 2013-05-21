package edu.gatech.hava.debug;

import java.util.HashMap;
import java.util.Map;

import edu.gatech.hava.parser.HavaSourceConstants;

enum TokenType {

    SPACE_AFTER(
        HavaSourceConstants.COMMA
    ),

    SPACE_BOTH(
        HavaSourceConstants.LBRACE,
        HavaSourceConstants.RBRACE,
        HavaSourceConstants.EQ,
        HavaSourceConstants.ASSIGN,
        HavaSourceConstants.ADD,
        HavaSourceConstants.MUL,
        HavaSourceConstants.SUB,
        HavaSourceConstants.DIV,
        HavaSourceConstants.PWR,
        HavaSourceConstants.OR,
        HavaSourceConstants.AND,
        HavaSourceConstants.BAR,
        HavaSourceConstants.TO,
        HavaSourceConstants.IN
    ),

    DEFAULT;

    private static final Map<Integer, TokenType> MAP =
        new HashMap<Integer, TokenType>();

    static {

        for (final TokenType type : values()) {
            for (final int token : type.tokens) {
                MAP.put(token, type);
            }
        }

    }

    private int[] tokens;

    private TokenType(final int ... tokens) {

        this.tokens = tokens;

    }

    public static TokenType get(final int token) {

        final TokenType type = MAP.get(token);

        return type != null ? type : TokenType.DEFAULT;

    }

}
