package Components.Tables;

import Components.Exceptions.InvalidInputException;
import Components.Security;
import Components.Series;
import Components.Table;
import Components.Trade;
import Components.Exceptions.SymbolInvalidException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SecurityTradesTable extends Table {
    public SecurityTradesTable(List<Trade> trades, Security s) throws SymbolInvalidException, InvalidInputException {
        List<String> header = new ArrayList();
        header.add(s.getSymbol());
        this.addRow(header);
        List<Trade> securityTrades = new ArrayList();
        Iterator var5 = trades.iterator();

        while(var5.hasNext()) {
            Trade t = (Trade)var5.next();
            if (t.getSecurity() == s) {
                securityTrades.add(t);
            }
        }

        securityTrades.sort(Comparator.comparing(Trade::getStartCal));
        List<Series> securitySeries = new ArrayList();
        List<Series> benchmarkSeries = new ArrayList();
        int i = 0;
        Iterator var8 = securityTrades.iterator();

        while(var8.hasNext()) {
            Trade t = (Trade)var8.next();
            this.addTableRight(new TradeTable(t, i++ == 0), 1);
            this.addColumn();
            securitySeries.add(t.getGains());
            benchmarkSeries.add(t.getBenchmarkGains());
        }

        List<String> compoundedHeaders = new ArrayList();
        compoundedHeaders.add(((Trade)trades.get(0)).getBenchmark().getSymbol());
        compoundedHeaders.add(s.getSymbol());
        compoundedHeaders.add("Difference");
        Series compoundedSecuritySeries = new Series(securitySeries);
        Series compoundedBenchmarkSeries = new Series(benchmarkSeries);
        Series compoundedDifferenceSeries = compoundedSecuritySeries.getDiffVs(compoundedBenchmarkSeries);
        List<Series> tableSeries = new ArrayList();
        tableSeries.add(compoundedBenchmarkSeries);
        tableSeries.add(compoundedSecuritySeries);
        tableSeries.add(compoundedDifferenceSeries);
        Table compounded = new TradeTable("Compounded", compoundedHeaders, tableSeries);
        this.addTableRight(compounded, 1);
    }
}
