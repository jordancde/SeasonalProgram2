package Components.Tables;

import Components.Series;
import Components.Table;
import Components.Trade;
import Components.Exceptions.SymbolInvalidException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TradeTable extends Table {
    public TradeTable(Trade t, boolean showRowTitles) throws SymbolInvalidException {
        List<String> header = new ArrayList();
        if (showRowTitles) {
            header.add("");
        }

        header.add(t.getTradeWindow("MMMdd"));
        header.add(t.isLong() ? "Long" : "Short");
        this.addRow(header);
        List<String> columnTitles = new ArrayList();
        if (showRowTitles) {
            columnTitles.add("Year");
        }

        columnTitles.add(t.getBenchmark().getSymbol());
        columnTitles.add(t.getSecurity().getSymbol());
        columnTitles.add("Difference");
        this.addRow(columnTitles);
        Table content = new Table();
        Series gains = t.getGains();
        Series benchmarkGains = t.getBenchmarkGains();
        new ArrayList();
        new ArrayList();
        new ArrayList();
        new ArrayList();
        List<String> yearsColumn = gains.getDateStrings("yyyy");
        yearsColumn.add("");
        yearsColumn.add("Avg");
        yearsColumn.add("Med");
        yearsColumn.add("Max");
        yearsColumn.add("Min");
        yearsColumn.add("Fq Pos #");
        yearsColumn.add("Fq Pos %");
        if (showRowTitles) {
            content.addColumn(yearsColumn);
        }

        content.addColumn(benchmarkGains.getValuesWithStats());
        content.addColumn(gains.getValuesWithStats());
        content.addColumn(gains.getDiffVs(benchmarkGains).getValuesWithStats());
        this.addTableBelow(content);
    }

    public TradeTable(String title, List<String> columnHeaders, List<Series> series) {
        List<String> titleRow = new ArrayList();
        titleRow.add(title);
        this.addRow(titleRow);
        this.addRow(columnHeaders);
        Table content = new Table();
        Iterator var6 = series.iterator();

        while(var6.hasNext()) {
            Series s = (Series)var6.next();
            content.addColumn(s.getValuesWithStats());
        }

        this.addTableBelow(content);
    }
}
