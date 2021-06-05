
package calculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

public class CalculatorGUI extends JFrame {

    private  JTextField textField;
    private JButton buttons[] = new JButton[24];
    public static final String[] btns_texts = { "<=", "CE", "(", ")", "+/-", "sin", "cos", "sqrt", "7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", ".", "0", "=", "+"};
    private Boolean isShowingResult = false;
    private ActionHandler handler;

    public CalculatorGUI(String title, int x, int y) {
        // Setting up frame design
        this.setTitle(title);
        this.setLocation(x, y);
        this.setSize(400, 480);

        // Setting up frame icon
        ImageIcon icon = new ImageIcon("image\\logo.png");
        this.setIconImage(icon.getImage());

        // Setting up functional settings
        this.setFocusable(true);
        this.handler = new ActionHandler(this);
        this.addKeyListener(handler);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        prepComponents();
    }

    private void prepComponents() {
        //Setting Layout & Main Panel
        BorderLayout mainLayout = new BorderLayout(0, 25);
        JPanel mainPanel = new JPanel(mainLayout);
        mainPanel.setBorder(new EmptyBorder(20, 20, 30, 20));
        mainPanel.setBackground(new Color(239, 239, 239, 70));
        this.add(mainPanel);

        // Setting up TextArea
        textField = new JTextField(" ");
        textField.setFont( new Font(Font.SANS_SERIF, Font.BOLD, 16) );
        textField.setBackground(Color.white);
        textField.setEditable(false);
        textField.setBorder(null);
        textField.setPreferredSize(new Dimension(270, 60));
        mainPanel.add(textField, BorderLayout.NORTH);

        // Setting up Calculator Buttons
        JPanel btnPanel = new JPanel(new GridLayout(6, 4, 10, 15));
        btnPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        mainPanel.add(btnPanel);

        for(int i = 0; i < 24; i++) {
            buttons[i] = new JButton(btns_texts[i]);
            buttons[i].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            buttons[i].setBackground(new Color(0xA3A3A7));
            buttons[i].setBorderPainted(false);
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(this.handler);
            if( btns_texts[i].equals("+/-") )
                buttons[i].setActionCommand("change");
            else
                buttons[i].setActionCommand(btns_texts[i]);
            btnPanel.add(buttons[i]);
        }
        this.revalidate();
    }

    public void pressedDigit(String digit) {
        if( this.textField.getText().length() > 1 && last_char(1) == ')' )
            addText(" * ");
        addText(digit);
    }

    public void pressedDot() {
        if( last_char(0) == ' ' )
            addText("0");
        addText(".");
    }

    public void pressedParenthesis(String parenthesis) {
        if( last_char(0) != ' ' )
            addText(" ");

        if( this.textField.getText().length() > 1 && parenthesis.equals("(") && (last_char(1) == ')' || Character.isDigit(last_char(1)) || last_char(1) == '.') )
            addText("* ");

        addText(parenthesis + " ");
    }

    public void pressedOperator(String operator) {
        if( last_char(1) == '+' || last_char(1) == '-' || last_char(1) == '*' || last_char(1) == '/' || last_char(1) == '(' )
            return;

        if( last_char(0) != ' ' )
            addText(" ");
        addText(operator + " ");
    }

    public void pressedFunction(String function) {
        if( last_char(0) != ' ' )
            addText(" ");

        if( this.textField.getText().length() > 1 && ( last_char(1) == ')' || Character.isDigit(last_char(1)) || last_char(1) == '.') )
            addText("* ");

        addText(function + "( ");
    }

    public void changeSign() {
        if( !Character.isDigit(last_char(0)) )
            return;

        String[] expression = this.textField.getText().split(" ");
        if( Character.isDigit(last_char(0)) || last_char(0) == '.' ) {
            if( expression[expression.length - 1].charAt(0) == '-' )
                expression[expression.length - 1] = '+' + expression[expression.length - 1].substring(1);
            else if( expression[expression.length - 1].charAt(0) == '+' )
                expression[expression.length - 1] = '-' + expression[expression.length - 1].substring(1);
            else
                expression[expression.length - 1] = '-' + expression[expression.length - 1];
        }
        this.textField.setText(String.join(" ", expression));
    }

    public void pressedEqual() {
        CalculationEngine engine = new CalculationEngine(this.textField.getText());
        BigDecimal ans;
        try {
            ans = engine.Calculate();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if( engine.getError() == null ) {
            this.textField.setText(" " + ans.setScale(12, BigDecimal.ROUND_HALF_EVEN).doubleValue());
            this.isShowingResult = true;
        }
        else
            JOptionPane.showMessageDialog(null, engine.getError(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void pressedDelete(int amo) {
        if( amo == -1 ) {
            this.textField.setText(" ");
            this.isShowingResult = false;
        }

        if (textField.getText().equals(" "))
            return;

        try {
            if (Character.isDigit(last_char(0)))
                textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
            else {
                do {
                    textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
                }
                while( !this.textField.equals(" ") &&  last_char(1) != '(' && !Character.isDigit(last_char(0)) && !isOperator(last_char(1)));
            }
        }
        catch (Exception e){}
    }

    public void resetDisplayStatus(String command) {
        if( this.textField.getText().equalsIgnoreCase(" infinity") )
            this.textField.setText(" 0");

        if( (Character.isDigit(command.charAt(0)) || command.equals(".") || command.equals("<=")) && this.isShowingResult )
            this.textField.setText(" ");

        this.isShowingResult = false;
    }

    private Boolean isOperator(char c) {
        if( c == '+' || c == '-' || c == '*' || c == '/' )
            return true;
        return false;
    }

    private char last_char(int cnt) {
        return this.textField.getText().charAt(this.textField.getText().length()-1-cnt);
    }

    private void addText(String c) {
        textField.setText(textField.getText() + c);
    }
}
