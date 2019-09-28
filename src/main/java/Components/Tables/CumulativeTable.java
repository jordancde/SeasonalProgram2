package Components.Tables;

import Components.Security;
import Components.Table;
import Components.Exceptions.SymbolInvalidException;
import java.util.Calendar;

public class CumulativeTable extends Table {
    public CumulativeTable(String title, Security s, Security benchmark, Calendar start, Calendar end) throws SymbolInvalidException {
        Table averagesTable = new CumulativeAveragesTable("Cumulative Gains", s, benchmark, start, end);
        Table securityGainsTable = new CumulativeGainsTable(s.getSymbol() + " Cumulative Gains", s, start, end);
        Table benchmarkGainsTable = new CumulativeGainsTable(benchmark.getSymbol() + " Cumulative Gains", benchmark, start, end);
        this.addTableRight(averagesTable, 0);
        this.addColumn();
        this.addTableRight(securityGainsTable, 0);
        this.addColumn();
        this.addTableRight(benchmarkGainsTable, 0);
    }
}
