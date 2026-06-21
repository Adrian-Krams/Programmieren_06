package filter.builder;

import filter.FilterBaseVisitor;
import filter.FilterParser;
import filter.nodes.CompOp;
import filter.nodes.Expr;
import filter.nodes.Value;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AstBuilderVisitor extends FilterBaseVisitor<Void> {

    private final Deque<Expr> exprStack = new ArrayDeque<>();
    private final Deque<Value> valueStack = new ArrayDeque<>();
    private final Deque<List<Value>> listStack = new ArrayDeque<>();

    public Expr translate(FilterParser.QueryContext ctx) {
        visit(ctx);
        return exprStack.pop();
    }

    @Override
    public Void visitQuery(FilterParser.QueryContext ctx) {
        visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitExpr(FilterParser.ExprContext ctx) {
        visit(ctx.orExpr());
        return null;
    }

    @Override
    public Void visitOrExpr(FilterParser.OrExprContext ctx) {
        visit(ctx.andExpr(0));
        Expr result = exprStack.pop();

        for (int i = 1; i < ctx.andExpr().size(); i++) {
            visit(ctx.andExpr(i));
            Expr right = exprStack.pop();
            result = new Expr.Or(result, right);
        }

        exprStack.push(result);
        return null;
    }

    @Override
    public Void visitAndExpr(FilterParser.AndExprContext ctx) {
        visit(ctx.notExpr(0));
        Expr result = exprStack.pop();

        for (int i = 1; i < ctx.notExpr().size(); i++) {
            visit(ctx.notExpr(i));
            Expr right = exprStack.pop();
            result = new Expr.And(result, right);
        }

        exprStack.push(result);
        return null;
    }

    @Override
    public Void visitNotExpr(FilterParser.NotExprContext ctx) {
        if (ctx.NOT() != null) {
            visit(ctx.notExpr());
            Expr inner = exprStack.pop();
            exprStack.push(new Expr.Not(inner));
        } else {
            visit(ctx.primary());
        }

        return null;
    }

    @Override
    public Void visitPrimary(FilterParser.PrimaryContext ctx) {
        if (ctx.comparison() != null) {
            visit(ctx.comparison());
        } else {
            visit(ctx.expr());
        }

        return null;
    }

    @Override
    public Void visitComparison(FilterParser.ComparisonContext ctx) {
        String field = ctx.IDENTIFIER().getText();

        if (ctx.COMPOP() != null) {
            visit(ctx.literal());

            Value value = valueStack.pop();
            CompOp op = CompOp.fromSymbol(ctx.COMPOP().getText());

            exprStack.push(new Expr.Comparison(field, op, value));
        } else {
            visit(ctx.literalList());

            List<Value> values = listStack.pop();

            exprStack.push(new Expr.InList(field, values));
        }

        return null;
    }

    @Override
    public Void visitLiteralList(FilterParser.LiteralListContext ctx) {
        List<Value> values = new ArrayList<>();

        for (FilterParser.LiteralContext literal : ctx.literal()) {
            visit(literal);
            values.add(valueStack.pop());
        }

        listStack.push(values);
        return null;
    }

    @Override
    public Void visitLiteral(FilterParser.LiteralContext ctx) {
        if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            text = text.substring(1, text.length() - 1);
            valueStack.push(new Value.Str(text));
        } else {
            int number = Integer.parseInt(ctx.NUMBER().getText());
            valueStack.push(new Value.Num(number));
        }

        return null;
    }
}
