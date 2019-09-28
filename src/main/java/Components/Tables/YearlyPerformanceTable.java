package Components.Tables;

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
import java.util.Locale;

public class YearlyPerformanceTable extends Table {
    public YearlyPerformanceTable(List<Security> securities, Security benchmark, Calendar startDate, Calendar endDate) throws SymbolInvalidException {
        int startYear = startDate.get(1);
        int endYear = endDate.get(1);
        List<String> titleRow = new ArrayList();
        titleRow.add("Sectors Performance");
        titleRow.add(endYear + " % Gain");
        titleRow.add(startYear + "-" + endYear + " Average % Gain");
        titleRow.add(startYear + "-" + endYear + " Fq % Positive");
        titleRow.add(startYear + "-" + endYear + " Fq % Positive > " + benchmark.getSymbol());
        this.addRow(titleRow);

        ArrayList row;
        for(Iterator var8 = securities.iterator(); var8.hasNext(); this.addRow(row)) {
            Security s = (Security)var8.next();
            row = new ArrayList();
            row.add(s.getSymbol());
            Calendar start = new GregorianCalendar(startYear, 0, 1);
            Calendar end = new GregorianCalendar(endYear + 1, 0, 1);
            Trade t = new Trade(true, start, end, s, benchmark);
            Series gains = t.getGains();
            Series gainsVsBenchmark = t.getDiffGains();
            row.add(this.round((Double)gains.getValues().get(gains.getValues().size() - 1)).toString());
            row.add(this.round(gains.getAverage()).toString());
            row.add(this.round(gains.getFreqPositive()).toString());
            row.add(this.round(gainsVsBenchmark.getFreqPositive()).toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            if (t.getNumPeriods() < endYear - startYear + 1) {
                if (s.getCloses().getDates().size() == 0) {
                    row.add("* No data available");
                } else {
                    String var10001 = sdf.format(((Calendar)s.getCloses().getDates().get(0)).getTime());
                    row.add("* Some data unavailable, starting " + var10001);
                }
            }
        }

    }
}
