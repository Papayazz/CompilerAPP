package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

public class Generator {

    public String generate(AST ast) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode node : ast.root.body) {
            generateNode(node, sb, 0);
        }
        return sb.toString();
    }

    private void generateNode(ASTNode node, StringBuilder sb, int indent) {
        String pre = "  ".repeat(indent);
        if (node instanceof Stylerule) {
            Stylerule rule = (Stylerule) node;
            if (rule.selectors.isEmpty()) return;
            sb.append(pre).append(rule.selectors.get(0).toString()).append(" {\n");
            for (ASTNode child : rule.body) {
                generateNode(child, sb, indent + 1);
            }
            sb.append(pre).append("}\n");
        } else if (node instanceof Declaration) {
            Declaration dec = (Declaration) node;
            sb.append(pre)
              .append(dec.property.name)
              .append(": ")
              .append(expressionToString(dec.expression))
              .append(";\n");
        } else if (node instanceof VariableAssignment) {
            // ignore in final output
        }
    }

    private String expressionToString(Expression expr) {
        if (expr instanceof PixelLiteral) {
            return ((PixelLiteral) expr).value + "px";
        } else if (expr instanceof PercentageLiteral) {
            return ((PercentageLiteral) expr).value + "%";
        } else if (expr instanceof ColorLiteral) {
            return ((ColorLiteral) expr).value;
        } else if (expr instanceof ScalarLiteral) {
            return Integer.toString(((ScalarLiteral) expr).value);
        } else if (expr instanceof BoolLiteral) {
            return ((BoolLiteral) expr).value ? "TRUE" : "FALSE";
        }
        return "";
    }
}
