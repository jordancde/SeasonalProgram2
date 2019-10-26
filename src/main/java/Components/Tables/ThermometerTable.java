package Components.Tables;

import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;
import Components.Security;
import Components.Table;
import Components.Trade;

import java.text.SimpleDateFormat;
import java.util.*;

public class ThermometerTable extends Table {

    public ThermometerTable(List<Security> securities, Security benchmark, Calendar start, Calendar end) throws InvalidInputException, SymbolInvalidException {
        int startYear = start.get(Calendar.YEAR);
        int endYear = end.get(Calendar.YEAR);

        Calendar fullStart = new GregorianCalendar(startYear, 0,1);
        Calendar fullEnd = new GregorianCalendar(endYear+1, 0,1);

        // refresh beforehand so we can check for first full available year
        for(Security s: securities) s.refresh(fullStart, fullEnd);

        for(int i = 0;i < 12; i++){
            addTableBelow(getMonthTable(securities, benchmark, startYear, endYear, i));
            addRow();
        }
    }

    public Table getMonthTable(List<Security> securities, Security benchmark, int startYear, int endYear, int month) throws InvalidInputException, SymbolInvalidException {

        Calendar start = new GregorianCalendar(startYear, month,1);
        Calendar end = new GregorianCalendar(endYear, month,1);
        end.add(Calendar.MONTH, 1);

        String monthString = new SimpleDateFormat("MMM").format(start.getTime());

        ArrayList<String> yearRow = new ArrayList();
        yearRow.add(startYear+"-"+endYear);

        ArrayList<String> headerRow = new ArrayList();
        headerRow.add(monthString);
        headerRow.add("Year Start");
        headerRow.add("Gain % Last Year");
        headerRow.add("Avg");
        headerRow.add("Med");
        headerRow.add("Max");
        headerRow.add("Min");
        headerRow.add("Fq % > 0");
        headerRow.add("Fq % > "+benchmark.getSymbol());
        headerRow.add("");
        for(int i = startYear;i <= endYear; i++) headerRow.add(Integer.toString(i));

        ArrayList<Trade> trades = new ArrayList();
        for(Security s: securities){

            int firstAvailableFullYear = s.getDataStart().get(Calendar.YEAR);
            Calendar startOfYear = new GregorianCalendar(firstAvailableFullYear,0,1);
            if(s.getDataStart().after(startOfYear)){
                firstAvailableFullYear += 1;
            }

            Calendar tradeStart = new GregorianCalendar(firstAvailableFullYear, month,1);
            trades.add(new Trade(true, tradeStart, end, s, benchmark, true));
        }

        // sort by average gain
        Collections.sort(trades,
                Comparator.comparing(o -> {
                    try {
                        return (-1)*o.getGains().getAverage();
                    } catch (SymbolInvalidException e) {
                        e.printStackTrace();
                    } catch (InvalidInputException e) {
                        e.printStackTrace();
                    }
                    return 0.0;
                }));

        Trade benchmarkTrade = new Trade(true, start, end, benchmark, benchmark, true);
        trades.add(benchmarkTrade);

        Table table = new Table();

        table.addRow(yearRow);
        table.addRow(headerRow);

        for(Trade t: trades){
            ArrayList<String> row = new ArrayList();
            row.add(t.getSecurity().getSymbol());

            row.add(Integer.toString(t.getGains().getDates().get(0).get(Calendar.YEAR)));
            row.add(Double.toString(round(t.getGains().getValues().get(t.getGains().getValues().size()-1))));

            row.add(round(t.getGains().getAverage()).toString());
            row.add(round(t.getGains().getMedian()).toString());
            row.add(round(t.getGains().getMax()).toString());
            row.add(round(t.getGains().getMin()).toString());
            row.add(round(t.getGains().getFreqPositive()).toString());
            row.add(round(t.getGains().getFreqPositiveVs(benchmarkTrade.getGains())).toString());

            row.add("");
            int firstAvailableYear = t.getGains().getDates().get(0).get(Calendar.YEAR);
            for(int i = startYear; i < firstAvailableYear; i++) row.add("");
            for(Double d: t.getGains().getValues()) row.add(round(d).toString());

            table.addRow(row);
        }
        return table;
    }
}
