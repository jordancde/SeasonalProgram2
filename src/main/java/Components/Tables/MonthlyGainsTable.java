package Components.Tables;

import Components.Exceptions.InvalidInputException;
import Components.Security;
import Components.Series;
import Components.Table;
import Components.Trade;
import Components.Exceptions.SymbolInvalidException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class MonthlyGainsTable extends Table {
    public MonthlyGainsTable.Type tableType;
    public Security security;
    public Security benchmark;

    public MonthlyGainsTable(String title, MonthlyGainsTable.Type type, Security s, Security benchmark, Calendar start, Calendar end, boolean addMonthHeaders, boolean addLeftAxis) throws SymbolInvalidException, InvalidInputException {
        this.tableType = type;
        this.security = s;
        this.benchmark = benchmark;
        int startYear = start.get(1);
        int endYear = end.get(1);
        int startMonth = start.get(2);
        int endMonth = end.get(2);
        Calendar periodStart = new GregorianCalendar(startYear, startMonth, 1);
        Calendar periodEnd = new GregorianCalendar(endYear, endMonth, 1);
        periodEnd.add(2, 1);

        s.refresh(periodStart, periodEnd);
        benchmark.refresh(periodStart, periodEnd);

        List<String> titleRow = new ArrayList();
        titleRow.add(title);
        if (addMonthHeaders) {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

            for(int i = 0; i < 12; ++i) {
                Calendar c = Calendar.getInstance();
                c.set(2, i);
                titleRow.add(monthFormat.format(c.getTime()));
            }

            titleRow.add("");
            titleRow.add("Yearly Gain");
            titleRow.add("Average");
        }

        this.addRow(titleRow);
        Table content = new Table();
        ArrayList yearlySums;

        if (addLeftAxis) {
            yearlySums = new ArrayList();
            yearlySums.add("Average");
            yearlySums.add("Median");
            yearlySums.add("Max");
            yearlySums.add("Min");
            yearlySums.add("Fq. Pos #");
            yearlySums.add("Fq. Pos %");
            yearlySums.add("");

            for(int i = startYear; i <= endYear; ++i) {
                yearlySums.add(String.valueOf(i));
            }

            content.addColumn(yearlySums);
        } else {
            content.addColumn();
        }

        yearlySums = new ArrayList();

        for(int i = startYear; i <= endYear; ++i) {
            yearlySums.add(0.0D);
        }

        ArrayList<Integer> yearlyCounts = new ArrayList();

        for(int i = startYear; i <= endYear; ++i) {
            yearlyCounts.add(0);
        }

        GregorianCalendar yearStart;
        for(int i = 0; i < 12; ++i) {
            Calendar tradeStart = new GregorianCalendar(startYear, i, 1);
            yearStart = new GregorianCalendar(endYear, i, 1);
            yearStart.add(2, 1);
            ArrayList<String> column = new ArrayList();
            if (i < startMonth) {
                tradeStart.add(1, 1);
            }

            if (i > endMonth) {
                yearStart.add(1, -1);
            }

            Trade trade = new Trade(true, tradeStart, yearStart, s, benchmark);
            if (i < startMonth) {
                column.add("");
            }

            Series gains;
            if (tradeStart.compareTo(yearStart) <= 0) {
                gains = this.getGains(trade);
            } else {
                gains = new Series();
            }

            List<Double> gainsValues = gains.getValuesRounded();
            Iterator var26 = gainsValues.iterator();

            while(var26.hasNext()) {
                Double d = (Double)var26.next();
                column.add(d.toString());
            }

            if (i > endMonth) {
                column.add("");
            }

            int valuesIndex = 0;

            for(int j = 0; j < yearlySums.size(); ++j) {
                if ((j != 0 || i >= startMonth) && (j != yearlySums.size() - 1 || i <= endMonth)) {
                    yearlySums.set(j, (Double)yearlySums.get(j) + (Double)gains.getValues().get(valuesIndex++));
                    yearlyCounts.set(j, (Integer)yearlyCounts.get(j) + 1);
                }
            }

            List<Double> stats = gains.getStats();
            List<String> stringStats = new ArrayList();
            Iterator var29 = stats.iterator();

            while(var29.hasNext()) {
                Double d = (Double)var29.next();
                stringStats.add(this.round(d).toString());
            }

            stringStats.add("");
            column.addAll(0, stringStats);
            content.addColumn(column);
        }

        content.addColumn();
        List<String> yearlyGains = new ArrayList();
        for(int i = 0;i<7;i++) yearlyGains.add("");

        for(int i = startYear; i <= endYear; ++i) {
            yearStart = new GregorianCalendar(i, 0, 1);
            Calendar yearEnd = new GregorianCalendar(i + 1, 0, 1);
            if (i == startYear) {
                yearStart.set(2, startMonth);
            }

            if (i == endYear) {
                yearEnd.set(1, i);
                yearEnd.set(2, endMonth);
                yearEnd.add(2, 1);
            }

            yearlyGains.add(this.round(this.getGains(yearStart, yearEnd)).toString());
        }

        content.addColumn(yearlyGains);
        List<String> averages = new ArrayList();
        for(int i = 0;i<7;i++) averages.add("");

        for(int i = 0; i < yearlySums.size(); ++i) {
            Double average = (Double)yearlySums.get(i) / (double)(Integer)yearlyCounts.get(i);
            averages.add(this.round(average).toString());
        }

        content.addColumn(averages);
        this.addTableBelow(content);
    }

    public Series getGains(Trade t) throws SymbolInvalidException, InvalidInputException {
        switch(this.tableType) {
            case GAINS:
                return t.getGains();
            case BENCHMARK_GAINS:
                return t.getBenchmarkGains();
            case DIFFERENCE_GAINS:
                return t.getDiffGains();
            default:
                return t.getGains();
        }
    }

    public Double getGains(Calendar from, Calendar to) {
        switch(this.tableType) {
            case GAINS:
                return this.security.getCloses().getValue(to) / this.security.getCloses().getValue(from) - 1.0D;
            case BENCHMARK_GAINS:
                return this.benchmark.getCloses().getValue(to) / this.benchmark.getCloses().getValue(from) - 1.0D;
            case DIFFERENCE_GAINS:
                return this.security.getCloses().getValue(to) / this.security.getCloses().getValue(from) - this.benchmark.getCloses().getValue(to) / this.benchmark.getCloses().getValue(from);
            default:
                return this.security.getCloses().getValue(to) / this.security.getCloses().getValue(from) - 1.0D;
        }
    }

    public static enum Type {
        GAINS,
        BENCHMARK_GAINS,
        DIFFERENCE_GAINS;

        private Type() {
        }
    }
}
