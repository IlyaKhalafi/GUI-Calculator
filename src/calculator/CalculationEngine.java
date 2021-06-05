package calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class CalculationEngine {

    private String expression, error;

    public CalculationEngine(String expression) {
        this.expression = expression;
    }

    public BigDecimal Calculate() {
        if( hasParenthesisError() )
            return new BigDecimal("0");

        String[] parts = this.expression.split(" ");
        ArrayList<String> infix = new ArrayList<>();
        for( String part : parts )
            if( part != null && part.length() != 0 )
                infix.add(part);
        return this.calculatePostfix(this.InfixToPostfix(infix));
    }

    private BigDecimal calculatePostfix(ArrayList<String> postfix)
    {
        if( this.error != null )
            return new BigDecimal("0");

        Stack<BigDecimal> stack = new Stack<>();

        for(int i = 0; i < postfix.size(); i++)
        {
            String part = postfix.get(i);

            if( Character.isDigit(part.charAt(0)) || ( part.length() > 1 && Character.isDigit(part.charAt(1)) ) )
                stack.push( new BigDecimal(Double.parseDouble(part)) );

            else
            {
                BigDecimal val1 = stack.pop().setScale(12, BigDecimal.ROUND_HALF_EVEN);
                BigDecimal val2 = stack.pop().setScale(12, BigDecimal.ROUND_HALF_EVEN);

                // Using second char of "sin", "cos", "sqrt" to recognize each function
                switch (part.charAt(0)) {
                    case '+':
                        stack.push(val2.add(val1));
                        break;

                    case '-':
                        stack.push(val2.subtract(val1));
                        break;

                    case '/':
                        stack.push(val2.divide(val1, RoundingMode.HALF_UP));
                        break;

                    case '*':
                        stack.push(val2.multiply(val1));
                        break;

                    case 'i':
                        stack.push(val1);
                        stack.push(val2);
                        break;

                    case 'o':
                        stack.push(val1);
                        stack.push(val2);
                        break;

                    case 'q':
                        stack.push(val1);
                        stack.push(val2);
                        break;
                }
            }
        }
        return stack.pop();
    }

    static int getPrecedence(String operator)
    {
        if (operator.equals("+") || operator.equals("-"))
            return 1;
        else if (operator.equals("*") || operator.equals("/"))
            return 2;
        else
            return -1;
    }

    private ArrayList<String> InfixToPostfix(ArrayList<String> infix)
    {
        Stack<String> stack = new Stack<>();

        ArrayList<String> output = new ArrayList<>();


        for (int i = 0; i < infix.size(); i++) {
            String part = infix.get(i);

            if ( Character.isDigit(part.charAt(0)) || ( part.length() > 1 && Character.isDigit(part.charAt(1)) ) )
                output.add(part);

            else if (part.equals("("))
                stack.push(part);

            else if (part.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    output.add(stack.pop());

                    String function = stack.pop();
                    if( !function.equals("(") )
                        output.add("" + function.charAt(1));
                }

            else {
                while ( !stack.isEmpty() && getPrecedence(part) <= getPrecedence(stack.peek()) )
                    output.add(stack.pop());

                stack.push(part);
            }
        }

        while ( !stack.isEmpty() ) {
            if ( stack.peek().equals("(") ) {
                this.error = "Expression syntax is invalid!";
                return null;
            }
            output.add(stack.pop());
        }
        return output;
    }

    private Boolean hasParenthesisError() {
        int cnt = 0;
        for(int i = 0; i < expression.length(); i++) {
            if( expression.charAt(i) == '(' )
                cnt++;
            else if( expression.charAt(i) == ')' )
                cnt--;

            if( cnt < 0 ) {
                this.error = "expression parenthesis syntax is invaid!";
                return true;
            }
        }

        if( cnt != 0 ) {
            this.error = "expression parenthesis syntax is invaid!";
            return true;
        }
        return false;
    }

    public String getError() {
        return this.error;
    }
}