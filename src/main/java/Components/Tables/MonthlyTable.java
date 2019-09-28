package Components.Tables;

import Components.Security;
import Components.Table;
import Components.Exceptions.SymbolInvalidException;
import java.util.Calendar;

public class MonthlyTable extends Table {
    public MonthlyTable(String title, Security s, Security benchmark, Calendar start, Calendar end, MonthlyTable.Type type) throws SymbolInvalidException {
        String var10002 = s.getSymbol();
        MonthlyGainsTable gainsTable = new MonthlyGainsTable(var10002 + " Monthly % Gains", Components.Tables.MonthlyGainsTable.Type.GAINS, s, benchmark, start, end, true, true);
        var10002 = benchmark.getSymbol();
        MonthlyGainsTable benchmarkGainsTable = new MonthlyGainsTable(var10002 + " Monthly % Gains", Components.Tables.MonthlyGainsTable.Type.BENCHMARK_GAINS, s, benchmark, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL);
        var10002 = s.getSymbol();
        MonthlyGainsTable differenceGainsTable = new MonthlyGainsTable(var10002 + "-" + benchmark.getSymbol() + " Difference % Gains", Components.Tables.MonthlyGainsTable.Type.DIFFERENCE_GAINS, s, benchmark, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL);
        var10002 = s.getSymbol();
        MonthlyClosingValuesTable closesTable = new MonthlyClosingValuesTable(var10002 + " Monthly Closes", s, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL, Components.Tables.MonthlyClosingValuesTable.Type.CLOSES);
        var10002 = benchmark.getSymbol();
        MonthlyClosingValuesTable benchmarkClosesTable = new MonthlyClosingValuesTable(var10002 + " Monthly Closes", benchmark, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL, Components.Tables.MonthlyClosingValuesTable.Type.CLOSES);
        var10002 = s.getSymbol();
        MonthlyClosingValuesTable highsTable = new MonthlyClosingValuesTable(var10002 + " Monthly Highs", s, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL, Components.Tables.MonthlyClosingValuesTable.Type.HIGHS);
        var10002 = benchmark.getSymbol();
        MonthlyClosingValuesTable benchmarkHighsTable = new MonthlyClosingValuesTable(var10002 + " Monthly Highs", benchmark, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL, Components.Tables.MonthlyClosingValuesTable.Type.HIGHS);
        var10002 = s.getSymbol();
        MonthlyClosingValuesTable lowsTable = new MonthlyClosingValuesTable(var10002 + " Monthly Lows", s, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL, Components.Tables.MonthlyClosingValuesTable.Type.LOWS);
        var10002 = benchmark.getSymbol();
        MonthlyClosingValuesTable benchmarkLowsTable = new MonthlyClosingValuesTable(var10002 + " Monthly Lows", benchmark, start, end, type == MonthlyTable.Type.HORIZONTAL, type == MonthlyTable.Type.VERTICAL, Components.Tables.MonthlyClosingValuesTable.Type.LOWS);
        switch(type) {
            case HORIZONTAL:
                this.addTableRight(gainsTable, 0);
                this.addColumn();
                this.addTableRight(benchmarkGainsTable, 0);
                this.addColumn();
                this.addTableRight(differenceGainsTable, 0);
                this.addColumn();
                this.addTableRight(closesTable, 0);
                this.addColumn();
                this.addTableRight(benchmarkClosesTable, 0);
                this.addColumn();
                this.addTableRight(highsTable, 0);
                this.addColumn();
                this.addTableRight(benchmarkHighsTable, 0);
                this.addColumn();
                this.addTableRight(lowsTable, 0);
                this.addColumn();
                this.addTableRight(benchmarkLowsTable, 0);
                break;
            case VERTICAL:
                this.addTableBelow(gainsTable);
                this.addRow();
                this.addTableBelow(benchmarkGainsTable);
                this.addRow();
                this.addTableBelow(differenceGainsTable);
                this.addRow();
                this.addTableBelow(closesTable);
                this.addRow();
                this.addTableBelow(benchmarkClosesTable);
                this.addRow();
                this.addTableBelow(highsTable);
                this.addRow();
                this.addTableBelow(benchmarkHighsTable);
                this.addRow();
                this.addTableBelow(lowsTable);
                this.addRow();
                this.addTableBelow(benchmarkLowsTable);
        }

    }

    public static enum Type {
        VERTICAL,
        HORIZONTAL;

        private Type() {
        }
    }
}
