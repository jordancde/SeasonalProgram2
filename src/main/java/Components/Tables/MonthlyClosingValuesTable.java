package Components.Tables;

import Components.Exceptions.InvalidInputException;
import Components.Security;
import Components.Series;
import Components.Table;
import Components.Exceptions.SymbolInvalidException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

public class MonthlyClosingValuesTable extends Table {
    MonthlyClosingValuesTable.Type tableType;

    public MonthlyClosingValuesTable(String title, Security s, Calendar start, Calendar end, boolean addMonthHeaders, boolean addLeftAxis, MonthlyClosingValuesTable.Type type) throws SymbolInvalidException, InvalidInputException {
        this.tableType = type;
        List<String> titleRow = new ArrayList();
        titleRow.add(title);
        int endYear;
        if (addMonthHeaders) {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

            for(endYear = 0; endYear < 12; ++endYear) {
                Calendar c = Calendar.getInstance();
                c.set(2, endYear);
                titleRow.add(monthFormat.format(c.getTime()));
            }
        }

        this.addRow(titleRow);
        int startYear = start.get(1);
        endYear = end.get(1);
        int startMonth = start.get(2);
        int endMonth = end.get(2);
        Calendar securityStart = new GregorianCalendar(startYear, 0, 1);
        Calendar securityEnd = new GregorianCalendar(endYear, 12, 31);

        s.refresh(securityStart, securityEnd);

        for(int i = startYear; i <= endYear; ++i) {
            List<String> row = new ArrayList();
            if (addLeftAxis) {
                row.add(String.valueOf(i));
            } else {
                row.add("");
            }

            for(int month = 0; month < 12; ++month) {
                if ((i != startYear || month >= startMonth) && (i != endYear || month <= endMonth)) {
                    Calendar openDate = new GregorianCalendar(i, month, 1);
                    Calendar closeDate = new GregorianCalendar(i, month, 1);
                    closeDate.add(2, 1);
                    Double val = 0.0D;
                    if (this.tableType == MonthlyClosingValuesTable.Type.LOWS) {
                        val = this.getExtreme(openDate, closeDate, s, false);
                    } else if (this.tableType == MonthlyClosingValuesTable.Type.HIGHS) {
                        val = this.getExtreme(openDate, closeDate, s, true);
                    } else {
                        val = this.getSeries(s).getValue(closeDate);
                    }

                    row.add(this.round(val).toString());
                } else {
                    row.add("");
                }
            }

            this.addRow(row);
        }

    }

    public Double getExtreme(Calendar start, Calendar end, Security security, boolean high) throws SymbolInvalidException {
        Series s = this.getSeries(security);
        s = s.trim(start, end);
        return high ? (Double)Collections.max(s.getValues()) : (Double)Collections.min(s.getValues());
    }

    public Series getSeries(Security s) throws SymbolInvalidException {
        switch(this.tableType) {
            case CLOSES:
                return s.getCloses();
            case LOWS:
                return s.getLows();
            case HIGHS:
                return s.getHighs();
            default:
                return s.getCloses();
        }
    }

    public static enum Type {
        CLOSES,
        LOWS,
        HIGHS;

        private Type() {
        }
    }
}
