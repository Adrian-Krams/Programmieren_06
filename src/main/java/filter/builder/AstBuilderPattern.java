package filter.builder;

import filter.FilterParser;
import filter.nodes.CompOp;
import filter.nodes.Expr;
import filter.nodes.Value;
import java.util.List;

public class AstBuilderPattern {

    public Expr translate(FilterParser.QueryContext ctx) {
        return buildExpr(ctx.expr());
    }

    private Expr buildExpr(FilterParser.ExprContext ctx) {
        return buildOrExpr(ctx.orExpr());
    }

    private Expr buildOrExpr(FilterParser.OrExprContext ctx) {
        Expr result = buildAndExpr(ctx.andExpr(0));

        for (int i = 1; i < ctx.andExpr().size(); i++) {
            result = new Expr.Or(result, buildAndExpr(ctx.andExpr(i)));
        }

        return result;
    }

    private Expr buildAndExpr(FilterParser.AndExprContext ctx) {
        Expr result = buildNotExpr(ctx.notExpr(0));

        for (int i = 1; i < ctx.notExpr().size(); i++) {
            result = new Expr.And(result, buildNotExpr(ctx.notExpr(i)));
        }

        return result;
    }

    private Expr buildNotExpr(FilterParser.NotExprContext ctx) {
        if (ctx.NOT() != null) {
            return new Expr.Not(buildNotExpr(ctx.notExpr()));
        }

        return buildPrimary(ctx.primary());
    }

    private Expr buildPrimary(FilterParser.PrimaryContext ctx) {
        if (ctx.comparison() != null) {
            return buildComparison(ctx.comparison());
        }

        return buildExpr(ctx.expr());
    }

    private Expr buildComparison(FilterParser.ComparisonContext ctx) {
        String field = ctx.IDENTIFIER().getText();

        if (ctx.COMPOP() != null) {
            CompOp op = CompOp.fromSymbol(ctx.COMPOP().getText());
            Value value = buildLiteral(ctx.literal());
            return new Expr.Comparison(field, op, value);
        }

        return new Expr.InList(field, buildLiteralList(ctx.literalList()));
    }

    private List<Value> buildLiteralList(FilterParser.LiteralListContext ctx) {
        return ctx.literal()
            .stream()
            .map(this::buildLiteral)
            .toList();
    }

    private Value buildLiteral(FilterParser.LiteralContext ctx) {
        if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            text = text.substring(1, text.length() - 1);
            return new Value.Str(text);
        }

        int number = Integer.parseInt(ctx.NUMBER().getText());
        return new Value.Num(number);
    }
}
