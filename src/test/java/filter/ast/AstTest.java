package filter.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import filter.builder.AstBuilderPattern;
import filter.builder.AstBuilderVisitor;
import filter.builder.AstBuilders;
import filter.printer.AstPrinter;
import org.junit.jupiter.api.Test;

public class AstTest {

    @Test
    void simpleComparisonPattern() {

        var expr =
            AstBuilders.fromQuery(
                "artist == \"Beatles\"",
                new AstBuilderPattern()::translate);

        String result = new AstPrinter().toString(expr);

        assertEquals(
            "(artist == \"Beatles\")",
            result);
    }

    @Test
    void simpleComparisonVisitor() {

        var expr =
            AstBuilders.fromQuery(
                "artist == \"Beatles\"",
                new AstBuilderVisitor()::translate);

        String result = new AstPrinter().toString(expr);

        assertEquals(
            "(artist == \"Beatles\")",
            result);
    }
}
