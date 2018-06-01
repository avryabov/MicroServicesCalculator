package controller;

import services.operations.OperationService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    private final Pattern pMult = Pattern.compile("((-?\\d+\\.?\\d*)(\\*|/)(-?\\d+\\.?\\d*))");
    private final Pattern pPlus = Pattern.compile("((-?\\d+\\.?\\d*)(\\+|-)(-?\\d+\\.?\\d*))");
    private final Pattern pBkt = Pattern.compile("(\\([^()]+[*/+-^]+[^()]+\\))");
    private final Pattern pDblOp = Pattern.compile("((--)|(\\+\\+))(?!\\d+\\.?\\d*\\^)");
    private final Pattern pEmptyBkt = Pattern.compile("(\\(((-|\\+)?\\d+\\.?\\d*)\\))");

    private OperationService addSvc;
    private OperationService subSvc;
    private OperationService mltSvc;
    private OperationService divSvc;

    public Calculator(OperationService addSvc, OperationService subSvc, OperationService mltSvc, OperationService divSvc) {
        this.addSvc = addSvc;
        this.subSvc = subSvc;
        this.mltSvc = mltSvc;
        this.divSvc = divSvc;
    }

    private Matcher find(String expression) {
        Matcher m;
        m = pMult.matcher(expression);
        if (m.find())
            return m;
        m = pPlus.matcher(expression);
        if (m.find())
            return m;
        return null;
    }

    private String findAndCalc(String expression) {
        Matcher m = find(expression);
        if (m != null) {
            double result = calculate(Double.parseDouble(m.group(2)), Double.parseDouble(m.group(4)), m.group(3));
            return expression.substring(0, m.start()) + result + expression.substring(m.end());
        }
        return null;
    }


    public String calculate(final String expression) {
        String origExpression = expression.replaceAll(" ", "");
        String newExpression;
        String readyExpression = origExpression;
        boolean isBkt = false;

        Matcher mBkt = pBkt.matcher(origExpression);
        if (mBkt.find()) {
            isBkt = true;
            readyExpression = mBkt.group();
        }

        Matcher mDblOp = pDblOp.matcher(readyExpression);
        if (mDblOp.find()) {
            newExpression = readyExpression.substring(0, mDblOp.start(1)) + "+" + readyExpression.substring(mDblOp.end());
        } else {
            newExpression = findAndCalc(readyExpression);
            if (newExpression == null) {
                Matcher mEmptyBkt = pEmptyBkt.matcher(readyExpression);
                if (mEmptyBkt.find()) {
                    newExpression = readyExpression.substring(0, mEmptyBkt.start()) + mEmptyBkt.group(2) + readyExpression.substring(mEmptyBkt.end());
                } else {
                    newExpression = Double.toString(Math.round(Double.parseDouble(readyExpression) * 100.0) / 100.0);
                }
            }
        }

        if (isBkt) {
            newExpression = origExpression.substring(0, mBkt.start()) + newExpression + origExpression.substring(mBkt.end());
        }

        if (readyExpression.equals(newExpression)) {
            if (origExpression.charAt(origExpression.length() - 1) == '0')
                origExpression = origExpression.substring(0, origExpression.length() - 2);
            return origExpression;
        } else
            return calculate(newExpression);

    }

    private double calculate(double a, double b, String op) {
        double result = 0;
        switch (op) {
            case "*":
                result = mltSvc.calc(a, b);
                break;
            case "/":
                result = divSvc.calc(a, b);
                break;
            case "+":
                result = addSvc.calc(a, b);
                break;
            case "-":
                result = subSvc.calc(a, b);
                break;
        }
        return result;
    }

}
