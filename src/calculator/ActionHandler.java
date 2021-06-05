package calculator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ActionHandler implements KeyListener, ActionListener {

    private CalculatorGUI calculator;

    public ActionHandler(CalculatorGUI calculator) {
        this.calculator = calculator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        calculator.resetDisplayStatus(e.getActionCommand());
        String command = e.getActionCommand();

        if( command.equals("CE") )
            calculator.pressedDelete(-1);

        else if( command.equals("<=") )
            calculator.pressedDelete(1);

        else if( command.equals("(") || command.equals(")") )
            calculator.pressedParenthesis(command);

        else if(  command.equals("-") || command.equals("+") || command.equals("/") || command.equals("*") )
            calculator.pressedOperator(command);

        else if( command.equals("sin") || command.equals("cos") || command.equals("sqrt") )
            calculator.pressedFunction(command);

        else if( command.equals("=") )
            calculator.pressedEqual();

        else if( command.equals("change") )
            calculator.changeSign();

        else if( Character.isDigit(command.charAt(0)) )
            calculator.pressedDigit(command);

        else if( command.equals(".") )
            calculator.pressedDot();

        this.calculator.setFocusable(true);
        this.calculator.requestFocusInWindow();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == 8 )
            this.actionPerformed(new ActionEvent(this.calculator, 0, "<="));

        else if( e.getKeyCode() == 10 )
            this.actionPerformed(new ActionEvent(this.calculator, 0, "="));

        else
            for( String btn : CalculatorGUI.btns_texts )
                if( e.getKeyChar() == btn.charAt(0) )
                    this.actionPerformed(new ActionEvent(this.calculator, 0, btn));
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
