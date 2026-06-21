package filter;

import filter.app.FilterApp;
import filter.builder.AstBuilderPattern;
import filter.builder.AstBuilderVisitor;
import filter.builder.AstBuilders;
import filter.eval.Evaluator;
import filter.nodes.CompOp;
import filter.nodes.Expr;
import filter.nodes.Value;
import filter.printer.AstPrinter;
import filter.model.MediaItem;

public class Main {

  static void main() {

    // ---------- Parsing ----------
    var query =
        """
        genre in ("rock", "jazz") or year <= 1990 and not artist == "Beatles"
        """;
    var ast1 = AstBuilders.fromQuery(query, new AstBuilderPattern()::translate);
    var ast2 = AstBuilders.fromQuery(query, new AstBuilderVisitor()::translate);
    IO.println(AstPrinter.toString(ast1));
    IO.println(AstPrinter.toString(ast2));

    // ---------- data as in songlist.txt ----------
    var items = MediaItem.loadFromResource("songlist.txt");
    var matching = items.stream().filter(i -> Evaluator.matches(i, ast1)).toList();
    matching.forEach(IO::println);

    // ---------- AST ----------
    // artist == "Beatles" and year == 1965
    var expr =
        new Expr.And(
            new Expr.Comparison("artist", CompOp.EQ, new Value.Str("Beatles")),
            new Expr.Comparison("year", CompOp.GE, new Value.Num(1965)));

    IO.println(AstPrinter.toString(expr));

    // ---------- start demo app ----------
    FilterApp.run();
  }
}
