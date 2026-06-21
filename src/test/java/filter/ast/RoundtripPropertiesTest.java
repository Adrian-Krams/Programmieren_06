package filter.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import filter.builder.AstBuilderPattern;
import filter.builder.AstBuilderVisitor;
import filter.builder.AstBuilders;
import filter.printer.AstPrinter;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class RoundtripPropertiesTest {

    @Property
    boolean patternBuilderRoundtrip(@ForAll("simpleQueries") String query) {
        var first =
            AstBuilders.fromQuery(
                query,
                new AstBuilderPattern()::translate);

        String printed = AstPrinter.toString(first);

        var second =
            AstBuilders.fromQuery(
                printed,
                new AstBuilderPattern()::translate);

        assertEquals(
            AstPrinter.toString(first),
            AstPrinter.toString(second));

        return true;
    }

    @Property
    boolean visitorBuilderRoundtrip(@ForAll("simpleQueries") String query) {
        var first =
            AstBuilders.fromQuery(
                query,
                new AstBuilderVisitor()::translate);

        String printed = AstPrinter.toString(first);

        var second =
            AstBuilders.fromQuery(
                printed,
                new AstBuilderVisitor()::translate);

        assertEquals(
            AstPrinter.toString(first),
            AstPrinter.toString(second));

        return true;
    }

    @Property
    boolean patternAndVisitorProduceSameAst(@ForAll("simpleQueries") String query) {
        var pattern =
            AstBuilders.fromQuery(
                query,
                new AstBuilderPattern()::translate);

        var visitor =
            AstBuilders.fromQuery(
                query,
                new AstBuilderVisitor()::translate);

        assertEquals(
            AstPrinter.toString(pattern),
            AstPrinter.toString(visitor));

        return true;
    }

    @Provide
    Arbitrary<String> simpleQueries() {
        return Arbitraries.of(
            "artist == \"Beatles\"",
            "year == 1965",
            "year <= 1990",
            "artist != \"Queen\"",
            "genre in (\"rock\", \"jazz\")",
            "artist == \"Beatles\" and year == 1965",
            "artist == \"Beatles\" or artist == \"Queen\"",
            "not artist == \"Beatles\"",
            "(artist == \"Beatles\" or artist == \"Queen\") and year > 1960"
        );
    }
}
