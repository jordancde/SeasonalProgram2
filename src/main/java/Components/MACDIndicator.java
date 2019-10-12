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

        Series slowEMA = this.getEMA(this.security.getCloses(), start, end, this.slow);
        slowEMA.name = "MACD slow EMA";

        Series fastEMA = this.getEMA(this.security.getCloses(), start, end, this.fast);
        fastEMA.name = "MACD fast EMA";

        Series MACD = fastEMA.getDiffVs(slowEMA);
        MACD.name = "MACD (" + this.fast + " " + this.slow + ")";

        Series signal = this.getEMA(MACD, start, end, this.signal);
        signal.name = "MACD Signal (" + this.signal + ")";

        Series histogram = MACD.getDiffVs(signal);
        histogram.name = "MACD Histogram";

        List<Series> series = new ArrayList();
        series.add(MACD);
        series.add(signal);
        series.add(histogram);
        return series;
    }
}
