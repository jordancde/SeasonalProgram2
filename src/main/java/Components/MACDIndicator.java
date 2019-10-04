package Components;

import Components.Exceptions.InvalidInputException;
import Components.Exceptions.SymbolInvalidException;
import org.omg.CORBA.DynAnyPackage.Invalid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MACDIndicator extends Indicator {
    int fast;
    int slow;
    int signal;

    public MACDIndicator(String name, Security s, Security benchmark, Calendar start, Calendar end, int fast, int slow, int signal) {
        super(name, s, benchmark, start, end);
        this.fast = fast;
        this.slow = slow;
        this.signal = signal;
    }

    public List<Series> getMACD() throws SymbolInvalidException, InvalidInputException {
        Calendar newStart = (Calendar)this.start.clone();
        newStart.add(5, -(this.signal + Math.max(this.fast, this.slow)) * 4);

        this.benchmark.refresh(newStart, this.end);
        this.security.refresh(newStart, this.end);

        Series slowEMA = new Series("MACD slow EMA", this.security.getCloses().getDates(), this.getEMA(this.security.getCloses().getValues(), this.slow));
        Series fastEMA = new Series("MACD fast EMA", this.security.getCloses().getDates(), this.getEMA(this.security.getCloses().getValues(), this.fast));
        Series MACD = fastEMA.getDiffVs(slowEMA);
        MACD.name = "MACD (" + this.fast + " " + this.slow + ")";
        Series signal = new Series("MACD Signal (" + this.signal + ")", MACD.getDates(), this.getEMA(MACD.getValues(), this.signal));
        Series histogram = MACD.getDiffVs(signal);
        histogram.name = "MACD Histogram";
        List<Series> series = new ArrayList();
        series.add(MACD);
        series.add(signal);
        series.add(histogram);
        return series;
    }
}
