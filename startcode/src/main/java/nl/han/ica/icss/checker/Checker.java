package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;

import java.util.HashMap;
import java.util.List;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkBody(ast.root.body);
    }

    private void checkBody(List<ASTNode> body) {
        variableTypes.addFirst(new HashMap<>());
        for (ASTNode node : body) {
            if (node instanceof VariableAssignment) {
                VariableAssignment va = (VariableAssignment) node;
                ExpressionType type = getExpressionType(va.expression);
                variableTypes.getFirst().put(va.name.name, type);
            } else if (node instanceof Declaration) {
                Declaration dec = (Declaration) node;
                checkExpression(dec.expression);
            } else if (node instanceof IfClause) {
                IfClause clause = (IfClause) node;
                checkExpression(clause.conditionalExpression);
                checkBody(clause.body);
                if (clause.elseClause != null) {
                    checkBody(clause.elseClause.body);
                }
            } else if (node instanceof Stylerule) {
                Stylerule rule = (Stylerule) node;
                checkBody(rule.body);
            } else {
                // VariableReferences are handled in expressions
            }
        }
        variableTypes.removeFirst();
    }

    private void checkExpression(Expression expr) {
        if (expr instanceof VariableReference) {
            String name = ((VariableReference) expr).name;
            if (!isDefined(name)) {
                expr.setError("Variable " + name + " not defined");
            }
        } else if (expr instanceof Operation) {
            for (ASTNode child : expr.getChildren()) {
                checkExpression((Expression) child);
            }
        }
    }

    private boolean isDefined(String name) {
        for (int i = 0; i < variableTypes.getSize(); i++) {
            HashMap<String, ExpressionType> map = variableTypes.get(i);
            if (map.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    private ExpressionType getExpressionType(Expression expr) {
        if (expr instanceof PixelLiteral) return ExpressionType.PIXEL;
        if (expr instanceof PercentageLiteral) return ExpressionType.PERCENTAGE;
        if (expr instanceof ColorLiteral) return ExpressionType.COLOR;
        if (expr instanceof ScalarLiteral) return ExpressionType.SCALAR;
        if (expr instanceof BoolLiteral) return ExpressionType.BOOL;
        if (expr instanceof VariableReference) {
            String name = ((VariableReference) expr).name;
            for (int i = 0; i < variableTypes.getSize(); i++) {
                HashMap<String, ExpressionType> map = variableTypes.get(i);
                if (map.containsKey(name)) {
                    return map.get(name);
                }
            }
        }
        return ExpressionType.UNDEFINED;
    }
}
