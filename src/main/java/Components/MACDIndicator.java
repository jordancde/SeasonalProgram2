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

        Series slowEMA = getEMA(security.getCloses(), slow);
        slowEMA.name = "MACD slow EMA";

        Series fastEMA = getEMA(security.getCloses(), fast);
        fastEMA.name = "MACD fast EMA";

        Series MACD = fastEMA.getDiffVs(slowEMA);
        MACD.name = "MACD (" + fast + " " + slow + ")";

        Series signalSeries = getEMA(MACD, signal);
        signalSeries.name = "MACD Signal (" + signal + ")";

        Series histogram = MACD.getDiffVs(signalSeries);
        histogram.name = "MACD Histogram";

        List<Series> series = new ArrayList();
        series.add(MACD.trim(start, end));
        series.add(signalSeries.trim(start, end));
        series.add(histogram.trim(start, end));
        return series;
    }
}
