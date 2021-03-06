package com.javaclasses.calculator.impl;

import com.javaclasses.calculator.MathExpressionCalculator;
import com.javaclasses.finiteStateMachine.AbstractStateMachine;
import com.javaclasses.finiteStateMachine.Parser;
import com.javaclasses.calculator.impl.state.StateImpl;
import com.javaclasses.calculator.impl.state.NextStateImpl;
import com.javaclasses.calculator.impl.operations.Operation;
import com.javaclasses.calculator.impl.operations.impl.BinaryFactory;

import java.util.ArrayDeque;

public class MathExpressionCalculatorImpl extends AbstractStateMachine implements MathExpressionCalculator {
    private ArrayDeque<Operation> operatorsStack;
    private ArrayDeque<Double> operandsStack;
    private ArrayDeque<Integer> parenthesesStack;
    private BinaryFactory binaryFactory = new BinaryFactory();

    public MathExpressionCalculatorImpl(Parser parser) {
        super(parser);
    }

    /**
     * Processes all stored operations.
     */
    private void cascadeOperations() {
        while (!operatorsStack.isEmpty() && (parenthesesStack.isEmpty() || parenthesesStack.peek() < operandsStack.size() - 1)) {
            Operation stackOperation = operatorsStack.peek();
            double[] args = new double[stackOperation.getArgsAmount()];
            for (int i = stackOperation.getArgsAmount() - 1; i >= 0; i--) {
                args[i] = operandsStack.pop();
            }
            operandsStack.push(stackOperation.performOperation(args));

            operatorsStack.pop();
        }
    }

    /**
     * Takes an operation than according to its priority performs it or stores it, for future processing.
     *
     * @param operationStr string which contains string representation of the operation
     */
    private void prioritizeOperation(String operationStr) {

        Operation currentOperation = binaryFactory.operationFactory(operationStr);
        if (!operatorsStack.isEmpty()) {
            Operation stackOperation = operatorsStack.peek();
            if (stackOperation.compareTo(currentOperation) == 0) {
                cascadeOperations();
                operatorsStack.push(currentOperation);
            } else if (stackOperation.compareTo(currentOperation) > 0) {
                operatorsStack.push(currentOperation);
            } else if (stackOperation.compareTo(currentOperation) < 0) {
                cascadeOperations();
                operatorsStack.push(currentOperation);
            }
        } else {
            operatorsStack.push(currentOperation);
        }
    }


    @Override
    public double calculate() {

        operatorsStack = new ArrayDeque<>();
        operandsStack = new ArrayDeque<>();
        parenthesesStack = new ArrayDeque<>();

        run();

        if (!parenthesesStack.isEmpty()) {
            throw new IllegalArgumentException("Parenthesis is not closed");
        }
        cascadeOperations();

        return operandsStack.pop();
    }

    @Override
    protected void performOperation(String sign) {
        NextStateImpl nState = new NextStateImpl();
        if (nState.getNextState(sign).equals(StateImpl.NUMBER)) {
            operandsStack.push(Double.parseDouble(sign));
        } else if (nState.getNextState(sign).equals(StateImpl.BINARY_OPERATION)) {
            prioritizeOperation(sign);
        } else if (nState.getNextState(sign).equals(StateImpl.OPEN_PARENTHESIS)) {
            parenthesesStack.push(operandsStack.size());
        } else if (nState.getNextState(sign).equals(StateImpl.CLOSE_PARENTHESIS)) {
            if (parenthesesStack.isEmpty()) {
                throw new IllegalArgumentException("Parentheses is not opened");
            }
            cascadeOperations();
            parenthesesStack.pop();
        } else if (!sign.equals("")) {
            prioritizeOperation(sign);
        }
    }
}
