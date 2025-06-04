package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();
        variableValues.addFirst(new HashMap<>());
        evaluateBody(ast.root.body);
    }

    private void evaluateBody(List<ASTNode> body) {
        variableValues.addFirst(new HashMap<>());
        for (int i = 0; i < body.size(); i++) {
            ASTNode node = body.get(i);
            if (node instanceof VariableAssignment) {
                VariableAssignment va = (VariableAssignment) node;
                Literal value = evaluateExpression(va.expression);
                va.expression = value;
                variableValues.getFirst().put(va.name.name, value);
            } else if (node instanceof Declaration) {
                Declaration dec = (Declaration) node;
                dec.expression = evaluateExpression(dec.expression);
            } else if (node instanceof IfClause) {
                IfClause clause = (IfClause) node;
                Literal cond = evaluateExpression(clause.conditionalExpression);
                boolean val = (cond instanceof BoolLiteral) && ((BoolLiteral) cond).value;
                List<ASTNode> replace = val ? clause.body
                        : (clause.elseClause != null ? clause.elseClause.body : new ArrayList<>());
                evaluateBody(replace);
                body.remove(i);
                body.addAll(i, replace);
                i += replace.size() - 1;
            } else if (node instanceof Stylerule) {
                Stylerule rule = (Stylerule) node;
                evaluateBody(rule.body);
            }
        }
        variableValues.removeFirst();
    }

    private Literal evaluateExpression(Expression expr) {
        if (expr instanceof Literal) {
            return (Literal) expr;
        } else if (expr instanceof VariableReference) {
            String name = ((VariableReference) expr).name;
            for (int i = 0; i < variableValues.getSize(); i++) {
                HashMap<String, Literal> map = variableValues.get(i);
                if (map.containsKey(name)) {
                    return map.get(name);
                }
            }
            return null;
        } else if (expr instanceof AddOperation) {
            AddOperation op = (AddOperation) expr;
            Literal left = evaluateExpression((Expression) op.getChildren().get(0));
            Literal right = evaluateExpression((Expression) op.getChildren().get(1));
            if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
            } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
            } else {
                return new ScalarLiteral(((ScalarLiteral) left).value + ((ScalarLiteral) right).value);
            }
        } else if (expr instanceof SubtractOperation) {
            SubtractOperation op = (SubtractOperation) expr;
            Literal left = evaluateExpression((Expression) op.getChildren().get(0));
            Literal right = evaluateExpression((Expression) op.getChildren().get(1));
            if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
            } else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
            } else {
                return new ScalarLiteral(((ScalarLiteral) left).value - ((ScalarLiteral) right).value);
            }
        } else if (expr instanceof MultiplyOperation) {
            MultiplyOperation op = (MultiplyOperation) expr;
            Literal left = evaluateExpression((Expression) op.getChildren().get(0));
            Literal right = evaluateExpression((Expression) op.getChildren().get(1));
            if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {
                return new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
            } else if (right instanceof ScalarLiteral && left instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
            } else if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
                return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
            } else if (right instanceof ScalarLiteral && left instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
            } else {
                int a = ((ScalarLiteral) left).value;
                int b = ((ScalarLiteral) right).value;
                return new ScalarLiteral(a * b);
            }
        }
        return null;
    }
}
