package com.EkkerArtem.stream.calculator.finiteStateMachine;

/**
 * Returns token by token parsing of input string.
 */
public interface Parser {
    /**
     * Adds support of new operand
     *
     * @param operand
     */
    void addOperator(String operand);

    /**
     * @return true if nextTokenExists or false if not
     */
    boolean hasNext();

    /**
     * @return next sign in the input string.
     */
    String nextSign();

    String getInput();
}