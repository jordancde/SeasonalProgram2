package Components.Tables;

import Components.Exceptions.InvalidInputException;
import Components.Security;
import Components.Series;
import Components.Table;
import Components.Exceptions.SymbolInvalidException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class CumulativeAveragesTable extends Table {
    public CumulativeAveragesTable(String title, Security s, Security benchmark, Calendar start, Calendar end) throws SymbolInvalidException, InvalidInputException {

        s.refresh(start, end);
        benchmark.refresh(start, end);

        int startYear = start.get(1);
        int endYear = end.get(1);
        int startMonth = start.get(2);
        int endMonth = end.get(2);
        int startDay = start.get(5);
        int endDay = end.get(5);
        ArrayList<String> titleRow = new ArrayList();
        titleRow.add(startYear + " to " + endYear);
        titleRow.add(s.getSymbol() + " Average");
        titleRow.add(benchmark.getSymbol() + " Average");
        titleRow.add("Relative Performance");
        this.addRow(titleRow);
        Map<String, Pair<Double, Integer>> securitySums = new HashMap();
        Map<String, Pair<Double, Integer>> benchmarkSums = new HashMap();
        Table content = new Table();

        int var10000;
        String dateString;
        for(int i = startYear; i <= endYear; ++i) {
            GregorianCalendar yearStart = new GregorianCalendar(i, startMonth, startDay);
            GregorianCalendar yearEnd = new GregorianCalendar(i, endMonth, endDay);
            if (yearStart.compareTo(yearEnd) >= 0) {
                yearEnd.add(1, 1);
            }

            Series cumulativeGains = s.getCloses().getCumulativeGains(yearStart, yearEnd);
            Series cumulativeBenchmark = benchmark.getCloses().getCumulativeGains(yearStart, yearEnd);

            while(yearStart.compareTo(yearEnd) <= 0) {
                var10000 = yearStart.get(2);
                dateString = var10000 + "/" + yearStart.get(5);
                Double securityResult = cumulativeGains.getCurrentValue(yearStart);
                Double benchmarkResult = cumulativeBenchmark.getCurrentValue(yearStart);
                Pair<Double, Integer> previousSecurityPair = (Pair)securitySums.getOrDefault(dateString, new Pair(0.0D, 0));
                Pair<Double, Integer> newSecurityPair = new Pair((Double)previousSecurityPair.getKey() + securityResult, (Integer)previousSecurityPair.getValue() + 1);
                Pair<Double, Integer> previousBenchmarkPair = (Pair)benchmarkSums.getOrDefault(dateString, new Pair(0.0D, 0));
                Pair<Double, Integer> newBenchmarkPair = new Pair((Double)previousBenchmarkPair.getKey() + benchmarkResult, (Integer)previousBenchmarkPair.getValue() + 1);
                securitySums.put(dateString, newSecurityPair);
                benchmarkSums.put(dateString, newBenchmarkPair);
                yearStart.add(5, 1);
            }
        }

        List<Calendar> dates = new ArrayList();
        List<Double> securityAverages = new ArrayList();
        List<Double> benchmarkAverages = new ArrayList();
        GregorianCalendar yearStart = new GregorianCalendar(startYear, startMonth, startDay);
        GregorianCalendar yearEnd = new GregorianCalendar(startYear, endMonth, endDay);
        if (yearStart.compareTo(yearEnd) >= 0) {
            yearEnd.add(1, 1);
        }

        while(yearStart.compareTo(yearEnd) <= 0) {
            if (!yearStart.isLeapYear(yearStart.get(1)) && yearStart.get(2) == 2 && yearStart.get(5) == 1) {
                dates.add(new GregorianCalendar(2000, 1, 29));
                securityAverages.add((Double)securityAverages.get(securityAverages.size() - 1));
                benchmarkAverages.add((Double)benchmarkAverages.get(benchmarkAverages.size() - 1));
            }

            var10000 = yearStart.get(2);
            dateString = var10000 + "/" + yearStart.get(5);
            dates.add((Calendar)yearStart.clone());
            securityAverages.add((Double)((Pair)securitySums.get(dateString)).getKey() / (double)(Integer)((Pair)securitySums.get(dateString)).getValue());
            benchmarkAverages.add((Double)((Pair)benchmarkSums.get(dateString)).getKey() / (double)(Integer)((Pair)benchmarkSums.get(dateString)).getValue());
            yearStart.add(5, 1);
        }

        Series securitySeries = new Series("", dates, securityAverages);
        Series benchmarkSeries = new Series("", dates, benchmarkAverages);
        Series relativePerformance = securitySeries.getRelativePerformanceVs(benchmarkSeries);
        List<String> leftAxis = new ArrayList(securitySeries.getDateStrings("dd-MMM"));
        leftAxis.add("");
        leftAxis.add("Average");
        leftAxis.add("Median");
        leftAxis.add("Max");
        leftAxis.add("Min");
        leftAxis.add("Fq. Pos #");
        leftAxis.add("Fq. Pos %");
        content.addColumn(leftAxis);
        content.addColumn(securitySeries.getValuesWithStats());
        content.addColumn(benchmarkSeries.getValuesWithStats());
        content.addColumn(relativePerformance.getValuesWithStats());
        this.addTableBelow(content);
    }
}
