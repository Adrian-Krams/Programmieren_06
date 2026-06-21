package filter.ast;

import filter.builder.AstBuilderPattern;
import filter.builder.AstBuilderVisitor;
import filter.builder.AstBuilders;
import filter.printer.AstPrinter;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

public class ApprovalTest {

    @Test
    void approveSimpleComparisonWithPatternBuilder() {
        var expr =
            AstBuilders.fromQuery(
                "artist == \"Beatles\"",
                new AstBuilderPattern()::translate);

        Approvals.verify(AstPrinter.toString(expr));
    }

    @Test
    void approveAndExpressionWithPatternBuilder() {
        var expr =
            AstBuilders.fromQuery(
                "artist == \"Beatles\" and year == 1965",
                new AstBuilderPattern()::translate);

        Approvals.verify(AstPrinter.toString(expr));
    }

    @Test
    void approveComplexExpressionWithPatternBuilder() {
        var expr =
            AstBuilders.fromQuery(
                "genre in (\"rock\", \"jazz\") or year <= 1990 and not artist == \"Beatles\"",
                new AstBuilderPattern()::translate);

        Approvals.verify(AstPrinter.toString(expr));
    }

    @Test
    void approveVisitorAndPatternBuilderAreEqual() {
        String query =
            "genre in (\"rock\", \"jazz\") or year <= 1990 and not artist == \"Beatles\"";

        var patternExpr =
            AstBuilders.fromQuery(
                query,
                new AstBuilderPattern()::translate);

        var visitorExpr =
            AstBuilders.fromQuery(
                query,
                new AstBuilderVisitor()::translate);

        String result =
            "Pattern:\n"
                + AstPrinter.toString(patternExpr)
                + "\n\nVisitor:\n"
                + AstPrinter.toString(visitorExpr);

        Approvals.verify(result);
    }
}
