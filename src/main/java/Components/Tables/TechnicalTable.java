package Components.Tables;

import Components.CorrelationIndicator;
import Components.Exceptions.InvalidInputException;
import Components.FSOIndicator;
import Components.Indicator;
import Components.MACDIndicator;
import Components.MovingAverageIndicator;
import Components.PPOIndicator;
import Components.Security;
import Components.Series;
import Components.Table;
import Components.TechnicalsManager;
import Components.Exceptions.SymbolInvalidException;
import Components.MovingAverageIndicator.Type;
import Components.TechnicalsManager.Selection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class TechnicalTable extends Table {
    TechnicalsManager manager;
    Security security;
    Security benchmark;
    Calendar start;
    Calendar end;

    public TechnicalTable(TechnicalsManager manager, Security security, Security benchmark, Calendar start, Calendar end) throws SymbolInvalidException, InvalidInputException {
        this.manager = manager;
        this.security = security;
        this.benchmark = benchmark;
        this.start = start;
        this.end = end;
        List<Series> series = this.getSeries();
        if (series.size() != 0) {
            List<String> titleRow = new ArrayList();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
            String var10001 = dateFormat.format(start.getTime());
            titleRow.add("Price performance " + var10001 + " to " + dateFormat.format(end.getTime()));
            this.addRow(titleRow);
            Table contents = new Table();
            new ArrayList();
            List<String> datesColumn = (series.get(0)).trim(start, end).getDateStrings("dd MMM yyyy");
            datesColumn.add(0, "Dates");
            contents.addColumn(datesColumn);
            Iterator var11 = series.iterator();

            while(var11.hasNext()) {
                Series s = (Series)var11.next();
                List<Double> values = s.getValuesRounded();
                List<String> column = new ArrayList();
                column.add(s.getName());
                Iterator var15 = values.iterator();

                while(var15.hasNext()) {
                    Double v = (Double)var15.next();
                    column.add(v.toString());
                }

                contents.addColumn(column);
            }

            this.addTableBelow(contents);
        }
    }

    List<Series> getSeries() throws SymbolInvalidException, InvalidInputException {
        List<Series> series = new ArrayList();
        List<Selection> selections = this.manager.getSelections();
        Iterator var3 = selections.iterator();

        while(var3.hasNext()) {
            Selection s = (Selection)var3.next();
            series.addAll(this.buildSeries(s));
        }

        return series;
    }

    List<Series> buildSeries(Selection selection) throws SymbolInvalidException, InvalidInputException {
        Calendar newStart = (Calendar)this.start.clone();
        newStart.add(Calendar.MONTH, -18);
        this.security.refresh(newStart, this.end);
        this.benchmark.refresh(newStart, this.end);

        List<Series> series = new ArrayList();
        switch(selection.type) {
            case CLOSE:
                series.add(this.security.getCloses().trim(this.start, this.end));
                break;
            case OPEN:
                series.add(this.security.getOpens().trim(this.start, this.end));
                break;
            case HIGH:
                series.add(this.security.getHighs().trim(this.start, this.end));
                break;
            case LOW:
                series.add(this.security.getLows().trim(this.start, this.end));
                break;
            case VOLUME:
                series.add(this.security.getVolumes().trim(this.start, this.end));
                break;
            case PERCENT_GAIN:
                series.add(this.security.getCloses().getCumulativeGains(this.start, this.end).trim(this.start, this.end));
                break;
            case BENCHMARK_PERCENT_GAIN:
                series.add(this.benchmark.getCloses().getCumulativeGains(this.start, this.end).trim(this.start, this.end));
                break;
            case BENCHMARK_CLOSE:
                series.add(this.benchmark.getCloses().trim(this.start, this.end));
                break;
            case REL_STR_VS_BM_PERCENT_GAIN:
                series.add(this.security.getCloses().getCumulativeGains(this.start, this.end).getRelativePerformanceVs(this.benchmark.getCloses().getCumulativeGains(this.start, this.end)).trim(this.start, this.end));
                break;
            case REL_STR_VS_BM_RATIO:
                series.add(this.security.getCloses().trim(this.start, this.end).getRatioVs(this.benchmark.getCloses().trim(this.start, this.end)));
                break;
            case MOV_AVG_SIMP_1:
            case MOV_AVG_SIMP_2:
                Indicator simpMovAvg = new MovingAverageIndicator("Simple Moving Avg (" + selection.parameters.get(0) + ")", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), Type.SIMPLE);
                series.add(simpMovAvg.getValues().trim(this.start, this.end));
                break;
            case MOV_AVG_EMA_1:
            case MOV_AVG_EMA_2:
                Indicator emaMovAvg = new MovingAverageIndicator("EMA Moving Avg (" + selection.parameters.get(0) + ")", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), Type.EMA);
                series.add(emaMovAvg.getValues().trim(this.start, this.end));
                break;
            case MOV_AVG_SIMP_REL_BM_1:
            case MOV_AVG_SIMP_REL_BM_2:
                Indicator simpRelMovAvg = new MovingAverageIndicator("Simple Rel Moving Avg (" + selection.parameters.get(0) + ")", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), Type.SIMPLE_REL);
                series.add(simpRelMovAvg.getValues().trim(this.start, this.end));
                break;
            case MOV_AVG_EMA_REL_BM_1:
            case MOV_AVG_EMA_REL_BM_2:
                Indicator emaRelMovAvg = new MovingAverageIndicator("EMA Rel Moving Avg (" + selection.parameters.get(0) + ")", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), Type.EMA_REL);
                series.add(emaRelMovAvg.getValues().trim(this.start, this.end));
                break;
            case RSI:
                Indicator rsi = new MovingAverageIndicator("RSI (" + selection.parameters.get(0) + ")", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), Type.RSI);
                series.add(rsi.getValues().trim(this.start, this.end));
                break;
            case RSI_REL_BM:
                Indicator rsiRelative = new MovingAverageIndicator("RSI Relative (" + selection.parameters.get(0) + ")", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), Type.RSI_REL);
                series.add(rsiRelative.getValues().trim(this.start, this.end));
                break;
            case FSO:
                FSOIndicator fso = new FSOIndicator("FSO", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), (Integer)selection.parameters.get(1), (Integer)selection.parameters.get(2));
                Iterator var14 = fso.getFSO().iterator();

                while(var14.hasNext()) {
                    Series s = (Series)var14.next();
                    series.add(s.trim(this.start, this.end));
                }
            case FSO_REL_BM:
            default:
                break;
            case MACD:
                MACDIndicator macd = new MACDIndicator("MACD", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), (Integer)selection.parameters.get(1), (Integer)selection.parameters.get(2));
                Iterator var15 = macd.getMACD().iterator();

                while(var15.hasNext()) {
                    Series s = (Series)var15.next();
                    series.add(s.trim(this.start, this.end));
                }

                return series;
            case PPO:
                PPOIndicator ppo = new PPOIndicator("PPO", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0), (Integer)selection.parameters.get(1), (Integer)selection.parameters.get(2));
                Iterator var17 = ppo.getPPO().iterator();

                while(var17.hasNext()) {
                    Series s = (Series)var17.next();
                    series.add(s.trim(this.start, this.end));
                }

                return series;
            case CORREL:
                Indicator corr = new CorrelationIndicator("Correlation", this.security, this.benchmark, this.start, this.end, (Integer)selection.parameters.get(0));
                series.add(corr.getValues().trim(this.start, this.end));
        }

        return series;
    }
}
