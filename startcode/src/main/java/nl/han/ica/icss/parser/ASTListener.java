package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class ASTListener extends ICSSBaseListener {

    private AST ast;
    private IHANStack<ASTNode> stack;

    public ASTListener() {
        ast = new AST();
        stack = new HANStack<>();
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet sheet = new Stylesheet();
        ast.setRoot(sheet);
        stack.push(sheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        stack.pop();
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule rule = new Stylerule();
        stack.peek().addChild(rule);
        stack.push(rule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        stack.pop();
    }

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment ass = new VariableAssignment();
        stack.peek().addChild(ass);
        stack.push(ass);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        Expression expr = (Expression) stack.pop();
        VariableAssignment ass = (VariableAssignment) stack.pop();
        ass.addChild(new VariableReference(ctx.CAPITAL_IDENT().getText()));
        ass.addChild(expr);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration dec = new Declaration();
        stack.peek().addChild(dec);
        stack.push(dec);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Expression expr = (Expression) stack.pop();
        Declaration dec = (Declaration) stack.pop();
        dec.addChild(new PropertyName(ctx.LOWER_IDENT().getText()));
        dec.addChild(expr);
    }

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause clause = new IfClause();
        stack.peek().addChild(clause);
        stack.push(clause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        ASTNode conditionNode = stack.pop();
        IfClause clause = (IfClause) stack.pop();
        clause.addChild((Expression) conditionNode);
    }

    @Override
    public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        ElseClause clause = new ElseClause();
        stack.peek().addChild(clause);
        stack.push(clause);
    }

    @Override
    public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        stack.pop();
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        String text = ctx.getText();
        Selector sel;
        if (text.startsWith("#")) {
            sel = new IdSelector(text);
        } else if (text.startsWith(".")) {
            sel = new ClassSelector(text);
        } else {
            sel = new TagSelector(text);
        }
        stack.peek().addChild(sel);
    }

    @Override
    public void enterLiteral(ICSSParser.LiteralContext ctx) {
        Literal lit;
        if (ctx.COLOR() != null) {
            lit = new ColorLiteral(ctx.COLOR().getText());
        } else if (ctx.PIXELSIZE() != null) {
            lit = new PixelLiteral(ctx.PIXELSIZE().getText());
        } else if (ctx.PERCENTAGE() != null) {
            lit = new PercentageLiteral(ctx.PERCENTAGE().getText());
        } else if (ctx.SCALAR() != null) {
            lit = new ScalarLiteral(ctx.SCALAR().getText());
        } else if (ctx.TRUE() != null) {
            lit = new BoolLiteral(true);
        } else {
            lit = new BoolLiteral(false);
        }
        stack.push(lit);
    }

    @Override
    public void exitLiteral(ICSSParser.LiteralContext ctx) {
        // literal already pushed on stack in enterLiteral
    }

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference ref = new VariableReference(ctx.CAPITAL_IDENT().getText());
        stack.push(ref);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        // variable reference already on stack
    }

    @Override
    public void exitMulExpression(ICSSParser.MulExpressionContext ctx) {
        int count = ctx.atom().size();
        if (count <= 1) return; // single atom
        Expression result = (Expression) stack.pop();
        for (int i = count - 2; i >= 0; i--) {
            Expression left = (Expression) stack.pop();
            MultiplyOperation op = new MultiplyOperation();
            op.addChild(left);
            op.addChild(result);
            result = op;
        }
        stack.push(result);
    }

    @Override
    public void exitAddExpression(ICSSParser.AddExpressionContext ctx) {
        int count = ctx.mulExpression().size();
        if (count <= 1) return; // single term
        Expression result = (Expression) stack.pop();
        for (int i = count - 2; i >= 0; i--) {
            Expression left = (Expression) stack.pop();
            String op = ctx.getChild(1 + 2 * i).getText();
            if (op.equals("+")) {
                AddOperation add = new AddOperation();
                add.addChild(left);
                add.addChild(result);
                result = add;
            } else {
                SubtractOperation sub = new SubtractOperation();
                sub.addChild(left);
                sub.addChild(result);
                result = sub;
            }
        }
        stack.push(result);
    }

    @Override
    public void exitExpression(ICSSParser.ExpressionContext ctx) {
        // expression result already on stack
    }

}
