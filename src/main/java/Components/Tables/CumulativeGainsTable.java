package Components.Tables;

import Components.Exceptions.InvalidInputException;
import Components.Security;
import Components.Series;
import Components.Table;
import Components.Exceptions.SymbolInvalidException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class CumulativeGainsTable extends Table {
    public CumulativeGainsTable(String title, Security s, Calendar start, Calendar end) throws SymbolInvalidException, InvalidInputException {

        s.refresh(start, end);

        int startYear = start.get(1);
        int endYear = end.get(1);
        int startMonth = start.get(2);
        int endMonth = end.get(2);
        int startDay = start.get(5);
        int endDay = end.get(5);
        ArrayList<String> titleRow = new ArrayList();
        titleRow.add(title);

        for(int i = startYear; i <= endYear; ++i) {
            titleRow.add(String.valueOf(i));
        }

        this.addRow(titleRow);
        Table content = new Table();

        for(int i = startYear; i <= endYear; ++i) {
            GregorianCalendar yearStart = new GregorianCalendar(i, startMonth, startDay);
            GregorianCalendar yearEnd = new GregorianCalendar(i, endMonth, endDay);
            if (yearStart.compareTo(yearEnd) >= 0) {
                yearEnd.add(1, 1);
            }

            Series cumulativeGains = s.getCloses().getCumulativeGains(yearStart, yearEnd);
            List<Calendar> fullDates = new ArrayList();
            ArrayList fullValues = new ArrayList();

            while(yearStart.compareTo(yearEnd) <= 0) {
                // added fullValues size check for the case when we start on march 1st
                // this leap year logic only applies when we pass over feb 29th
                if (fullValues.size() > 0 && !yearStart.isLeapYear(yearStart.get(1)) && yearStart.get(2) == 2 && yearStart.get(5) == 1) {
                    fullDates.add(new GregorianCalendar(2000, 1, 29));
                    fullValues.add((Double)fullValues.get(fullValues.size() - 1));
                }

                Double result = cumulativeGains.getCurrentValue(yearStart);
                fullDates.add((Calendar)yearStart.clone());
                fullValues.add(result);
                yearStart.add(5, 1);
            }

            Series fullCumulative = new Series("", fullDates, fullValues);
            if (i == startYear) {
                content.addColumn(fullCumulative.getDateStrings("dd-MMM"));
            }

            content.addColumn(fullCumulative.getValuesRounded());
        }

        this.addTableBelow(content);
    }
}
